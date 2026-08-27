package com.mad.techfix.ui.admin.branches;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.mad.techfix.R;
import com.mad.techfix.ui.admin.AdminViewModel;

public class BranchListFragment extends Fragment {

    private AdminViewModel viewModel;
    private RecyclerView rvBranches;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_branch_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        viewModel = new ViewModelProvider(requireActivity()).get(AdminViewModel.class);
        rvBranches = view.findViewById(R.id.rv_branches);
        rvBranches.setLayoutManager(new LinearLayoutManager(getContext()));

        viewModel.getBranches().observe(getViewLifecycleOwner(), branches -> {
            if (branches != null) {
                // Update adapter
            }
        });

        viewModel.loadBranches();
    }
}
