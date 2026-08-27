package com.mad.techfix.ui.admin.technicians;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.ChipGroup;
import com.mad.techfix.R;
import com.mad.techfix.ui.admin.AdminViewModel;
import com.mad.techfix.models.User;
import java.util.ArrayList;
import java.util.List;

public class TechnicianListFragment extends Fragment {

    private AdminViewModel viewModel;
    private RecyclerView rvTechnicians;
    private EditText etSearch;
    private ChipGroup chipGroupStatus;
    
    private List<User> fullList = new ArrayList<>();
    private List<User> filteredList = new ArrayList<>();
    private String currentStatusFilter = "All";
    private String currentSearchText = "";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_technician_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        viewModel = new ViewModelProvider(requireActivity()).get(AdminViewModel.class);
        rvTechnicians = view.findViewById(R.id.rv_technicians);
        etSearch = view.findViewById(R.id.et_search);
        chipGroupStatus = view.findViewById(R.id.chip_group_status);
        
        rvTechnicians.setLayoutManager(new LinearLayoutManager(getContext()));

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchText = s.toString().toLowerCase();
                applyFilters();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        chipGroupStatus.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                int checkedId = checkedIds.get(0);
                if (checkedId == R.id.chip_available) currentStatusFilter = "Available";
                else if (checkedId == R.id.chip_busy) currentStatusFilter = "Busy";
                else if (checkedId == R.id.chip_off_duty) currentStatusFilter = "Off Duty";
                else if (checkedId == R.id.chip_on_leave) currentStatusFilter = "On Leave";
                else currentStatusFilter = "All";
            } else {
                currentStatusFilter = "All";
            }
            applyFilters();
        });

        viewModel.getTechnicians().observe(getViewLifecycleOwner(), technicians -> {
            if (technicians != null) {
                fullList = technicians;
                applyFilters();
            }
        });

        viewModel.loadTechnicians();
    }

    private void applyFilters() {
        filteredList.clear();
        for (User tech : fullList) {
            boolean matchesSearch = (tech.getName() != null && tech.getName().toLowerCase().contains(currentSearchText)) ||
                                    (tech.getEmployeeCode() != null && tech.getEmployeeCode().toLowerCase().contains(currentSearchText));
            boolean matchesStatus = currentStatusFilter.equals("All") || currentStatusFilter.equalsIgnoreCase(tech.getStatus());
            
            if (matchesSearch && matchesStatus) {
                filteredList.add(tech);
            }
        }
        // Update adapter here
    }
}
