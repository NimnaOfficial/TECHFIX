package com.mad.techfix.ui.admin.branches;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.mad.techfix.R;
import com.mad.techfix.models.admin.Branch;
import com.mad.techfix.ui.admin.adapters.BranchAdapter;
import com.mad.techfix.viewmodel.AdminViewModel;

public class BranchListFragment extends Fragment {

    private AdminViewModel viewModel;
    private BranchAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvEmptyState;
    private RecyclerView recyclerBranches;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_branch_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity()).get(AdminViewModel.class);

        recyclerBranches = view.findViewById(R.id.recycler_branches);
        progressBar = view.findViewById(R.id.progress_bar);
        tvEmptyState = view.findViewById(R.id.tv_empty_state);
        FloatingActionButton fabAdd = view.findViewById(R.id.fab_add_branch);

        recyclerBranches.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // Tap a branch to Edit/Delete
        adapter = new BranchAdapter(this::showBranchDialog);
        recyclerBranches.setAdapter(adapter);

        fabAdd.setOnClickListener(v -> showBranchDialog(null));

        observeViewModel();
        viewModel.loadBranches();
    }

    private void showBranchDialog(@Nullable Branch branch) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_branch_form, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        TextView tvTitle = view.findViewById(R.id.tv_form_title);
        TextInputEditText etName = view.findViewById(R.id.et_branch_name);
        TextInputEditText etAddress = view.findViewById(R.id.et_branch_address);
        TextInputEditText etCity = view.findViewById(R.id.et_branch_city);
        TextInputEditText etPhone = view.findViewById(R.id.et_branch_phone);
        View btnSave = view.findViewById(R.id.btn_save);
        View btnCancel = view.findViewById(R.id.btn_cancel);
        View btnDelete = view.findViewById(R.id.btn_delete);

        if (branch != null) {
            tvTitle.setText("Edit Branch");
            etName.setText(branch.getName());
            etAddress.setText(branch.getAddress());
            etCity.setText(branch.getCity());
            etPhone.setText(branch.getPhone());
            btnDelete.setVisibility(View.VISIBLE);
        }

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnDelete.setOnClickListener(v -> {
            if (branch != null) {
                viewModel.deleteBranch(branch.getId());
                dialog.dismiss();
            }
        });

        btnSave.setOnClickListener(v -> {
            Branch b = branch != null ? branch : new Branch();
            b.setName(etName.getText().toString());
            b.setAddress(etAddress.getText().toString());
            b.setCity(etCity.getText().toString());
            b.setPhone(etPhone.getText().toString());

            if (branch == null) {
                viewModel.createBranch(b);
            } else {
                viewModel.updateBranch(b.getId(), b);
            }
            dialog.dismiss();
        });
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getBranches().observe(getViewLifecycleOwner(), branches -> {
            if (branches != null && !branches.isEmpty()) {
                adapter.updateData(branches);
                tvEmptyState.setVisibility(View.GONE);
                recyclerBranches.setVisibility(View.VISIBLE);
            } else {
                tvEmptyState.setVisibility(View.VISIBLE);
                recyclerBranches.setVisibility(View.GONE);
            }
        });

        viewModel.getCrudSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                Toast.makeText(getContext(), "Success!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
