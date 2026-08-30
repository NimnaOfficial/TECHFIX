package com.mad.techfix.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.mad.techfix.R;
import com.mad.techfix.data.SessionManager;
import com.mad.techfix.ui.auth.LoginActivity;
import com.mad.techfix.viewmodel.AdminViewModel;

public class SysAdminDashboardFragment extends Fragment {

    private AdminViewModel viewModel;
    private TextView tvTotalUsers, tvSystemHealth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sys_admin_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(AdminViewModel.class);
        
        tvTotalUsers = view.findViewById(R.id.tv_total_users);
        tvSystemHealth = view.findViewById(R.id.tv_system_health);

        ImageButton btnLogout = view.findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(v -> {
            SessionManager sessionManager = new SessionManager(requireContext());
            sessionManager.clearSession();
            Toast.makeText(requireContext(), "Signed out safely", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });
        
        view.findViewById(R.id.btn_manage_managers).setOnClickListener(v -> {
            ((com.google.android.material.bottomnavigation.BottomNavigationView) requireActivity().findViewById(R.id.bottom_nav)).setSelectedItemId(R.id.nav_sys_users);
        });
        
        view.findViewById(R.id.btn_system_logs).setOnClickListener(v -> {
            Toast.makeText(getContext(), "Loading System Logs...", Toast.LENGTH_SHORT).show();
        });

        setupObservers();
        viewModel.loadSystemOverview();
    }

    private void setupObservers() {
        viewModel.getSystemOverview().observe(getViewLifecycleOwner(), data -> {
            if (data != null) {
                tvTotalUsers.setText(String.valueOf(data.getTotalUsers()));
                tvSystemHealth.setText(data.getSystemHealth());
                
                if ("ONLINE".equalsIgnoreCase(data.getSystemHealth())) {
                    tvSystemHealth.setTextColor(android.graphics.Color.parseColor("#4CAF50")); // Green
                } else {
                    tvSystemHealth.setTextColor(android.graphics.Color.parseColor("#F44336")); // Red
                }
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                tvSystemHealth.setText("OFFLINE / ERROR");
                tvSystemHealth.setTextColor(android.graphics.Color.parseColor("#F44336"));
            }
        });
    }
}


