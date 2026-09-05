package com.mad.techfix.ui.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.mad.techfix.R;
import com.mad.techfix.models.admin.Manager;
import com.mad.techfix.ui.admin.adapters.ManagerAdapter;
import com.mad.techfix.viewmodel.AdminViewModel;
import java.util.ArrayList;
import java.util.List;

public class SysAdminUsersFragment extends Fragment implements ManagerAdapter.OnManagerInteractionListener {

    private AdminViewModel viewModel;
    private ManagerAdapter adapter;
    private ProgressBar progressBar;
    private List<Manager> allUsers = new ArrayList<>();
    private String currentRoleFilter = "ALL";

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
        ChipGroup chipGroup = view.findViewById(R.id.chip_group_roles);
        
        rvManagers.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ManagerAdapter(this);
        rvManagers.setAdapter(adapter);

        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                int id = checkedIds.get(0);
                if (id == R.id.chip_all) currentRoleFilter = "ALL";
                else if (id == R.id.chip_manager) currentRoleFilter = "MANAGER";
                else if (id == R.id.chip_tech) currentRoleFilter = "TECHNICIAN";
                else if (id == R.id.chip_customer) currentRoleFilter = "CUSTOMER";
                filterUsers();
            }
        });

        FloatingActionButton fabAdd = view.findViewById(R.id.fab_add_manager);
        fabAdd.setOnClickListener(v -> showUserDialog(null));

        String pendingFilter = viewModel.getPendingUserFilter();
        if (pendingFilter != null) {
            currentRoleFilter = pendingFilter;
            if ("MANAGER".equals(pendingFilter)) {
                chipGroup.check(R.id.chip_manager);
            }
            viewModel.setPendingUserFilter(null);
        }

        setupObservers();
        viewModel.loadManagers();
    }

    private void filterUsers() {
        if ("ALL".equals(currentRoleFilter)) {
            adapter.setManagers(allUsers);
        } else {
            List<Manager> filtered = new ArrayList<>();
            for (Manager m : allUsers) {
                if (currentRoleFilter.equalsIgnoreCase(m.getRole())) {
                    filtered.add(m);
                }
            }
            adapter.setManagers(filtered);
        }
    }

    private void setupObservers() {
                viewModel.getUserMonitor().observe(getViewLifecycleOwner(), monitor -> {
            if (monitor != null && activeConnectionsTextView != null) {
                StringBuilder sb = new StringBuilder();
                com.mad.techfix.models.admin.UserMonitorResponse.Connections c = monitor.getConnections();
                if (c == null) return;
                
                sb.append("✓ System History Interactions: ").append(c.getHistoryActionsCount()).append("\n\n");
                
                String role = monitor.getUser().getRole();
                if ("CUSTOMER".equalsIgnoreCase(role)) {
                    sb.append("[ REGISTERED DEVICES ]\n");
                    if (c.getDevices() != null && !c.getDevices().isEmpty()) {
                        for (com.mad.techfix.models.admin.UserMonitorResponse.Device d : c.getDevices()) {
                            sb.append("- ").append(d.brand).append(" ").append(d.model).append("\n");
                        }
                    } else { sb.append("- No devices found\n"); }
                    
                    sb.append("\n[ APPOINTMENTS ]\n");
                    if (c.getAppointmentsAsCustomer() != null && !c.getAppointmentsAsCustomer().isEmpty()) {
                        for (com.mad.techfix.models.admin.UserMonitorResponse.AppointmentPreview a : c.getAppointmentsAsCustomer()) {
                            sb.append("- APT: ").append(a.appointmentNumber).append(" (").append(a.status).append(")\n");
                        }
                    } else { sb.append("- No appointments found\n"); }
                } else if ("TECHNICIAN".equalsIgnoreCase(role)) {
                    if (c.getTechnicianProfile() != null) {
                        sb.append("Employee Code: ").append(c.getTechnicianProfile().employeeCode).append("\n");
                        sb.append("Status: ").append(c.getTechnicianProfile().availabilityStatus).append("\n\n");
                    }
                    sb.append("[ ASSIGNED REPAIRS ]\n");
                    if (c.getAppointmentsAsTech() != null && !c.getAppointmentsAsTech().isEmpty()) {
                        for (com.mad.techfix.models.admin.UserMonitorResponse.AppointmentPreview a : c.getAppointmentsAsTech()) {
                            sb.append("- APT: ").append(a.appointmentNumber).append(" (").append(a.status).append(")\n");
                        }
                    } else { sb.append("- No repairs assigned\n"); }
                    
                    sb.append("\n[ CERTIFIED SKILLS ]\n");
                    if (c.getSkills() != null && !c.getSkills().isEmpty()) {
                        for (com.mad.techfix.models.admin.UserMonitorResponse.Skill s : c.getSkills()) {
                            sb.append("- ").append(s.name).append("\n");
                        }
                    } else { sb.append("- No skills certified\n"); }
                } else if ("MANAGER".equalsIgnoreCase(role)) {
                    sb.append("[ BRANCH ACCESS ]\n");
                    sb.append("- Assigned Branch: ").append(c.getManagerBranch() != null ? c.getManagerBranch() : "Global Node").append("\n");
                }
                
                activeConnectionsTextView.setText(sb.toString().trim());
            }
        });

        viewModel.getManagers().observe(getViewLifecycleOwner(), users -> {
            this.allUsers = users != null ? users : new ArrayList<>();
            filterUsers();
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
    public void onEditClick(Manager user) {
        showUserDialog(user);
    }

    @Override
    public void onDeleteClick(Manager user) {
        new AlertDialog.Builder(requireContext())
            .setTitle("Delete User")
            .setMessage("Permanently delete " + user.getFirstName() + " (" + user.getRole() + ")? This will cascade and break their relational records.")
            .setPositiveButton("DELETE", (dialog, which) -> viewModel.deleteManager(user.getId()))
            .setNegativeButton("Cancel", null)
            .show();
    }

            private TextView activeConnectionsTextView = null;

    @Override
    public void onUserClick(Manager user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_user_details, null);
        
        TextView tvName = view.findViewById(R.id.tv_detail_name);
        TextView tvId = view.findViewById(R.id.tv_detail_id);
        TextView tvRole = view.findViewById(R.id.tv_detail_role);
        TextView tvStatus = view.findViewById(R.id.tv_detail_status);
        TextView tvEmail = view.findViewById(R.id.tv_detail_email);
        TextView tvPhone = view.findViewById(R.id.tv_detail_phone);
        activeConnectionsTextView = view.findViewById(R.id.tv_detail_connections);
        com.google.android.material.button.MaterialButton btnClose = view.findViewById(R.id.btn_close_details);

        tvName.setText(user.getFirstName() + " " + user.getLastName());
        tvId.setText("ID: " + user.getId());
        tvEmail.setText(user.getEmail());
        tvPhone.setText(user.getPhone() != null && !user.getPhone().isEmpty() ? user.getPhone() : "No phone provided");

        String role = user.getRole() != null ? user.getRole().toUpperCase() : "USER";
        tvRole.setText(role);
        if ("ADMIN".equals(role)) tvRole.setBackgroundColor(android.graphics.Color.parseColor("#9C27B0"));
        else if ("MANAGER".equals(role)) tvRole.setBackgroundColor(android.graphics.Color.parseColor("#2196F3"));
        else if ("TECHNICIAN".equals(role)) tvRole.setBackgroundColor(android.graphics.Color.parseColor("#FF9800"));
        else tvRole.setBackgroundColor(android.graphics.Color.parseColor("#607D8B"));

        if (user.getIsActive() == 1) {
            tvStatus.setText("Active");
            tvStatus.setTextColor(android.graphics.Color.parseColor("#4CAF50"));
        } else {
            tvStatus.setText("Inactive");
            tvStatus.setTextColor(android.graphics.Color.parseColor("#F44336"));
        }

        activeConnectionsTextView.setText("Establishing secure connection to fetch real-time database relational data...");
        viewModel.loadUserMonitor(user.getId());

        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        
        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void showUserDialog(@Nullable Manager existingUser) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_manager_form, null);
        
        TextView tvTitle = view.findViewById(R.id.tv_dialog_title);
        tvTitle.setText(existingUser == null ? "Create User" : "Edit User");

        TextInputEditText etFirstName = view.findViewById(R.id.et_first_name);
        TextInputEditText etLastName = view.findViewById(R.id.et_last_name);
        TextInputEditText etEmail = view.findViewById(R.id.et_email);
        TextInputEditText etPhone = view.findViewById(R.id.et_phone);
        TextInputEditText etPassword = view.findViewById(R.id.et_password);
        Spinner spinnerRole = view.findViewById(R.id.spinner_role);
        SwitchMaterial switchActive = view.findViewById(R.id.switch_active);

        if (existingUser != null) {
            etFirstName.setText(existingUser.getFirstName());
            etLastName.setText(existingUser.getLastName());
            etEmail.setText(existingUser.getEmail());
            etPhone.setText(existingUser.getPhone());
            switchActive.setChecked(existingUser.getIsActive() == 1);
            
            String[] roles = getResources().getStringArray(R.array.role_options);
            for (int i = 0; i < roles.length; i++) {
                if (roles[i].equalsIgnoreCase(existingUser.getRole())) {
                    spinnerRole.setSelection(i);
                    break;
                }
            }
        }

        builder.setView(view);
        builder.setPositiveButton("Execute", (dialog, which) -> {
            Manager user = new Manager();
            user.setFirstName(etFirstName.getText().toString().trim());
            user.setLastName(etLastName.getText().toString().trim());
            user.setEmail(etEmail.getText().toString().trim());
            user.setPhone(etPhone.getText().toString().trim());
            user.setRole(spinnerRole.getSelectedItem().toString());
            user.setIsActive(switchActive.isChecked() ? 1 : 0);
            
            String newPassword = etPassword.getText().toString().trim();
            if (!newPassword.isEmpty()) {
                user.setPassword(newPassword);
            }
            
            if (existingUser == null) {
                viewModel.createManager(user);
            } else {
                viewModel.updateManager(existingUser.getId(), user);
            }
        });
        builder.setNegativeButton("Abort", null);
        builder.show();
    }

}
