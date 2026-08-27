package com.mad.techfix.ui.admin.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mad.techfix.R;
import com.mad.techfix.Branch; // Assuming Branch model exists in root package or will be created

import java.util.List;

public class BranchAdapter extends RecyclerView.Adapter<BranchAdapter.BranchViewHolder> {
    private List<Branch> branchList;
    private OnBranchClickListener listener;

    public interface OnBranchClickListener {
        void onBranchClick(Branch branch);
    }

    public BranchAdapter(List<Branch> branchList, OnBranchClickListener listener) {
        this.branchList = branchList;
        this.listener = listener;
    }

    public void updateData(List<Branch> newList) {
        this.branchList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BranchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_branch, parent, false);
        return new BranchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BranchViewHolder holder, int position) {
        Branch branch = branchList.get(position);
        holder.bind(branch, listener);
    }

    @Override
    public int getItemCount() {
        return branchList != null ? branchList.size() : 0;
    }

    static class BranchViewHolder extends RecyclerView.ViewHolder {
        TextView tvBranchName, tvAddress, tvPhone, tvEmail, tvTime, tvCityChip;

        public BranchViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBranchName = itemView.findViewById(R.id.tvBranchName);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvCityChip = itemView.findViewById(R.id.tvCityChip);
        }

        public void bind(Branch branch, OnBranchClickListener listener) {
            if (tvBranchName != null) tvBranchName.setText(branch.getName());
            if (tvAddress != null) tvAddress.setText(branch.getAddress()); 
            if (tvPhone != null) tvPhone.setText(branch.getPhone());
            if (tvEmail != null) tvEmail.setText(branch.getEmail());
            if (tvTime != null) {
                String open = branch.getOpeningTime() != null ? branch.getOpeningTime() : "";
                String close = branch.getClosingTime() != null ? branch.getClosingTime() : "";
                tvTime.setText(open + " - " + close);
            }
            if (tvCityChip != null) tvCityChip.setText(branch.getCity());

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onBranchClick(branch);
                }
            });
        }
    }
}
