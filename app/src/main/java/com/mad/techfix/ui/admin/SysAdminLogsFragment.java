package com.mad.techfix.ui.admin;

import android.app.AlertDialog;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.mad.techfix.R;
import com.mad.techfix.ui.admin.adapters.LogAdapter;
import com.mad.techfix.viewmodel.AdminViewModel;

public class SysAdminLogsFragment extends Fragment {

    private AdminViewModel viewModel;
    private LogAdapter adapter;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sys_admin_logs, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(AdminViewModel.class);
        
        progressBar = view.findViewById(R.id.progress_logs);
        RecyclerView rvLogs = view.findViewById(R.id.rv_logs);
        
        rvLogs.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new LogAdapter();
        rvLogs.setAdapter(adapter);

        MaterialButton btnRefresh = view.findViewById(R.id.btn_refresh_logs);
        MaterialButton btnClear = view.findViewById(R.id.btn_clear_logs);

        btnRefresh.setOnClickListener(v -> viewModel.loadSystemLogs());
        
        btnClear.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                .setTitle("Purge System Logs")
                .setMessage("Are you sure you want to permanently delete all API and system logs? This action cannot be undone.")
                .setPositiveButton("Purge", (d, w) -> viewModel.clearSystemLogs())
                .setNegativeButton("Cancel", null)
                .show();
        });

        setupObservers();
        viewModel.loadSystemLogs();
    }

    private void setupObservers() {
        viewModel.getSystemLogs().observe(getViewLifecycleOwner(), logs -> {
            adapter.setLogs(logs);
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
