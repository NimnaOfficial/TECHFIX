package com.mad.techfix.ui.admin.technicians;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import com.mad.techfix.R;
import com.mad.techfix.ui.admin.AdminViewModel;
import java.util.ArrayList;
import java.util.List;

public class EditSkillsDialogFragment extends DialogFragment {

    private AdminViewModel viewModel;
    private int technicianId;
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_edit_skills, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        viewModel = new ViewModelProvider(requireActivity()).get(AdminViewModel.class);

        if (getArguments() != null) {
            technicianId = getArguments().getInt("technicianId");
        }

        viewModel.loadAllServices();
        viewModel.loadTechnicianServices(technicianId);

        Button btnSave = view.findViewById(R.id.btn_save_skills);

        viewModel.getAllServices().observe(getViewLifecycleOwner(), allServices -> {
            // Populate checkboxes
        });

        viewModel.getTechnicianServices().observe(getViewLifecycleOwner(), techServices -> {
            // Pre-check checkboxes based on current skills
        });

        btnSave.setOnClickListener(v -> {
            List<Integer> selectedServiceIds = new ArrayList<>();
            // Collect checked service IDs from UI
            viewModel.updateTechnicianServices(technicianId, selectedServiceIds);
        });

        viewModel.getSkillUpdateSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                Toast.makeText(getContext(), "Skills saved", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
    }
}
