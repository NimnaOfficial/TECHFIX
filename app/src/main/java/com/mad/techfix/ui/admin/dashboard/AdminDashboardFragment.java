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
import android.content.Intent;
import com.mad.techfix.data.SessionManager;
import com.mad.techfix.ui.auth.LoginActivity;
import android.widget.Toast;
import com.mad.techfix.R;
import com.mad.techfix.ui.admin.adapters.DashboardAppointmentAdapter;
import com.mad.techfix.viewmodel.AdminViewModel;
import java.text.NumberFormat;
import java.util.Locale;

public class AdminDashboardFragment extends Fragment {

    private AdminViewModel viewModel;
    private DashboardAppointmentAdapter adapter;

    private TextView tvTotalRevenue, tvPendingRequests, tvActiveRepairs, tvAvailableTechs, tvEmpty, tvBranchName;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;
    private RecyclerView recyclerRecentAppointments;

    private double currentRevenue = -1;
    private int currentPending = -1;
    private int currentActive = -1;
    private int currentTechs = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity()).get(AdminViewModel.class);

        tvTotalRevenue = view.findViewById(R.id.tv_total_revenue);
        tvBranchName = view.findViewById(R.id.tv_branch_name);
        tvPendingRequests = view.findViewById(R.id.tv_pending_requests);
        tvActiveRepairs = view.findViewById(R.id.tv_active_repairs);
        tvAvailableTechs = view.findViewById(R.id.tv_available_techs);
        tvEmpty = view.findViewById(R.id.tv_empty);
        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        progressBar = view.findViewById(R.id.progress_bar);
        recyclerRecentAppointments = view.findViewById(R.id.recycler_recent_appointments);

                ImageButton btnRefresh = view.findViewById(R.id.btn_refresh);
        ImageButton btnLogout = view.findViewById(R.id.btn_logout);
        ImageButton btnTheme = view.findViewById(R.id.btn_theme);

        SessionManager sessionManager = new SessionManager(requireContext());
        
        btnTheme.setOnClickListener(v -> {
            boolean isDark = sessionManager.isDarkMode();
            sessionManager.setDarkMode(!isDark);
            if (!isDark) {
                androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        btnLogout.setOnClickListener(v -> {
            sessionManager.clearSession();
            Toast.makeText(requireContext(), "Signed out successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });

        recyclerRecentAppointments.setLayoutManager(new LinearLayoutManager(getContext()));
        
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

    private void animateNumber(TextView textView, int endValue, int currentValue) {
        if (currentValue == endValue) return;
        int start = currentValue == -1 ? 0 : currentValue;
        android.animation.ValueAnimator animator = android.animation.ValueAnimator.ofInt(start, endValue);
        animator.setDuration(1000);
        animator.addUpdateListener(animation -> textView.setText(String.valueOf(animation.getAnimatedValue())));
        animator.start();
    }
    
    private void animateCurrency(TextView textView, double endValue, double currentValue) {
        if (currentValue == endValue) return;
        double start = currentValue == -1 ? 0 : currentValue;
        android.animation.ValueAnimator animator = android.animation.ValueAnimator.ofFloat((float)start, (float) endValue);
        animator.setDuration(1200);
        animator.addUpdateListener(animation -> {
            NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("en", "LK"));
            textView.setText(format.format(animation.getAnimatedValue()));
        });
        animator.start();
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            swipeRefresh.setRefreshing(isLoading);
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getDashboardData().observe(getViewLifecycleOwner(), data -> {
            if (data != null) {
                if (data.getBranchName() != null && !data.getBranchName().isEmpty()) {
                    tvBranchName.setText("Branch: " + data.getBranchName());
                    tvBranchName.setVisibility(View.VISIBLE);
                } else {
                    tvBranchName.setVisibility(View.GONE);
                }
                
                animateCurrency(tvTotalRevenue, data.getTotalRevenue(), currentRevenue);
                currentRevenue = data.getTotalRevenue();
                
                animateNumber(tvPendingRequests, data.getPendingRequests(), currentPending);
                currentPending = data.getPendingRequests();
                
                animateNumber(tvActiveRepairs, data.getActiveRepairs(), currentActive);
                currentActive = data.getActiveRepairs();
                
                animateNumber(tvAvailableTechs, data.getAvailableTechnicians(), currentTechs);
                currentTechs = data.getAvailableTechnicians();
            }
        });

        viewModel.getAllAppointments().observe(getViewLifecycleOwner(), appointments -> {
            if (appointments != null && !appointments.isEmpty()) {
                adapter.updateData(appointments);
                tvEmpty.setVisibility(View.GONE);
                recyclerRecentAppointments.setVisibility(View.VISIBLE);
                
                // Run layout animation
                final android.content.Context ctx = recyclerRecentAppointments.getContext();
                final android.view.animation.LayoutAnimationController controller = 
                    android.view.animation.AnimationUtils.loadLayoutAnimation(ctx, R.anim.layout_animation_slide_up);
                recyclerRecentAppointments.setLayoutAnimation(controller);
                recyclerRecentAppointments.scheduleLayoutAnimation();
            } else {
                tvEmpty.setVisibility(View.VISIBLE);
                recyclerRecentAppointments.setVisibility(View.GONE);
            }
        });

        viewModel.getAssignmentSuccess().observe(getViewLifecycleOwner(), success -> {
            if (Boolean.TRUE.equals(success)) {
                // Refresh dashboard when assignment succeeds
                viewModel.loadDashboard();
                viewModel.loadAllAppointments();
                // Reset the trigger
                viewModel.getAssignmentSuccess().setValue(false);
            }
        });
    }

    // --- REALTIME ACTIVE DASHBOARD LOGIC ---
    private final android.os.Handler autoRefreshHandler = new android.os.Handler(android.os.Looper.getMainLooper());
    private final Runnable autoRefreshRunnable = new Runnable() {
        @Override
        public void run() {
            loadData();
            autoRefreshHandler.postDelayed(this, 10000); // 10 seconds auto-refresh
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        autoRefreshHandler.post(autoRefreshRunnable); // Start realtime tracking
    }

    @Override
    public void onPause() {
        super.onPause();
        autoRefreshHandler.removeCallbacks(autoRefreshRunnable); // Pause when hidden
    }
}


