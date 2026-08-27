package com.mad.techfix.ui.admin.adapters;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.mad.techfix.R;
import com.mad.techfix.models.admin.Branch;
import java.util.ArrayList;
import java.util.List;

public class BranchAdapter extends RecyclerView.Adapter<BranchAdapter.BranchViewHolder> {

    private List<Branch> branchList = new ArrayList<>();
    private final OnBranchClickListener listener;

    public interface OnBranchClickListener {
        void onBranchClick(Branch branch);
    }

    public BranchAdapter(OnBranchClickListener listener) {
        this.listener = listener;
    }

    public void updateData(List<Branch> newBranches) {
        this.branchList = newBranches;
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
        return branchList == null ? 0 : branchList.size();
    }

    static class BranchViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvBranchName;
        private final TextView tvCityChip;
        private final TextView tvAddress;
        private final TextView tvPhone;
        private final TextView tvEmail;
        private final TextView tvOpeningTimes;
        private final MaterialButton btnMapDirections;

        public BranchViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBranchName = itemView.findViewById(R.id.tv_branch_name);
            tvCityChip = itemView.findViewById(R.id.tv_city_chip);
            tvAddress = itemView.findViewById(R.id.tv_address);
            tvPhone = itemView.findViewById(R.id.tv_phone);
            tvEmail = itemView.findViewById(R.id.tv_email);
            tvOpeningTimes = itemView.findViewById(R.id.tv_opening_times);
            btnMapDirections = itemView.findViewById(R.id.btn_map_directions);
        }

        public void bind(Branch branch, OnBranchClickListener listener) {
            tvBranchName.setText(branch.getName());
            tvCityChip.setText(branch.getCity());
            tvAddress.setText(branch.getFullAddress());
            tvPhone.setText(branch.getPhone());
            tvEmail.setText(branch.getEmail());
            
            String times = branch.getOpeningTime() + " - " + branch.getClosingTime();
            tvOpeningTimes.setText(times);

            // Setup Google Maps Intent
            btnMapDirections.setOnClickListener(v -> {
                String uriStr = "geo:" + branch.getLatitude() + "," + branch.getLongitude() 
                        + "?q=" + branch.getLatitude() + "," + branch.getLongitude() + "(" + branch.getName() + ")";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uriStr));
                intent.setPackage("com.google.android.apps.maps");
                if (intent.resolveActivity(itemView.getContext().getPackageManager()) != null) {
                    itemView.getContext().startActivity(intent);
                } else {
                    // Fallback to browser if Maps app isn't installed
                    Intent fallback = new Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com/?q=" + branch.getLatitude() + "," + branch.getLongitude()));
                    itemView.getContext().startActivity(fallback);
                }
            });

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onBranchClick(branch);
                }
            });
        }
    }
}
