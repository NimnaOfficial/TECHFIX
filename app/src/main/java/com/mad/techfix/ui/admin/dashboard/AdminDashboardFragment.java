package com.mad.techfix.ui.admin.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.mad.techfix.R;
import com.mad.techfix.ui.admin.adapters.DashboardAppointmentAdapter;
import com.mad.techfix.ui.admin.assignment.AssignTechnicianBottomSheet;
import com.mad.techfix.viewmodel.AdminViewModel;
import java.text.NumberFormat;
import java.util.Locale;

public class AdminDashboardFragment extends Fragment {

    private AdminViewModel viewModel;
    private DashboardAppointmentAdapter adapter;

    private TextView tvTotalRevenue, tvPendingRequests, tvActiveRepairs, tvAvailableTechs, tvEmpty;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;
    private RecyclerView recyclerRecentAppointments;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity()).get(AdminViewModel.class);

        tvTotalRevenue = view.findViewById(R.id.tv_total_revenue);
        tvPendingRequests = view.findViewById(R.id.tv_pending_requests);
        tvActiveRepairs = view.findViewById(R.id.tv_active_repairs);
        tvAvailableTechs = view.findViewById(R.id.tv_available_techs);
        tvEmpty = view.findViewById(R.id.tv_empty);
        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        progressBar = view.findViewById(R.id.progress_bar);
        recyclerRecentAppointments = view.findViewById(R.id.recycler_recent_appointments);

        ImageButton btnRefresh = view.findViewById(R.id.btn_refresh);

        recyclerRecentAppointments.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new DashboardAppointmentAdapter(appointment -> {
            AssignTechnicianBottomSheet bottomSheet = AssignTechnicianBottomSheet.newInstance(
                    appointment.getId(),
                    appointment.getBranch_id(),
                    appointment.getAppointment_number(),
                    "Service",
                    "Branch"
            );
            bottomSheet.show(getParentFragmentManager(), "AssignBottomSheet");
        });
        recyclerRecentAppointments.setAdapter(adapter);

        swipeRefresh.setOnRefreshListener(this::loadData);
        btnRefresh.setOnClickListener(v -> loadData());

        observeViewModel();
        loadData();
    }

    private void loadData() {
        viewModel.loadDashboard();
        viewModel.loadAllAppointments();
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            swipeRefresh.setRefreshing(isLoading);
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getDashboardData().observe(getViewLifecycleOwner(), data -> {
            if (data != null) {
                NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("en", "LK"));
                tvTotalRevenue.setText(format.format(data.getTotalRevenue()));
                tvPendingRequests.setText(String.valueOf(data.getPendingRequests()));
                tvActiveRepairs.setText(String.valueOf(data.getActiveRepairs()));
                tvAvailableTechs.setText(String.valueOf(data.getAvailableTechnicians()));
            }
        });

        viewModel.getAllAppointments().observe(getViewLifecycleOwner(), appointments -> {
            if (appointments != null && !appointments.isEmpty()) {
                adapter.updateData(appointments);
                tvEmpty.setVisibility(View.GONE);
                recyclerRecentAppointments.setVisibility(View.VISIBLE);
            } else {
                tvEmpty.setVisibility(View.VISIBLE);
                recyclerRecentAppointments.setVisibility(View.GONE);
            }
        });
    }
}
