package com.mad.techfix.ui.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mad.techfix.R;
import com.mad.techfix.models.admin.Manager;
import com.mad.techfix.ui.admin.adapters.ManagerAdapter;
import com.mad.techfix.viewmodel.AdminViewModel;

public class SysAdminUsersFragment extends Fragment implements ManagerAdapter.OnManagerInteractionListener {

    private AdminViewModel viewModel;
    private ManagerAdapter adapter;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sys_admin_users, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(AdminViewModel.class);
        progressBar = view.findViewById(R.id.progress_bar);
        
        RecyclerView rvManagers = view.findViewById(R.id.rv_managers);
        rvManagers.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ManagerAdapter(this);
        rvManagers.setAdapter(adapter);

        FloatingActionButton fabAdd = view.findViewById(R.id.fab_add_manager);
        fabAdd.setOnClickListener(v -> showManagerDialog(null));

        setupObservers();
        viewModel.loadManagers();
    }

    private void setupObservers() {
        viewModel.getManagers().observe(getViewLifecycleOwner(), managers -> {
            adapter.setManagers(managers);
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

    @Override
    public void onEditClick(Manager manager) {
        showManagerDialog(manager);
    }

    @Override
    public void onDeleteClick(Manager manager) {
        new AlertDialog.Builder(requireContext())
            .setTitle("Delete Manager")
            .setMessage("Are you sure you want to delete " + manager.getFirstName() + "?")
            .setPositiveButton("Delete", (dialog, which) -> viewModel.deleteManager(manager.getId()))
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void showManagerDialog(@Nullable Manager existingManager) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(existingManager == null ? "Add Manager" : "Edit Manager");

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_manager_form, null);
        EditText etFirstName = view.findViewById(R.id.et_first_name);
        EditText etLastName = view.findViewById(R.id.et_last_name);
        EditText etEmail = view.findViewById(R.id.et_email);
        EditText etPhone = view.findViewById(R.id.et_phone);
        EditText etPassword = view.findViewById(R.id.et_password);

        if (existingManager != null) {
            etFirstName.setText(existingManager.getFirstName());
            etLastName.setText(existingManager.getLastName());
            etEmail.setText(existingManager.getEmail());
            etEmail.setEnabled(false); // Can't change email
            etPhone.setText(existingManager.getPhone());
            etPassword.setVisibility(View.GONE); // Don't edit password here
        }

        builder.setView(view);
        builder.setPositiveButton("Save", (dialog, which) -> {
            Manager manager = new Manager();
            manager.setFirstName(etFirstName.getText().toString().trim());
            manager.setLastName(etLastName.getText().toString().trim());
            manager.setPhone(etPhone.getText().toString().trim());
            manager.setIsActive(1);
            
            if (existingManager == null) {
                manager.setEmail(etEmail.getText().toString().trim());
                manager.setPassword(etPassword.getText().toString().trim());
                viewModel.createManager(manager);
            } else {
                viewModel.updateManager(existingManager.getId(), manager);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}
