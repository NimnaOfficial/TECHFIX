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
    private final OnTechnicianClickListener listener;

    public interface OnTechnicianClickListener {
        void onTechnicianClick(Technician technician);
    }

    public TechnicianAdapter(OnTechnicianClickListener listener) {
        this.listener = listener;
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
        Technician technician = technicianList.get(position);
        holder.bind(technician, listener);
    }

    @Override
    public int getItemCount() {
        return technicianList == null ? 0 : technicianList.size();
    }

    static class TechnicianViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivAvatar;
        private final TextView tvName;
        private final TextView tvEmpCode;
        private final TextView tvSpecialization;
        private final TextView tvBranchName;
        private final View vStatusDot;
        private final TextView tvStatus;

        public TechnicianViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_avatar);
            tvName = itemView.findViewById(R.id.tv_name);
            tvEmpCode = itemView.findViewById(R.id.tv_emp_code);
            tvSpecialization = itemView.findViewById(R.id.tv_specialization);
            tvBranchName = itemView.findViewById(R.id.tv_branch_name);
            vStatusDot = itemView.findViewById(R.id.v_status_dot);
            tvStatus = itemView.findViewById(R.id.tv_status);
        }

        public void bind(Technician technician, OnTechnicianClickListener listener) {
            tvName.setText(technician.getFullName());
            tvEmpCode.setText(technician.getEmployeeCode());
            tvSpecialization.setText(technician.getSpecialization());
            tvBranchName.setText("Branch: " + technician.getBranchId());

            String status = technician.getAvailabilityStatus();
            tvStatus.setText(status != null ? status : "UNKNOWN");
            
            // Set status dot background
            int dotResId = R.drawable.bg_status_dot_available;
            if ("BUSY".equalsIgnoreCase(status)) {
                dotResId = R.drawable.bg_status_dot_busy;
            } else if ("OFF_DUTY".equalsIgnoreCase(status)) {
                dotResId = R.drawable.bg_status_dot_off_duty;
            } else if ("ON_LEAVE".equalsIgnoreCase(status)) {
                dotResId = R.drawable.bg_status_dot_on_leave;
            }
            vStatusDot.setBackgroundResource(dotResId);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTechnicianClick(technician);
                }
            });
        }
    }
}
