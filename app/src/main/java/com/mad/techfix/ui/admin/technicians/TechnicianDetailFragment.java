package com.mad.techfix.ui.admin.technicians;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
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
import java.util.ArrayList;
import java.util.List;

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
        TextView tvHireDate = view.findViewById(R.id.tv_detail_hire_date);
        RecyclerView recyclerServices = view.findViewById(R.id.recycler_services);
        MaterialButton btnEditSkills = view.findViewById(R.id.btn_edit_skills);

        btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        
        tvName.setText(name);
        tvCode.setText(code);
        tvSpecialization.setText(specialization);
        tvBranch.setText("Branch: " + branchId);
        tvStatus.setText(status);
        tvHireDate.setText("Hired: " + hireDate);

        btnEditSkills.setOnClickListener(v -> {
            EditSkillsDialogFragment dialog = EditSkillsDialogFragment.newInstance(technicianId);
            dialog.show(getParentFragmentManager(), "EditSkillsDialog");
        });

        // Set up basic services list
        recyclerServices.setLayoutManager(new LinearLayoutManager(getContext()));
        
        viewModel.getTechnicianServices().observe(getViewLifecycleOwner(), services -> {
            if (services != null) {
                // simple anonymous adapter just for text
                recyclerServices.setAdapter(new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
                    @NonNull
                    @Override
                    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        TextView tv = new TextView(parent.getContext());
                        tv.setPadding(32, 16, 32, 16);
                        tv.setTextSize(16f);
                        return new RecyclerView.ViewHolder(tv) {};
                    }

                    @Override
                    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                        ((TextView) holder.itemView).setText("• " + services.get(position).getName());
                    }

                    @Override
                    public int getItemCount() {
                        return services.size();
                    }
                });
            }
        });

        viewModel.loadTechnicianServices(technicianId);
    }
}
