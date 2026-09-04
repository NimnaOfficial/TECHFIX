package com.mad.techfix.ui.admin.assignment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.mad.techfix.R;
import com.mad.techfix.models.admin.EligibleTechniciansResponse;
import com.mad.techfix.models.admin.Technician;
import com.mad.techfix.ui.admin.adapters.AssignableTechnicianAdapter;
import com.mad.techfix.viewmodel.AdminViewModel;
import java.util.ArrayList;
import java.util.List;

public class AssignTechnicianBottomSheet extends BottomSheetDialogFragment {

    private AdminViewModel viewModel;
    private AssignableTechnicianAdapter adapter;
    private String appointmentId, branchId, apptNumber, serviceName, branchName;

    // UI refs
    private ProgressBar progressBar;
    private TextView tvEmptyState;
    private TextView tvRecommendedLabel;
    private TextView tvOtherLabel;
    private RecyclerView recyclerTechs;

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
        recyclerTechs = view.findViewById(R.id.recycler_available_techs);
        Button btnCancel = view.findViewById(R.id.btn_cancel);
        Button btnAssign = view.findViewById(R.id.btn_assign);
        progressBar = view.findViewById(R.id.progress_eligible);
        tvEmptyState = view.findViewById(R.id.tv_no_eligible);
        tvRecommendedLabel = view.findViewById(R.id.tv_recommended_label);
        tvOtherLabel = view.findViewById(R.id.tv_other_label);

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

        // Observe the smart eligible technicians response
        viewModel.getEligibleTechnicians().observe(getViewLifecycleOwner(), response -> {
            if (progressBar != null) progressBar.setVisibility(View.GONE);

            if (response == null) return;

            List<Technician> recommended = response.getRecommended();
            List<Technician> otherAvailable = response.getOtherAvailable();

            boolean hasRecommended = recommended != null && !recommended.isEmpty();
            boolean hasOther = otherAvailable != null && !otherAvailable.isEmpty();

            if (!hasRecommended && !hasOther) {
                // No technicians available at all
                if (tvEmptyState != null) {
                    tvEmptyState.setVisibility(View.VISIBLE);
                    tvEmptyState.setText("No eligible technicians available for this appointment's branch and service.");
                }
                recyclerTechs.setVisibility(View.GONE);
                if (tvRecommendedLabel != null) tvRecommendedLabel.setVisibility(View.GONE);
                if (tvOtherLabel != null) tvOtherLabel.setVisibility(View.GONE);
                return;
            }

            if (tvEmptyState != null) tvEmptyState.setVisibility(View.GONE);
            recyclerTechs.setVisibility(View.VISIBLE);

            // Build combined list: recommended first, then others
            List<Technician> combinedList = new ArrayList<>();

            if (hasRecommended) {
                if (tvRecommendedLabel != null) {
                    tvRecommendedLabel.setVisibility(View.VISIBLE);
                    tvRecommendedLabel.setText("\u2B50 Recommended (" + recommended.size() + " skill-matched)");
                }
                combinedList.addAll(recommended);
            } else {
                if (tvRecommendedLabel != null) tvRecommendedLabel.setVisibility(View.GONE);
            }

            if (hasOther) {
                if (tvOtherLabel != null) {
                    tvOtherLabel.setVisibility(View.VISIBLE);
                    tvOtherLabel.setText("Other Available (" + otherAvailable.size() + ")");
                }
                combinedList.addAll(otherAvailable);
            } else {
                if (tvOtherLabel != null) tvOtherLabel.setVisibility(View.GONE);
            }

            adapter.updateData(combinedList);
        });

        viewModel.getAssignmentSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                Toast.makeText(getContext(), "Assigned Successfully", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });

        // Show loading and fetch eligible technicians from backend
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        if (tvEmptyState != null) tvEmptyState.setVisibility(View.GONE);
        viewModel.loadEligibleTechnicians(appointmentId);
    }
}
