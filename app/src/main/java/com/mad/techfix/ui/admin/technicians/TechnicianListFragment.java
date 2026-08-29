package com.mad.techfix.ui.admin.technicians;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mad.techfix.R;
import com.mad.techfix.models.admin.Technician;
import com.mad.techfix.ui.admin.adapters.TechnicianAdapter;
import com.mad.techfix.viewmodel.AdminViewModel;
import java.util.ArrayList;
import java.util.List;

public class TechnicianListFragment extends Fragment {

    private AdminViewModel viewModel;
    private TechnicianAdapter adapter;
    private List<Technician> allTechnicians = new ArrayList<>();
    private String currentStatusFilter = "ALL";
    private String currentSearchQuery = "";

    private RecyclerView recyclerTechnicians;
    private ProgressBar progressBar;
    private LinearLayout layoutEmptyState;
    private EditText etSearch;
    private ChipGroup chipGroupFilter;
    private FloatingActionButton fabAdd;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_technician_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity()).get(AdminViewModel.class);

        recyclerTechnicians = view.findViewById(R.id.recycler_technicians);
        progressBar = view.findViewById(R.id.progress_bar);
        layoutEmptyState = view.findViewById(R.id.layout_empty_state);
        etSearch = view.findViewById(R.id.et_search);
        chipGroupFilter = view.findViewById(R.id.chip_group_filter);
        fabAdd = view.findViewById(R.id.fab_add_technician);

        recyclerTechnicians.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TechnicianAdapter(this::openTechnicianDetail, this::showAddTechnicianDialog, this::confirmDeleteTechnician);
        recyclerTechnicians.setAdapter(adapter);

        fabAdd.setOnClickListener(v -> showAddTechnicianDialog(null));

        setupFilters();
        observeViewModel();
        viewModel.loadTechnicians();
        viewModel.loadBranches(); // Pre-load branches for the Combobox
    }

    private void showAddTechnicianDialog(@Nullable Technician tech) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_technician_form, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();


        TextView tvTitle = view.findViewById(R.id.tv_form_title);
        TextInputLayout tilFirstName = view.findViewById(R.id.til_tech_first_name);
        TextInputLayout tilLastName = view.findViewById(R.id.til_tech_last_name);
        TextInputLayout tilCode = view.findViewById(R.id.til_tech_code);
        TextInputLayout tilSpecialization = view.findViewById(R.id.til_tech_specialization);
        TextInputLayout tilBranch = view.findViewById(R.id.til_tech_branch);

        TextInputEditText etFirstName = view.findViewById(R.id.et_tech_first_name);
        TextInputEditText etLastName = view.findViewById(R.id.et_tech_last_name);
        TextInputEditText etCode = view.findViewById(R.id.et_tech_code);
        TextInputEditText etSpecialization = view.findViewById(R.id.et_tech_specialization);
        android.widget.AutoCompleteTextView etBranch = view.findViewById(R.id.et_tech_branch);
        View btnSave = view.findViewById(R.id.btn_save);
        View btnCancel = view.findViewById(R.id.btn_cancel);
        View btnDelete = view.findViewById(R.id.btn_delete);

        // Setup Branch Combobox
        List<com.mad.techfix.models.admin.Branch> branchList = viewModel.getBranches().getValue();
        List<String> branchOptions = new ArrayList<>();
        if (branchList != null && !branchList.isEmpty()) {
            for (com.mad.techfix.models.admin.Branch b : branchList) {
                branchOptions.add(b.getId() + " - " + b.getName());
            }
        } else {
            branchOptions.add("No branches found - Add a branch first");
        }
        
        android.widget.ArrayAdapter<String> branchAdapter = new android.widget.ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, branchOptions);
        etBranch.setAdapter(branchAdapter);

        if (tech != null) {
            tvTitle.setText("Edit Technician");
            etFirstName.setText(tech.getFirstName());
            etLastName.setText(tech.getLastName());
            etCode.setText(tech.getEmployeeCode());
            etSpecialization.setText(tech.getSpecialization());
            
            // Re-map the selected branch formatting if editing
            String displayBranch = tech.getBranchId();
            if (branchList != null) {
                for (com.mad.techfix.models.admin.Branch b : branchList) {
                    if (b.getId().equals(tech.getBranchId())) {
                        displayBranch = b.getId() + " - " + b.getName();
                        break;
                    }
                }
            }
            etBranch.setText(displayBranch, false);
            btnDelete.setVisibility(View.VISIBLE);
        }

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        
        btnDelete.setOnClickListener(v -> {
            if (tech != null) {
                viewModel.deleteTechnician(tech.getId());
                dialog.dismiss();
            }
        });

        btnSave.setOnClickListener(v -> {
            String firstNameStr = etFirstName.getText() != null ? etFirstName.getText().toString().trim() : "";
            String lastNameStr = etLastName.getText() != null ? etLastName.getText().toString().trim() : "";
            String branchRawStr = etBranch.getText() != null ? etBranch.getText().toString().trim() : "";

            if (firstNameStr.isEmpty()) { etFirstName.setError("First name is required"); etFirstName.requestFocus(); return; }
            if (lastNameStr.isEmpty()) { etLastName.setError("Last name is required"); etLastName.requestFocus(); return; }
            if (branchRawStr.isEmpty() || branchRawStr.startsWith("No branches")) { etBranch.setError("Valid Branch is required"); etBranch.requestFocus(); return; }

            String branchIdStr = branchRawStr;
            if (branchRawStr.contains(" - ")) {
                branchIdStr = branchRawStr.split(" - ")[0]; // Extract the ID
            }

            Technician t = tech != null ? tech : new Technician();
            if (tech != null) { t.setUserId(tech.getUserId()); } // Keep existing user ID if editing
            t.setFirstName(firstNameStr);
            t.setLastName(lastNameStr);
            t.setEmployeeCode((etCode.getText() != null ? etCode.getText().toString().trim() : ""));
            t.setSpecialization((etSpecialization.getText() != null ? etSpecialization.getText().toString().trim() : ""));
            t.setBranchId(branchIdStr);
            
            if (tech == null) {
                t.setAvailabilityStatus("AVAILABLE");
                viewModel.createTechnician(t);
            } else {
                viewModel.updateTechnician(t.getId(), t);
            }
            dialog.dismiss();
        });
    }

    private void confirmDeleteTechnician(Technician tech) {
        new android.app.AlertDialog.Builder(requireContext())
            .setTitle("Remove Technician")
            .setMessage("Are you sure you want to completely remove this technician?")
            .setPositiveButton("Remove", (dialog, which) -> viewModel.deleteTechnician(tech.getId()))
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void openTechnicianDetail(Technician tech) {
        TechnicianDetailFragment fragment = TechnicianDetailFragment.newInstance(
                tech.getId(),
                tech.getFullName(),
                tech.getEmployeeCode(),
                tech.getSpecialization(),
                tech.getBranchId(),
                tech.getAvailabilityStatus(),
                tech.getHireDate()
        );
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void setupFilters() {
        chipGroupFilter.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            if (id == R.id.chip_all) currentStatusFilter = "ALL";
            else if (id == R.id.chip_available) currentStatusFilter = "AVAILABLE";
            else if (id == R.id.chip_busy) currentStatusFilter = "BUSY";
            else if (id == R.id.chip_off_duty) currentStatusFilter = "OFF_DUTY";
            else if (id == R.id.chip_on_leave) currentStatusFilter = "ON_LEAVE";
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
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getTechnicians().observe(getViewLifecycleOwner(), technicians -> {
            if (technicians != null) {
                allTechnicians = technicians;
                applyFilters();
            }
        });

        viewModel.getCrudSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                Toast.makeText(getContext(), "Operation Successful!", Toast.LENGTH_SHORT).show();
                viewModel.loadTechnicians(); // Crucial: Reload list!
            }
        });
    }

    private void applyFilters() {
        List<Technician> filtered = new ArrayList<>();
        for (Technician t : allTechnicians) {
            boolean matchesStatus = currentStatusFilter.equals("ALL") || currentStatusFilter.equalsIgnoreCase(t.getAvailabilityStatus());
            
            String name = t.getFullName() != null ? t.getFullName().toLowerCase() : "";
            String code = t.getEmployeeCode() != null ? t.getEmployeeCode().toLowerCase() : "";
            boolean matchesSearch = name.contains(currentSearchQuery) || code.contains(currentSearchQuery);
            
            if (matchesStatus && matchesSearch) {
                filtered.add(t);
            }
        }

        adapter.updateData(filtered);
        
        if (filtered.isEmpty()) {
            layoutEmptyState.setVisibility(View.VISIBLE);
            recyclerTechnicians.setVisibility(View.GONE);
        } else {
            layoutEmptyState.setVisibility(View.GONE);
            recyclerTechnicians.setVisibility(View.VISIBLE);
        }
    }

    // Helper TextWatcher to clear errors
    private static class SimpleTextWatcher implements TextWatcher {
        private final TextInputLayout til;
        public SimpleTextWatcher(TextInputLayout til) { this.til = til; }
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
            til.setError(null);
        }
        @Override public void afterTextChanged(Editable s) {}
    }
}


