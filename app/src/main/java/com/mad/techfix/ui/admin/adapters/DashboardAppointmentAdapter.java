package com.mad.techfix.ui.admin.adapters;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mad.techfix.R;
import com.mad.techfix.Appointment;

import java.util.List;

public class DashboardAppointmentAdapter extends RecyclerView.Adapter<DashboardAppointmentAdapter.DashboardAppointmentViewHolder> {
    private List<Appointment> appointmentList;
    private OnAssignClickListener listener;

    public interface OnAssignClickListener {
        void onAssignClick(Appointment appointment);
    }

    public DashboardAppointmentAdapter(List<Appointment> appointmentList, OnAssignClickListener listener) {
        this.appointmentList = appointmentList;
        this.listener = listener;
    }

    public void updateData(List<Appointment> newList) {
        this.appointmentList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DashboardAppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dashboard_appointment, parent, false);
        return new DashboardAppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DashboardAppointmentViewHolder holder, int position) {
        holder.bind(appointmentList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return appointmentList != null ? appointmentList.size() : 0;
    }

    static class DashboardAppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView tvAppointmentNumber, tvStatus, tvRequestedDate;
        Button btnAssign;

        public DashboardAppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAppointmentNumber = itemView.findViewById(R.id.tvAppointmentNumber);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvRequestedDate = itemView.findViewById(R.id.tvRequestedDate);
            btnAssign = itemView.findViewById(R.id.btnAssign);
        }

        public void bind(Appointment appointment, OnAssignClickListener listener) {
            if (tvAppointmentNumber != null) {
                tvAppointmentNumber.setText(appointment.getAppointmentNumber() != null ? appointment.getAppointmentNumber() : "");
            }
            if (tvRequestedDate != null) {
                tvRequestedDate.setText(appointment.getRequestedDate() != null ? appointment.getRequestedDate() : "");
            }
            
            String status = appointment.getStatus();
            if (tvStatus != null && status != null) {
                tvStatus.setText(status);
                GradientDrawable badgeShape = new GradientDrawable();
                badgeShape.setShape(GradientDrawable.RECTANGLE);
                badgeShape.setCornerRadius(16f);
                
                int badgeColor = 0xFF9E9E9E; // Default gray
                switch (status.toUpperCase()) {
                    case "REQUESTED":
                        badgeColor = 0xFFFF9800;
                        break;
                    case "ASSIGNED":
                        badgeColor = 0xFF1565C0;
                        break;
                    case "DIAGNOSING":
                        badgeColor = 0xFF1E88E5;
                        break;
                    case "REPAIRING":
                        badgeColor = 0xFF42A5F5;
                        break;
                    case "TESTING":
                        badgeColor = 0xFF7E57C2;
                        break;
                    case "READY":
                        badgeColor = 0xFF4CAF50;
                        break;
                    case "COMPLETED":
                        badgeColor = 0xFF2E7D32;
                        break;
                    case "CANCELLED":
                        badgeColor = 0xFFF44336;
                        break;
                }
                badgeShape.setColor(badgeColor);
                tvStatus.setBackground(badgeShape);
                tvStatus.setTextColor(Color.WHITE);
            }

            if (btnAssign != null) {
                if (status != null && status.equalsIgnoreCase("REQUESTED")) {
                    btnAssign.setVisibility(View.VISIBLE);
                    btnAssign.setOnClickListener(v -> {
                        if (listener != null) {
                            listener.onAssignClick(appointment);
                        }
                    });
                } else {
                    btnAssign.setVisibility(View.GONE);
                }
            }
        }
    }
}
