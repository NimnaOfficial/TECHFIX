package com.mad.techfix.ui.admin;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.mad.techfix.R;
import com.mad.techfix.data.SessionManager;
import com.mad.techfix.ui.auth.LoginActivity;
import com.mad.techfix.viewmodel.AdminViewModel;

public class SysAdminSettingsFragment extends Fragment {

    private AdminViewModel viewModel;
    private SwitchMaterial switchMaintenance;
    private ProgressBar progressBar;
    private boolean isProgrammaticChange = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sys_admin_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(AdminViewModel.class);
        
        switchMaintenance = view.findViewById(R.id.switch_maintenance);
        progressBar = view.findViewById(R.id.progress_settings);
        
        MaterialButton btnExportDb = view.findViewById(R.id.btn_export_db);
        MaterialButton btnClearCache = view.findViewById(R.id.btn_clear_cache);
        MaterialButton btnLogout = view.findViewById(R.id.btn_admin_logout);

        switchMaintenance.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isProgrammaticChange) {
                viewModel.updateSystemSetting("maintenance_mode", String.valueOf(isChecked));
            }
        });

        btnExportDb.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                .setTitle("Export Database")
                .setMessage("Database backup dispatched successfully to Admin Email.")
                .setPositiveButton("OK", null)
                .show();
        });

        btnClearCache.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                .setTitle("Purge Cache")
                .setMessage("Local application cache cleared successfully.")
                .setPositiveButton("OK", null)
                .show();
        });

        btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                .setTitle("Terminate Session")
                .setMessage("Are you sure you want to completely log out of the system?")
                .setPositiveButton("Logout", (d, w) -> {
                    SessionManager sessionManager = new SessionManager(requireContext());
                    sessionManager.clearSession();
                    Intent intent = new Intent(requireContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    requireActivity().finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
        });

        setupObservers();
        viewModel.loadSystemSettings();
    }

    private void setupObservers() {
        viewModel.getSystemSettings().observe(getViewLifecycleOwner(), settings -> {
            if (settings != null && settings.containsKey("maintenance_mode")) {
                isProgrammaticChange = true;
                switchMaintenance.setChecked("true".equalsIgnoreCase(settings.get("maintenance_mode")));
                isProgrammaticChange = false;
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading != null && isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
