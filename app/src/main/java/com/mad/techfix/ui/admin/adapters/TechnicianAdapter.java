package com.mad.techfix.ui.admin.adapters;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mad.techfix.R;
import com.mad.techfix.Technician;

import java.util.List;

public class TechnicianAdapter extends RecyclerView.Adapter<TechnicianAdapter.TechnicianViewHolder> {
    private List<Technician> technicianList;
    private OnTechnicianClickListener listener;

    public interface OnTechnicianClickListener {
        void onTechnicianClick(Technician technician);
    }

    public TechnicianAdapter(List<Technician> technicianList, OnTechnicianClickListener listener) {
        this.technicianList = technicianList;
        this.listener = listener;
    }

    public void updateData(List<Technician> newList) {
        this.technicianList = newList;
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
        holder.bind(technicianList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return technicianList != null ? technicianList.size() : 0;
    }

    static class TechnicianViewHolder extends RecyclerView.ViewHolder {
        TextView tvAvatar, tvFullName, tvEmployeeCode, tvSpecialization, tvBranchName, tvStatus, tvStatusDot;

        public TechnicianViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAvatar = itemView.findViewById(R.id.tvAvatar);
            tvFullName = itemView.findViewById(R.id.tvFullName);
            tvEmployeeCode = itemView.findViewById(R.id.tvEmployeeCode);
            tvSpecialization = itemView.findViewById(R.id.tvSpecialization);
            tvBranchName = itemView.findViewById(R.id.tvBranchName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvStatusDot = itemView.findViewById(R.id.tvStatusDot);
        }

        public void bind(Technician technician, OnTechnicianClickListener listener) {
            String name = technician.getFullName();
            if (tvFullName != null) tvFullName.setText(name);
            if (tvEmployeeCode != null) tvEmployeeCode.setText(technician.getEmployeeCode());
            if (tvSpecialization != null) tvSpecialization.setText(technician.getSpecialization());
            
            // Using branchId if branchName is not directly available, but casting safely
            if (tvBranchName != null) {
                tvBranchName.setText(technician.getBranchId() != null ? String.valueOf(technician.getBranchId()) : "");
            }
            
            String status = technician.getStatus();
            if (tvStatus != null) {
                tvStatus.setText(status != null ? status : "");
            }

            if (tvAvatar != null && name != null && !name.isEmpty()) {
                tvAvatar.setText(name.substring(0, 1).toUpperCase());
                tvAvatar.setTextColor(Color.WHITE);
                GradientDrawable bgShape = new GradientDrawable();
                bgShape.setShape(GradientDrawable.OVAL);
                bgShape.setColor(0xFF1565C0);
                tvAvatar.setBackground(bgShape);
            }

            if (tvStatusDot != null && status != null) {
                GradientDrawable dotShape = new GradientDrawable();
                dotShape.setShape(GradientDrawable.OVAL);
                switch (status.toUpperCase()) {
                    case "AVAILABLE":
                        dotShape.setColor(0xFF4CAF50);
                        break;
                    case "BUSY":
                        dotShape.setColor(0xFFF44336);
                        break;
                    case "OFF_DUTY":
                        dotShape.setColor(0xFF9E9E9E);
                        break;
                    case "ON_LEAVE":
                        dotShape.setColor(0xFFFF9800);
                        break;
                    default:
                        dotShape.setColor(0xFF9E9E9E);
                        break;
                }
                tvStatusDot.setBackground(dotShape);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTechnicianClick(technician);
                }
            });
        }
    }
}
