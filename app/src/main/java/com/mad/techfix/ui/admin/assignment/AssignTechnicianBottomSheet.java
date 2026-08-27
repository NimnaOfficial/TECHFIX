package com.mad.techfix.ui.admin.assignment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.mad.techfix.R;
import com.mad.techfix.ui.admin.AdminViewModel;
import com.mad.techfix.models.User;
import java.util.ArrayList;
import java.util.List;

public class AssignTechnicianBottomSheet extends BottomSheetDialogFragment {

    private AdminViewModel viewModel;
    private int appointmentId;
    private int branchId;
    private Integer selectedTechnicianId = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_assign_technician, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        viewModel = new ViewModelProvider(requireActivity()).get(AdminViewModel.class);

        if (getArguments() != null) {
            appointmentId = getArguments().getInt("appointmentId");
            branchId = getArguments().getInt("branchId");
        }

        RecyclerView rvTechnicians = view.findViewById(R.id.rv_assignable_technicians);
        rvTechnicians.setLayoutManager(new LinearLayoutManager(getContext()));
        Button btnAssign = view.findViewById(R.id.btn_assign);

        viewModel.getTechnicians().observe(getViewLifecycleOwner(), technicians -> {
            if (technicians != null) {
                List<User> filteredList = new ArrayList<>();
                for (User tech : technicians) {
                    if ("Available".equalsIgnoreCase(tech.getStatus()) && tech.getBranchId() == branchId) {
                        filteredList.add(tech);
                    }
                }
                // Update adapter with filteredList
            }
        });

        viewModel.loadTechnicians();

        btnAssign.setOnClickListener(v -> {
            if (selectedTechnicianId != null) {
                viewModel.assignTechnician(appointmentId, selectedTechnicianId);
            } else {
                Toast.makeText(getContext(), "Please select a technician", Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getAssignmentSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                Toast.makeText(getContext(), "Technician assigned successfully", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
