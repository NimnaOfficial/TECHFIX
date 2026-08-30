package com.mad.techfix.ui.admin;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.mad.techfix.R;
import com.mad.techfix.data.SessionManager;
import com.mad.techfix.network.RetrofitClient;
import com.mad.techfix.repository.AdminRepository;
import com.mad.techfix.ui.admin.adapters.LogAdapter;
import com.mad.techfix.ui.auth.LoginActivity;
import com.mad.techfix.viewmodel.AdminViewModel;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import okhttp3.ResponseBody;

public class SysAdminSettingsFragment extends Fragment {

    private AdminViewModel viewModel;
    private SwitchMaterial switchMaintenance;
    private ProgressBar progressBar;
    private boolean isProgrammaticChange = false;
    private LogAdapter logAdapter;

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

        // Logs
        RecyclerView rvLogs = view.findViewById(R.id.rv_logs);
        MaterialButton btnRefreshLogs = view.findViewById(R.id.btn_refresh_logs);
        MaterialButton btnClearLogs = view.findViewById(R.id.btn_clear_logs);

        rvLogs.setLayoutManager(new LinearLayoutManager(getContext()));
        logAdapter = new LogAdapter();
        rvLogs.setAdapter(logAdapter);

        btnRefreshLogs.setOnClickListener(v -> viewModel.loadSystemLogs());
        btnClearLogs.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                .setTitle("Purge Logs")
                .setMessage("Are you sure you want to permanently delete all system API logs?")
                .setPositiveButton("Purge", (d, w) -> viewModel.clearSystemLogs())
                .setNegativeButton("Cancel", null)
                .show();
        });

        switchMaintenance.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isProgrammaticChange) {
                viewModel.updateSystemSetting("maintenance_mode", String.valueOf(isChecked));
            }
        });

        btnExportDb.setOnClickListener(v -> exportDatabase());

        btnClearCache.setOnClickListener(v -> {
            requireContext().getCacheDir().delete();
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
        viewModel.loadSystemLogs();
    }

    private void exportDatabase() {
        progressBar.setVisibility(View.VISIBLE);
        SessionManager sessionManager = new SessionManager(requireContext());
        String token = "Bearer " + sessionManager.getBearerToken();
        
        AdminRepository repo = new AdminRepository(com.mad.techfix.data.local.database.AppDatabase.getInstance(requireContext()));
        repo.getSystemBackup(token, new AdminRepository.AdminCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody data) {
                try {
                    File dir = requireContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
                    File backupFile = new File(dir, "TechFix_DB_Backup_" + System.currentTimeMillis() + ".json");
                    
                    InputStream is = data.byteStream();
                    FileOutputStream fos = new FileOutputStream(backupFile);
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, bytesRead);
                    }
                    fos.close();
                    is.close();
                    
                    requireActivity().runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        new AlertDialog.Builder(requireContext())
                            .setTitle("Database Exported")
                            .setMessage("Full database successfully saved to device:\n" + backupFile.getAbsolutePath())
                            .setPositiveButton("OK", null)
                            .show();
                    });
                } catch (Exception e) {
                    requireActivity().runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Error saving file: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
                }
            }
            @Override
            public void onError(String error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupObservers() {
        viewModel.getSystemSettings().observe(getViewLifecycleOwner(), settings -> {
            if (settings != null && settings.containsKey("maintenance_mode")) {
                isProgrammaticChange = true;
                switchMaintenance.setChecked("true".equalsIgnoreCase(settings.get("maintenance_mode")));
                isProgrammaticChange = false;
            }
        });

        viewModel.getSystemLogs().observe(getViewLifecycleOwner(), logs -> {
            logAdapter.setLogs(logs);
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



