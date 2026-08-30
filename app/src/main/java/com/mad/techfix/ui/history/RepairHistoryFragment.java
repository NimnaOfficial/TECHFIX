package com.mad.techfix.ui.history;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.mad.techfix.R;
import com.mad.techfix.data.local.database.AppDatabase;
import com.mad.techfix.data.local.database.RepairHistoryEntity;
import com.mad.techfix.models.ApiResponse;
import com.mad.techfix.models.Appointment;
import com.mad.techfix.network.ApiService;
import com.mad.techfix.network.RetrofitClient;
import com.mad.techfix.utils.TokenManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RepairHistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private RepairHistoryAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ApiService apiService;
    private TokenManager tokenManager;
    private AppDatabase db;

    private static final String TAG = "HISTORY_DEBUG";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_repair_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        recyclerView = view.findViewById(R.id.rvHistory);
        progressBar = view.findViewById(R.id.progress_bar);
        tvEmpty = view.findViewById(R.id.tv_empty);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);

        // Setup RecyclerView with click listener
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RepairHistoryAdapter(this::navigateToDetail); // <-- PASS CLICK LISTENER
        recyclerView.setAdapter(adapter);

        // Initialize helpers
        apiService = RetrofitClient.getClient().create(ApiService.class);
        tokenManager = new TokenManager(requireContext());
        db = AppDatabase.getInstance(requireContext());

        // Pull to refresh
        swipeRefreshLayout.setOnRefreshListener(this::fetchFromApiAndRefresh);

        // Load data (Offline First!)
        loadFromLocalDatabase();
        fetchFromApiAndRefresh();
    }

    // ==========================================
    // NAVIGATE TO DETAIL SCREEN
    // ==========================================
    private void navigateToDetail(RepairHistoryEntity item) {
        RepairHistoryDetailFragment detailFragment = RepairHistoryDetailFragment.newInstance(item.getRepairId());
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, detailFragment)
                .addToBackStack(null)
                .commit();
    }

    // ==========================================
    // LOAD FROM LOCAL DATABASE (OFFLINE FIRST)
    // ==========================================
    private void loadFromLocalDatabase() {
        new Thread(() -> {
            List<RepairHistoryEntity> history = db.techFixDao().getAllHistory();
            requireActivity().runOnUiThread(() -> {
                if (history != null && !history.isEmpty()) {
                    adapter.updateList(history);
                    tvEmpty.setVisibility(View.GONE);
                    Log.d(TAG, "📦 Loaded " + history.size() + " items from local cache");
                } else {
                    tvEmpty.setVisibility(View.VISIBLE);
                    tvEmpty.setText("No repair history found.\nPull down to refresh.");
                    Log.d(TAG, "📦 No cached history found");
                }
            });
        }).start();
    }

    // ==========================================
    // FETCH FROM API AND REFRESH LOCAL DB
    // ==========================================
    private void fetchFromApiAndRefresh() {
        String token = tokenManager.getToken();

        Log.d(TAG, "🔍 Token: " + (token != null ? token.substring(0, Math.min(token.length(), 20)) + "..." : "NULL"));

        if (token == null) {
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(getContext(), "⚠️ Please login first.", Toast.LENGTH_LONG).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setRefreshing(true);
        Log.d(TAG, "📡 Making API call to get appointments...");

        apiService.getAppointments("Bearer " + token).enqueue(new Callback<ApiResponse<List<Appointment>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<Appointment>>> call,
                                   @NonNull Response<ApiResponse<List<Appointment>>> response) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);

                Log.d(TAG, "📡 Response Code: " + response.code());

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Appointment> appointments = response.body().getData();
                    Log.d(TAG, "✅ Received " + (appointments != null ? appointments.size() : 0) + " appointments from API");

                    // Save to Local DB
                    new Thread(() -> {
                        List<RepairHistoryEntity> entityList = new ArrayList<>();
                        if (appointments != null) {
                            for (Appointment apt : appointments) {
                                RepairHistoryEntity entity = new RepairHistoryEntity(
                                        apt.getId(),
                                        apt.getDevice_id() != null ? apt.getDevice_id() : "Unknown Device",
                                        apt.getService_id() != null ? apt.getService_id() : "Unknown Service",
                                        apt.getStatus() != null ? apt.getStatus() : "REQUESTED",
                                        apt.getEstimated_price(),
                                        apt.getCreated_at() != null ? apt.getCreated_at() : String.valueOf(System.currentTimeMillis()),
                                        apt.getBranch_id() != null ? apt.getBranch_id() : "Unknown Branch"
                                );
                                entity.setAppointmentNumber(apt.getAppointment_number());
                                entityList.add(entity);
                            }
                        }

                        // Clear old data and insert new list
                        db.techFixDao().clearAllHistory();
                        if (!entityList.isEmpty()) {
                            db.techFixDao().insertHistoryList(entityList);
                        }

                        // Update UI on main thread
                        requireActivity().runOnUiThread(() -> {
                            if (!entityList.isEmpty()) {
                                adapter.updateList(entityList);
                                tvEmpty.setVisibility(View.GONE);
                                Toast.makeText(getContext(), "✅ Loaded " + entityList.size() + " history items", Toast.LENGTH_SHORT).show();
                            } else {
                                tvEmpty.setVisibility(View.VISIBLE);
                                tvEmpty.setText("No repair history found.\nPull down to refresh.");
                                adapter.updateList(new ArrayList<>());
                            }
                        });
                    }).start();

                } else {
                    String errorMsg = "API Error: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMsg = response.errorBody().string();
                            Log.e(TAG, "❌ Error Body: " + errorMsg);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading error body", e);
                    }
                    Toast.makeText(getContext(), "⚠️ " + errorMsg, Toast.LENGTH_LONG).show();
                    // If API fails, we already showed cached data from loadFromLocalDatabase()
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<Appointment>>> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                Log.e(TAG, "❌ Network Failure: ", t);
                Toast.makeText(getContext(), "⚠️ Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                // If network fails, we already showed cached data
            }
        });
    }
}