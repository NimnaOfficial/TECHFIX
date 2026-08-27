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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.mad.techfix.R;
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

        recyclerBranches.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BranchAdapter(branch -> 
            Toast.makeText(getContext(), "Selected Branch: " + branch.getName(), Toast.LENGTH_SHORT).show()
        );
        recyclerBranches.setAdapter(adapter);

        observeViewModel();
        viewModel.loadBranches();
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
    }
}
