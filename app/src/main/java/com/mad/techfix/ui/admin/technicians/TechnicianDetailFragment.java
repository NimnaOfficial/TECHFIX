package com.mad.techfix.ui.admin.technicians;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.mad.techfix.R;
import com.mad.techfix.ui.admin.AdminViewModel;

public class TechnicianDetailFragment extends Fragment {

    private AdminViewModel viewModel;
    private int technicianId;
    
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_technician_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        viewModel = new ViewModelProvider(requireActivity()).get(AdminViewModel.class);

        TextView tvName = view.findViewById(R.id.tv_tech_name);
        TextView tvCode = view.findViewById(R.id.tv_tech_code);
        TextView tvSpecialization = view.findViewById(R.id.tv_tech_specialization);
        TextView tvBranch = view.findViewById(R.id.tv_tech_branch);
        TextView tvStatus = view.findViewById(R.id.tv_tech_status);
        Button btnEditSkills = view.findViewById(R.id.btn_edit_skills);

        if (getArguments() != null) {
            technicianId = getArguments().getInt("technicianId");
            tvName.setText(getArguments().getString("name"));
            tvCode.setText(getArguments().getString("code"));
            tvSpecialization.setText(getArguments().getString("specialization"));
            tvBranch.setText(getArguments().getString("branch"));
            tvStatus.setText(getArguments().getString("status"));
        }

        viewModel.loadTechnicianServices(technicianId);

        viewModel.getTechnicianServices().observe(getViewLifecycleOwner(), services -> {
            if (services != null) {
                // Populate chips or list
            }
        });

        btnEditSkills.setOnClickListener(v -> {
            EditSkillsDialogFragment dialog = new EditSkillsDialogFragment();
            Bundle args = new Bundle();
            args.putInt("technicianId", technicianId);
            dialog.setArguments(args);
            dialog.show(getChildFragmentManager(), "EditSkillsDialog");
        });

        viewModel.getSkillUpdateSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                Toast.makeText(getContext(), "Skills updated successfully", Toast.LENGTH_SHORT).show();
                viewModel.loadTechnicianServices(technicianId);
            }
        });
    }
}
