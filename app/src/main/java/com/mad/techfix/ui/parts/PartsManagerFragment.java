package com.mad.techfix.ui.parts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mad.techfix.R;
import com.mad.techfix.data.local.database.AppDatabase;
import com.mad.techfix.data.local.database.SparePartEntity;
import com.mad.techfix.models.ApiResponse;
import com.mad.techfix.models.SparePart;
import com.mad.techfix.network.ApiService;
import com.mad.techfix.network.RetrofitClient;
import com.mad.techfix.utils.TokenManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PartsManagerFragment extends Fragment {

    private RecyclerView recyclerView;
    private PartsAdapter adapter;
    private ApiService apiService;
    private TokenManager tokenManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_parts_manager, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Setup RecyclerView
        recyclerView = view.findViewById(R.id.rvParts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PartsAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // 2. Initialize Network Helpers
        apiService = RetrofitClient.getClient().create(ApiService.class);
        tokenManager = new TokenManager(requireContext());

        // 3. Fetch Real Data from API
        fetchSparePartsFromApi();
    }

    private void fetchSparePartsFromApi() {
        String token = tokenManager.getToken();

        // Check if user is logged in
        if (token == null) {
            Toast.makeText(getContext(), "Please login first", Toast.LENGTH_SHORT).show();
            // For testing without login, you can uncomment this to keep dummy data
            // insertDummyData();
            return;
        }

        // Show loading state (optional)
        Toast.makeText(getContext(), "Loading spare parts...", Toast.LENGTH_SHORT).show();

        // Call the API
        apiService.getSpareParts("Bearer " + token).enqueue(new Callback<ApiResponse<List<SparePart>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<SparePart>>> call,
                                   @NonNull Response<ApiResponse<List<SparePart>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    // Convert API SparePart list to your local SparePartEntity list
                    List<SparePart> apiParts = response.body().getData();
                    List<SparePartEntity> entityList = new ArrayList<>();

                    for (SparePart part : apiParts) {
                        // Map API data to your Room Entity
                        SparePartEntity entity = new SparePartEntity(
                                part.getId(),
                                "B001", // Default branch ID (you can fetch from branch selection later)
                                part.getName(),
                                "General", // Category (API doesn't have this yet)
                                part.getMinimum_stock(), // Using minimum_stock as quantity for now
                                part.getUnit_price()
                        );
                        entityList.add(entity);
                    }

                    // Update the RecyclerView
                    adapter.updateList(entityList);
                    Toast.makeText(getContext(), "Loaded " + entityList.size() + " parts", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getContext(), "Failed to load parts: " + response.message(), Toast.LENGTH_SHORT).show();
                    // Fallback to dummy data if API fails
                    insertDummyData();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<SparePart>>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                // Fallback to dummy data if network fails
                insertDummyData();
            }
        });
    }

    // ==========================================
    // FALLBACK: Insert dummy data for testing
    // (Kept from your original code)
    // ==========================================
    private void insertDummyData() {
        AppDatabase db = AppDatabase.getInstance(requireContext());
        new Thread(() -> {
            // Clear existing data (optional)
            // db.techFixDao().clearAll(); // You would need to add this method

            SparePartEntity dummy1 = new SparePartEntity("P001", "B001", "iPhone 14 Screen", "Display", 15, 85.00);
            SparePartEntity dummy2 = new SparePartEntity("P002", "B001", "Samsung Battery", "Battery", 20, 45.00);
            SparePartEntity dummy3 = new SparePartEntity("P003", "B001", "MacBook Charger", "Accessories", 8, 120.00);

            db.techFixDao().insertPart(dummy1);
            db.techFixDao().insertPart(dummy2);
            db.techFixDao().insertPart(dummy3);

            // Fetch and update UI
            List<SparePartEntity> parts = db.techFixDao().getPartsByBranch("B001");
            requireActivity().runOnUiThread(() -> {
                adapter.updateList(parts);
                Toast.makeText(getContext(), "Using dummy data (offline mode)", Toast.LENGTH_SHORT).show();
            });
        }).start();
    }
}