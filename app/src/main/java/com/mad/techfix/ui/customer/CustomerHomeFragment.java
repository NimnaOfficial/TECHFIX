package com.mad.techfix.ui.customer;

import android.os.Bundle;
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

import com.google.android.material.card.MaterialCardView;
import com.mad.techfix.R;
import com.mad.techfix.data.SessionManager;
import com.mad.techfix.models.ApiResponse;
import com.mad.techfix.models.Appointment;
import com.mad.techfix.network.RetrofitClient;
import com.mad.techfix.ui.customer.booking.CustomerAppointmentAdapter;
import com.mad.techfix.ui.customer.booking.CustomerAppointmentDetailBottomSheet;
import com.mad.techfix.ui.customer.booking.MyAppointmentsFragment;
import com.mad.techfix.ui.customer.booking.RepairBookingFragment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerHomeFragment extends Fragment {

    private SessionManager sessionManager;
    private TextView tvWelcomeName, tvViewAll, tvEmptyRepairs;
    private MaterialCardView cardBookRepair, cardMyAppointments, cardMyDevices, cardProfile;
    private RecyclerView recyclerActiveRepairs;
    private ProgressBar progressBar;
    private CustomerAppointmentAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_home, container, false);
        sessionManager = new SessionManager(requireContext());
        
        initViews(view);
        setupGreeting();
        setupClickListeners();
        setupRecyclerView();
        
        fetchRecentAppointments();
        
        return view;
    }

    private void initViews(View view) {
        tvWelcomeName = view.findViewById(R.id.tvWelcomeName);
        tvViewAll = view.findViewById(R.id.tvViewAll);
        tvEmptyRepairs = view.findViewById(R.id.tvEmptyRepairs);
        
        cardBookRepair = view.findViewById(R.id.cardBookRepair);
        cardMyAppointments = view.findViewById(R.id.cardMyAppointments);
        cardMyDevices = view.findViewById(R.id.cardMyDevices);
        cardProfile = view.findViewById(R.id.cardProfile);
        
        recyclerActiveRepairs = view.findViewById(R.id.recyclerActiveRepairs);
        progressBar = view.findViewById(R.id.progressBar);
    }

    private void setupGreeting() {
        String userName = sessionManager.getUserName();
        tvWelcomeName.setText(userName != null && !userName.isEmpty() ? userName : "Customer");
    }

    private void setupClickListeners() {
        cardBookRepair.setOnClickListener(v -> navigateTo(new RepairBookingFragment()));
        cardMyAppointments.setOnClickListener(v -> navigateTo(new MyAppointmentsFragment()));
        cardMyDevices.setOnClickListener(v -> navigateTo(new CustomerDevicesFragment()));
        cardProfile.setOnClickListener(v -> navigateTo(new CustomerProfileFragment()));
        
        tvViewAll.setOnClickListener(v -> navigateTo(new MyAppointmentsFragment()));
    }

    private void setupRecyclerView() {
        recyclerActiveRepairs.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new CustomerAppointmentAdapter(appointment -> {
            CustomerAppointmentDetailBottomSheet bottomSheet = CustomerAppointmentDetailBottomSheet.newInstance(appointment.getId());
            bottomSheet.show(getParentFragmentManager(), "AppointmentDetailBottomSheet");
        });
        recyclerActiveRepairs.setAdapter(adapter);
    }

    private void fetchRecentAppointments() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerActiveRepairs.setVisibility(View.GONE);
        tvEmptyRepairs.setVisibility(View.GONE);
        
        String token = sessionManager.getBearerToken();
        if (token == null) return;
        
        RetrofitClient.getApiService().getAppointments(token).enqueue(new Callback<ApiResponse<List<Appointment>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<Appointment>>> call, @NonNull Response<ApiResponse<List<Appointment>>> response) {
                if (isAdded()) {
                    progressBar.setVisibility(View.GONE);
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        List<Appointment> allAppointments = response.body().getData();
                        List<Appointment> activeRepairs = new ArrayList<>();
                        
                        // Filter active repairs only
                        if (allAppointments != null) {
                            for (Appointment apt : allAppointments) {
                                if (!"COMPLETED".equalsIgnoreCase(apt.getStatus()) && !"CANCELLED".equalsIgnoreCase(apt.getStatus())) {
                                    activeRepairs.add(apt);
                                }
                            }
                        }
                        
                        if (activeRepairs.isEmpty()) {
                            tvEmptyRepairs.setVisibility(View.VISIBLE);
                        } else {
                            adapter.setAppointments(activeRepairs);
                            recyclerActiveRepairs.setVisibility(View.VISIBLE);
                        }
                    } else {
                        tvEmptyRepairs.setText("Failed to load repairs.");
                        tvEmptyRepairs.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<Appointment>>> call, @NonNull Throwable t) {
                if (isAdded()) {
                    progressBar.setVisibility(View.GONE);
                    tvEmptyRepairs.setText("Network error loading repairs.");
                    tvEmptyRepairs.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void navigateTo(Fragment fragment) {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
}
