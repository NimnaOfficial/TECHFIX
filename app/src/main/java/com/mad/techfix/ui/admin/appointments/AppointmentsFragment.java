package com.mad.techfix.ui.admin.appointments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.mad.techfix.R;
import com.mad.techfix.models.Appointment;
import com.mad.techfix.ui.admin.adapters.DashboardAppointmentAdapter;
import com.mad.techfix.ui.admin.dashboard.AppointmentDetailBottomSheet;
import com.mad.techfix.viewmodel.AdminViewModel;
import java.util.ArrayList;
import java.util.List;

public class AppointmentsFragment extends Fragment {

    private AdminViewModel viewModel;
    private DashboardAppointmentAdapter adapter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private LinearLayout layoutEmptyState;
    private SwipeRefreshLayout swipeRefresh;
    private ChipGroup chipGroupFilter;
    private TextInputEditText etSearch;

    private List<Appointment> allAppointments = new ArrayList<>();
    private String currentStatusFilter = "ALL";
    private String currentSearchQuery = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_appointments, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity()).get(AdminViewModel.class);

        recyclerView = view.findViewById(R.id.recycler_appointments);
        progressBar = view.findViewById(R.id.progress_bar);
        layoutEmptyState = view.findViewById(R.id.layout_empty_state);
        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        chipGroupFilter = view.findViewById(R.id.chip_group_filter);
        etSearch = view.findViewById(R.id.et_search);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new DashboardAppointmentAdapter(appointment -> {
            AppointmentDetailBottomSheet detailSheet = AppointmentDetailBottomSheet.newInstance(
                    appointment.getId(),
                    appointment.getAppointment_number(),
                    appointment.getStatus(),
                    appointment.getRequested_date(),
                    appointment.getRequested_time(),
                    appointment.getCustomer_id(),
                    appointment.getBranch_id()
            );
            detailSheet.show(getParentFragmentManager(), "AppointmentDetail");
        });
        recyclerView.setAdapter(adapter);

        setupFilters();
        observeViewModel();

        swipeRefresh.setOnRefreshListener(() -> viewModel.loadAllAppointments());
        
        // Initial load
        viewModel.loadAllAppointments();
    }

    private void setupFilters() {
        chipGroupFilter.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            if (id == R.id.chip_all) currentStatusFilter = "ALL";
            else if (id == R.id.chip_requested) currentStatusFilter = "REQUESTED";
            else if (id == R.id.chip_assigned) currentStatusFilter = "ASSIGNED";
            else if (id == R.id.chip_active) currentStatusFilter = "ACTIVE";
            else if (id == R.id.chip_completed) currentStatusFilter = "COMPLETED";
            applyFilters();
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchQuery = s.toString().toLowerCase().trim();
                applyFilters();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading && allAppointments.isEmpty()) {
                progressBar.setVisibility(View.VISIBLE);
            } else {
                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);
            }
        });

        viewModel.getAllAppointments().observe(getViewLifecycleOwner(), appointments -> {
            if (appointments != null) {
                allAppointments = appointments;
                applyFilters();
            }
        });
    }

    private void applyFilters() {
        List<Appointment> filtered = new ArrayList<>();
        for (Appointment a : allAppointments) {
            String status = a.getStatus() != null ? a.getStatus().toUpperCase() : "";
            
            boolean matchesStatus = currentStatusFilter.equals("ALL");
            if (!matchesStatus) {
                if (currentStatusFilter.equals("ACTIVE")) {
                    matchesStatus = status.equals("DEVICE_RECEIVED") || status.equals("DIAGNOSING") 
                                 || status.equals("REPAIRING") || status.equals("TESTING") || status.equals("READY");
                } else {
                    matchesStatus = currentStatusFilter.equals(status);
                }
            }
            
            String num = a.getAppointment_number() != null ? a.getAppointment_number().toLowerCase() : "";
            String date = a.getRequested_date() != null ? a.getRequested_date().toLowerCase() : "";
            boolean matchesSearch = num.contains(currentSearchQuery) || date.contains(currentSearchQuery);
            
            if (matchesStatus && matchesSearch) {
                filtered.add(a);
            }
        }

        adapter.updateData(filtered);
        
        if (filtered.isEmpty()) {
            layoutEmptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            layoutEmptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            
            // Re-trigger layout animation if we filter from empty to non-empty
            if (recyclerView.getAdapter() != null && recyclerView.getAdapter().getItemCount() == filtered.size()) {
                // If it's a completely new list update, sometimes animation helps UX
                recyclerView.scheduleLayoutAnimation();
            }
        }
    }
}
