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
        TextInputEditText etEmail = view.findViewById(R.id.et_branch_email);
        TextInputEditText etLat = view.findViewById(R.id.et_branch_lat);
        TextInputEditText etLng = view.findViewById(R.id.et_branch_lng);
        TextInputEditText etOpening = view.findViewById(R.id.et_branch_opening);
        TextInputEditText etClosing = view.findViewById(R.id.et_branch_closing);
        
        View btnSave = view.findViewById(R.id.btn_save);
        View btnCancel = view.findViewById(R.id.btn_cancel);
        View btnDelete = view.findViewById(R.id.btn_delete);

        if (branch != null) {
            tvTitle.setText("Edit Branch");
            etName.setText(branch.getName());
            etAddress.setText(branch.getAddress());
            etCity.setText(branch.getCity());
            etPhone.setText(branch.getPhone());
            etEmail.setText(branch.getEmail());
            etLat.setText(String.valueOf(branch.getLatitude()));
            etLng.setText(String.valueOf(branch.getLongitude()));
            etOpening.setText(branch.getOpeningTime());
            etClosing.setText(branch.getClosingTime());
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
            String nameStr = etName.getText() != null ? etName.getText().toString().trim() : "";
            String addressStr = etAddress.getText() != null ? etAddress.getText().toString().trim() : "";
            String cityStr = etCity.getText() != null ? etCity.getText().toString().trim() : "";
            String phoneStr = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";

            if (nameStr.isEmpty()) { etName.setError("Name is required"); etName.requestFocus(); return; }
            if (addressStr.isEmpty()) { etAddress.setError("Address is required"); etAddress.requestFocus(); return; }
            if (cityStr.isEmpty()) { etCity.setError("City is required"); etCity.requestFocus(); return; }
            if (phoneStr.isEmpty()) { etPhone.setError("Phone is required"); etPhone.requestFocus(); return; }

            Branch b = branch != null ? branch : new Branch();
            b.setName(nameStr);
            b.setAddress(addressStr);
            b.setCity(cityStr);
            b.setPhone(phoneStr);
            b.setEmail(etEmail.getText().toString());
            b.setOpeningTime(etOpening.getText().toString());
            b.setClosingTime(etClosing.getText().toString());
            
            try { b.setLatitude(Double.parseDouble(etLat.getText().toString())); } catch(Exception ignored){}
            try { b.setLongitude(Double.parseDouble(etLng.getText().toString())); } catch(Exception ignored){}

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
                Toast.makeText(getContext(), "Operation Successful!", Toast.LENGTH_SHORT).show();
                viewModel.loadBranches(); // Reload list on DB update
            }
        });
    }
}
