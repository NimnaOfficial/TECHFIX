package com.mad.techfix.ui.admin.assignment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.mad.techfix.R;
import com.mad.techfix.models.admin.Technician;
import com.mad.techfix.ui.admin.adapters.AssignableTechnicianAdapter;
import com.mad.techfix.viewmodel.AdminViewModel;
import java.util.ArrayList;
import java.util.List;

public class AssignTechnicianBottomSheet extends BottomSheetDialogFragment {

    private AdminViewModel viewModel;
    private AssignableTechnicianAdapter adapter;
    private String appointmentId, branchId, apptNumber, serviceName, branchName;

    public static AssignTechnicianBottomSheet newInstance(String appointmentId, String branchId, String apptNumber, String serviceName, String branchName) {
        AssignTechnicianBottomSheet sheet = new AssignTechnicianBottomSheet();
        Bundle args = new Bundle();
        args.putString("appointmentId", appointmentId);
        args.putString("branchId", branchId);
        args.putString("apptNumber", apptNumber);
        args.putString("serviceName", serviceName);
        args.putString("branchName", branchName);
        sheet.setArguments(args);
        return sheet;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            appointmentId = getArguments().getString("appointmentId");
            branchId = getArguments().getString("branchId");
            apptNumber = getArguments().getString("apptNumber");
            serviceName = getArguments().getString("serviceName");
            branchName = getArguments().getString("branchName");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_assign_technician, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity()).get(AdminViewModel.class);

        TextView tvApptNo = view.findViewById(R.id.tv_assign_appt_no);
        TextView tvService = view.findViewById(R.id.tv_assign_service);
        TextView tvBranch = view.findViewById(R.id.tv_assign_branch);
        RecyclerView recyclerTechs = view.findViewById(R.id.recycler_available_techs);
        Button btnCancel = view.findViewById(R.id.btn_cancel);
        Button btnAssign = view.findViewById(R.id.btn_assign);

        tvApptNo.setText(apptNumber);
        tvService.setText(serviceName);
        tvBranch.setText(branchName);

        recyclerTechs.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AssignableTechnicianAdapter();
        recyclerTechs.setAdapter(adapter);

        btnCancel.setOnClickListener(v -> dismiss());

        btnAssign.setOnClickListener(v -> {
            Technician selected = adapter.getSelectedTechnician();
            if (selected == null) {
                Toast.makeText(getContext(), "Please select a technician", Toast.LENGTH_SHORT).show();
                return;
            }
            viewModel.assignTechnician(appointmentId, selected.getId());
        });

        viewModel.getTechnicians().observe(getViewLifecycleOwner(), technicians -> {
            if (technicians != null) {
                List<Technician> availableForBranch = new ArrayList<>();
                for (Technician t : technicians) {
                    if ("AVAILABLE".equalsIgnoreCase(t.getAvailabilityStatus()) && branchId.equals(t.getBranchId())) {
                        availableForBranch.add(t);
                    }
                }
                adapter.updateData(availableForBranch);
            }
        });

        viewModel.getAssignmentSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                Toast.makeText(getContext(), "Assigned Successfully", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });

        viewModel.loadTechnicians();
    }
}

