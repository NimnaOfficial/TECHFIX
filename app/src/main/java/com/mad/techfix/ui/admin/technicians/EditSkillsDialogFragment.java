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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.mad.techfix.R;
import com.mad.techfix.models.admin.Service;
import com.mad.techfix.ui.admin.adapters.ServiceCheckboxAdapter;
import com.mad.techfix.viewmodel.AdminViewModel;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EditSkillsDialogFragment extends DialogFragment {

    private AdminViewModel viewModel;
    private ServiceCheckboxAdapter adapter;
    private String technicianId;

    public static EditSkillsDialogFragment newInstance(String technicianId) {
        EditSkillsDialogFragment fragment = new EditSkillsDialogFragment();
        Bundle args = new Bundle();
        args.putString("technicianId", technicianId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            technicianId = getArguments().getString("technicianId");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_edit_skills, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity()).get(AdminViewModel.class);

        RecyclerView recyclerServices = view.findViewById(R.id.recycler_all_services);
        Button btnCancel = view.findViewById(R.id.btn_cancel);
        Button btnSave = view.findViewById(R.id.btn_save);

        recyclerServices.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ServiceCheckboxAdapter();
        recyclerServices.setAdapter(adapter);

        btnCancel.setOnClickListener(v -> dismiss());

        btnSave.setOnClickListener(v -> {
            List<String> selectedIds = adapter.getSelectedServiceIds();
            viewModel.updateTechnicianServices(technicianId, selectedIds);
        });

        // We need both all services and the technician's current services to setup the checkboxes correctly
        viewModel.getAllServices().observe(getViewLifecycleOwner(), allServices -> {
            if (allServices != null) {
                // Fetch current ones
                viewModel.getTechnicianServices().observe(getViewLifecycleOwner(), techServices -> {
                    if (techServices != null) {
                        Set<String> assignedIds = new HashSet<>();
                        for (Service s : techServices) {
                            assignedIds.add(s.getId());
                        }
                        adapter.updateData(allServices, assignedIds);
                    }
                });
            }
        });

        viewModel.getSkillUpdateSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                Toast.makeText(getContext(), "Skills updated", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });

        viewModel.loadAllServices();
        viewModel.loadTechnicianServices(technicianId);
    }
}

