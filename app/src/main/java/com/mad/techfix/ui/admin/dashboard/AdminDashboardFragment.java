package com.mad.techfix.ui.admin.dashboard;

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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.mad.techfix.R;
import com.mad.techfix.ui.admin.AdminViewModel;
import java.util.Locale;

public class AdminDashboardFragment extends Fragment {

    private AdminViewModel viewModel;
    private TextView tvTotalAppointments, tvPendingAppointments, tvCompletedAppointments, tvTotalRevenue;
    private RecyclerView rvAppointments;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefresh;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        viewModel = new ViewModelProvider(requireActivity()).get(AdminViewModel.class);

        tvTotalAppointments = view.findViewById(R.id.tv_total_appointments);
        tvPendingAppointments = view.findViewById(R.id.tv_pending_appointments);
        tvCompletedAppointments = view.findViewById(R.id.tv_completed_appointments);
        tvTotalRevenue = view.findViewById(R.id.tv_total_revenue);
        rvAppointments = view.findViewById(R.id.rv_appointments);
        progressBar = view.findViewById(R.id.progress_bar);
        swipeRefresh = view.findViewById(R.id.swipe_refresh);

        rvAppointments.setLayoutManager(new LinearLayoutManager(getContext()));

        swipeRefresh.setOnRefreshListener(() -> {
            loadData();
            swipeRefresh.setRefreshing(false);
        });

        observeViewModel();
        loadData();
    }

    private void loadData() {
        viewModel.loadDashboard();
        viewModel.loadAllAppointments();
    }

    private void observeViewModel() {
        viewModel.getDashboardData().observe(getViewLifecycleOwner(), data -> {
            if (data != null) {
                tvTotalAppointments.setText(String.valueOf(data.getTotalAppointments()));
                tvPendingAppointments.setText(String.valueOf(data.getPendingAppointments()));
                tvCompletedAppointments.setText(String.valueOf(data.getCompletedAppointments()));
                tvTotalRevenue.setText(String.format(Locale.getDefault(), "LKR %,.2f", data.getTotalRevenue()));
            }
        });

        viewModel.getAllAppointments().observe(getViewLifecycleOwner(), appointments -> {
            if (appointments != null) {
                // Populate adapter here
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
