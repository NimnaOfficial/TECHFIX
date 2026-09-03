package com.mad.techfix.ui.booking;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.radiobutton.MaterialRadioButton;
import com.mad.techfix.R;
import com.mad.techfix.models.admin.Branch;

import java.util.ArrayList;
import java.util.List;

public class BookingBranchAdapter
        extends RecyclerView.Adapter<BookingBranchAdapter.BranchViewHolder> {

    public interface OnBranchSelectedListener {
        void onBranchSelected(Branch branch);
    }

    private final List<Branch> branches = new ArrayList<>();
    private final OnBranchSelectedListener listener;

    private int selectedPosition = RecyclerView.NO_POSITION;

    public BookingBranchAdapter(OnBranchSelectedListener listener) {
        this.listener = listener;
    }

    public void setBranches(List<Branch> newBranches) {
        branches.clear();

        if (newBranches != null) {
            branches.addAll(newBranches);
        }

        selectedPosition = RecyclerView.NO_POSITION;
        notifyDataSetChanged();
    }

    public Branch getSelectedBranch() {
        if (selectedPosition == RecyclerView.NO_POSITION) {
            return null;
        }

        return branches.get(selectedPosition);
    }

    @NonNull
    @Override
    public BranchViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_booking_branch, parent, false);

        return new BranchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull BranchViewHolder holder,
            int position
    ) {

        Branch branch = branches.get(position);

        String name = branch.getName();

        if (name == null || name.trim().isEmpty()) {
            name = "TECHFIX Branch";
        }

        holder.tvBranchName.setText(name);

        String city = branch.getCity();

        if (city == null || city.trim().isEmpty()) {
            city = "City unavailable";
        }

        holder.tvBranchCity.setText(city);

        String address = branch.getAddress();

        if (address == null || address.trim().isEmpty()) {
            address = "Address unavailable";
        }

        holder.tvBranchAddress.setText(address);

        String openingTime = branch.getOpeningTime();
        String closingTime = branch.getClosingTime();

        if (openingTime == null || openingTime.trim().isEmpty()
                || closingTime == null || closingTime.trim().isEmpty()) {

            holder.tvBranchHours.setText("Opening hours unavailable");

        } else {

            holder.tvBranchHours.setText(
                    "Open " + openingTime + " - " + closingTime
            );
        }

        holder.radioSelectBranch.setChecked(
                selectedPosition == holder.getBindingAdapterPosition()
        );

        holder.itemView.setOnClickListener(v -> {

            int clickedPosition = holder.getBindingAdapterPosition();

            if (clickedPosition == RecyclerView.NO_POSITION) {
                return;
            }

            int previousPosition = selectedPosition;
            selectedPosition = clickedPosition;

            if (previousPosition != RecyclerView.NO_POSITION) {
                notifyItemChanged(previousPosition);
            }

            notifyItemChanged(selectedPosition);

            if (listener != null) {
                listener.onBranchSelected(
                        branches.get(selectedPosition)
                );
            }
        });
    }

    @Override
    public int getItemCount() {
        return branches.size();
    }

    static class BranchViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvBranchName;
        TextView tvBranchCity;
        TextView tvBranchAddress;
        TextView tvBranchHours;

        MaterialRadioButton radioSelectBranch;

        public BranchViewHolder(@NonNull View itemView) {
            super(itemView);

            tvBranchName =
                    itemView.findViewById(
                            R.id.tv_branch_name
                    );

            tvBranchCity =
                    itemView.findViewById(
                            R.id.tv_branch_city
                    );

            tvBranchAddress =
                    itemView.findViewById(
                            R.id.tv_branch_address
                    );

            tvBranchHours =
                    itemView.findViewById(
                            R.id.tv_branch_hours
                    );

            radioSelectBranch =
                    itemView.findViewById(
                            R.id.radio_select_branch
                    );
        }
    }
}