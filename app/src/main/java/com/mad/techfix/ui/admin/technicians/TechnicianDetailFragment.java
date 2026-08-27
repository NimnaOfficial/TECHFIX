package com.mad.techfix.ui.admin.technicians;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.mad.techfix.R;
import com.mad.techfix.models.admin.Service;
import com.mad.techfix.viewmodel.AdminViewModel;

public class TechnicianDetailFragment extends Fragment {

    private AdminViewModel viewModel;
    private String technicianId, name, code, specialization, branchId, status, hireDate;

    public static TechnicianDetailFragment newInstance(String id, String name, String code, String specialization, String branchId, String status, String hireDate) {
        TechnicianDetailFragment fragment = new TechnicianDetailFragment();
        Bundle args = new Bundle();
        args.putString("id", id);
        args.putString("name", name);
        args.putString("code", code);
        args.putString("specialization", specialization);
        args.putString("branchId", branchId);
        args.putString("status", status);
        args.putString("hireDate", hireDate);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            technicianId = getArguments().getString("id");
            name = getArguments().getString("name");
            code = getArguments().getString("code");
            specialization = getArguments().getString("specialization");
            branchId = getArguments().getString("branchId");
            status = getArguments().getString("status");
            hireDate = getArguments().getString("hireDate");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_technician_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity()).get(AdminViewModel.class);

        ImageButton btnBack = view.findViewById(R.id.btn_back);
        TextView tvName = view.findViewById(R.id.tv_detail_name);
        TextView tvCode = view.findViewById(R.id.tv_detail_emp_code);
        TextView tvSpecialization = view.findViewById(R.id.tv_detail_specialization);
        TextView tvBranch = view.findViewById(R.id.tv_detail_branch);
        TextView tvStatus = view.findViewById(R.id.tv_detail_status);
        RecyclerView recyclerServices = view.findViewById(R.id.recycler_services);
        MaterialButton btnEditSkills = view.findViewById(R.id.btn_edit_skills);

        btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        
        tvName.setText(name);
        tvCode.setText(code);
        tvSpecialization.setText(specialization);
        tvBranch.setText("Branch: " + branchId);
        tvStatus.setText(status);
        
        // Dynamically color the status
        if ("AVAILABLE".equalsIgnoreCase(status)) {
            tvStatus.setTextColor(Color.parseColor("#4CAF50")); // Green
        } else if ("BUSY".equalsIgnoreCase(status)) {
            tvStatus.setTextColor(Color.parseColor("#F44336")); // Red
        } else if ("OFF_DUTY".equalsIgnoreCase(status)) {
            tvStatus.setTextColor(Color.parseColor("#9E9E9E")); // Gray
        } else if ("ON_LEAVE".equalsIgnoreCase(status)) {
            tvStatus.setTextColor(Color.parseColor("#FF9800")); // Orange
        }

        btnEditSkills.setOnClickListener(v -> {
            EditSkillsDialogFragment dialog = EditSkillsDialogFragment.newInstance(technicianId);
            dialog.show(getParentFragmentManager(), "EditSkillsDialog");
        });

        // Setup better list for services
        recyclerServices.setLayoutManager(new LinearLayoutManager(getContext()));
        
        viewModel.getTechnicianServices().observe(getViewLifecycleOwner(), services -> {
            if (services != null) {
                recyclerServices.setAdapter(new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
                    @NonNull
                    @Override
                    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        TextView tv = new TextView(parent.getContext());
                        tv.setPadding(32, 24, 32, 24);
                        tv.setTextSize(15f);
                        tv.setTextColor(Color.parseColor("#424242"));
                        tv.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.presence_online, 0, 0, 0);
                        tv.setCompoundDrawablePadding(24);
                        return new RecyclerView.ViewHolder(tv) {};
                    }

                    @Override
                    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                        ((TextView) holder.itemView).setText(services.get(position).getName());
                    }

                    @Override
                    public int getItemCount() {
                        return services.size();
                    }
                });
            }
        });

        // Trigger network loads
        viewModel.loadTechnicianServices(technicianId);
        
        // Listen for when skills dialog finishes saving, so we auto-refresh the UI
        viewModel.getSkillUpdateSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                viewModel.loadTechnicianServices(technicianId);
            }
        });
    }
}
