package com.mad.techfix.ui.admin.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.mad.techfix.R;
import com.mad.techfix.models.Appointment;
import java.util.ArrayList;
import java.util.List;

public class DashboardAppointmentAdapter extends RecyclerView.Adapter<DashboardAppointmentAdapter.ViewHolder> {

    private List<Appointment> appointmentList = new ArrayList<>();
    private final OnAssignClickListener listener;

    public interface OnAssignClickListener {
        void onAssignClick(Appointment appointment);
    }

    public DashboardAppointmentAdapter(OnAssignClickListener listener) {
        this.listener = listener;
    }

    public void updateData(List<Appointment> newAppointments) {
        this.appointmentList = newAppointments;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dashboard_appointment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(appointmentList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return appointmentList == null ? 0 : appointmentList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvApptNumber;
        private final TextView tvStatusBadge; private final com.google.android.material.card.MaterialCardView cardStatusBadge;
        private final TextView tvApptDate;
        private final Button btnQuickAssign;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvApptNumber = itemView.findViewById(R.id.tv_appt_number);
            tvStatusBadge = itemView.findViewById(R.id.tv_status_badge);
            cardStatusBadge = itemView.findViewById(R.id.card_status_badge);
            tvApptDate = itemView.findViewById(R.id.tv_appt_date);
            btnQuickAssign = itemView.findViewById(R.id.btn_quick_assign);
        }

        public void bind(Appointment appointment, OnAssignClickListener listener) {
            tvApptNumber.setText(appointment.getAppointment_number());
            tvApptDate.setText(appointment.getRequested_date());
            
            String status = appointment.getStatus();
            tvStatusBadge.setText(status);

            // Dynamic badge coloring
            if (status != null) {
                switch (status.toUpperCase()) {
                    case "REQUESTED":
                        cardStatusBadge.setCardBackgroundColor(android.graphics.Color.parseColor("#FF9800")); tvStatusBadge.setTextColor(android.graphics.Color.WHITE); // Orange
                        break;
                    case "ASSIGNED":
                    case "DEVICE_RECEIVED":
                    case "DIAGNOSING":
                    case "REPAIRING":
                    case "TESTING":
                        cardStatusBadge.setCardBackgroundColor(android.graphics.Color.parseColor("#1E88E5")); tvStatusBadge.setTextColor(android.graphics.Color.WHITE); // Blue
                        break;
                    case "READY":
                    case "COMPLETED":
                        cardStatusBadge.setCardBackgroundColor(android.graphics.Color.parseColor("#4CAF50")); tvStatusBadge.setTextColor(android.graphics.Color.WHITE); // Green
                        break;
                    case "CANCELLED":
                        cardStatusBadge.setCardBackgroundColor(android.graphics.Color.parseColor("#F44336")); tvStatusBadge.setTextColor(android.graphics.Color.WHITE); // Red
                        break;
                    default:
                        cardStatusBadge.setCardBackgroundColor(android.graphics.Color.parseColor("#9E9E9E")); tvStatusBadge.setTextColor(android.graphics.Color.WHITE); // Gray
                        break;
                }
            }

            if ("REQUESTED".equalsIgnoreCase(status)) {
                btnQuickAssign.setVisibility(View.VISIBLE);
                btnQuickAssign.setText("Assign Tech");
            } else {
                btnQuickAssign.setVisibility(View.GONE);
            }
            
            // Allow whole card to be clicked to view details
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAssignClick(appointment);
                }
            });
            
            btnQuickAssign.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAssignClick(appointment);
                }
            });
        }
    }
}

