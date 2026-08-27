package com.mad.techfix.ui.admin.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.mad.techfix.R;
import com.mad.techfix.models.admin.Technician;
import java.util.ArrayList;
import java.util.List;

public class TechnicianAdapter extends RecyclerView.Adapter<TechnicianAdapter.TechnicianViewHolder> {

    private List<Technician> technicianList = new ArrayList<>();
    private final OnTechnicianClickListener clickListener;
    private final OnTechnicianLongClickListener longClickListener;

    public interface OnTechnicianClickListener {
        void onTechnicianClick(Technician technician);
    }

    public interface OnTechnicianLongClickListener {
        void onTechnicianLongClick(Technician technician);
    }

    public TechnicianAdapter(OnTechnicianClickListener clickListener, OnTechnicianLongClickListener longClickListener) {
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
    }

    public void updateData(List<Technician> newTechnicians) {
        this.technicianList = newTechnicians;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TechnicianViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_technician, parent, false);
        return new TechnicianViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TechnicianViewHolder holder, int position) {
        holder.bind(technicianList.get(position), clickListener, longClickListener);
    }

    @Override
    public int getItemCount() {
        return technicianList == null ? 0 : technicianList.size();
    }

    static class TechnicianViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName;
        private final TextView tvCode;
        private final TextView tvSpecialization;
        private final TextView tvBranch;
        private final View statusIndicator;
        private final TextView tvStatusBadge;

        public TechnicianViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvCode = itemView.findViewById(R.id.tv_emp_code);
            tvSpecialization = itemView.findViewById(R.id.tv_specialization);
            tvBranch = itemView.findViewById(R.id.tv_branch_name);
            statusIndicator = itemView.findViewById(R.id.v_status_dot);
            tvStatusBadge = itemView.findViewById(R.id.tv_status);
        }

        public void bind(Technician technician, OnTechnicianClickListener clickListener, OnTechnicianLongClickListener longClickListener) {
            tvName.setText(technician.getFullName());
            tvCode.setText(technician.getEmployeeCode());
            tvSpecialization.setText(technician.getSpecialization());
            tvBranch.setText("Branch: " + technician.getBranchId());

            String status = technician.getAvailabilityStatus();
            tvStatusBadge.setText(status != null ? status : "UNKNOWN");

            if ("AVAILABLE".equalsIgnoreCase(status)) {
                statusIndicator.setBackgroundResource(R.drawable.bg_status_dot_available);
                tvStatusBadge.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_green_dark));
            } else if ("BUSY".equalsIgnoreCase(status)) {
                statusIndicator.setBackgroundResource(R.drawable.bg_status_dot_busy);
                tvStatusBadge.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
            } else if ("OFF_DUTY".equalsIgnoreCase(status)) {
                statusIndicator.setBackgroundResource(R.drawable.bg_status_dot_off_duty);
                tvStatusBadge.setTextColor(itemView.getContext().getResources().getColor(android.R.color.darker_gray));
            } else if ("ON_LEAVE".equalsIgnoreCase(status)) {
                statusIndicator.setBackgroundResource(R.drawable.bg_status_dot_on_leave);
                tvStatusBadge.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_orange_dark));
            }

            itemView.setOnClickListener(v -> {
                if (clickListener != null) clickListener.onTechnicianClick(technician);
            });

            itemView.setOnLongClickListener(v -> {
                if (longClickListener != null) longClickListener.onTechnicianLongClick(technician);
                return true;
            });
        }
    }
}
