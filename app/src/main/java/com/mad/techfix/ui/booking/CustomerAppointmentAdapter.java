package com.mad.techfix.ui.booking;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mad.techfix.R;
import com.mad.techfix.models.Appointment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CustomerAppointmentAdapter
        extends RecyclerView.Adapter<
        CustomerAppointmentAdapter.AppointmentViewHolder> {

    public interface OnAppointmentClickListener {
        void onAppointmentClick(Appointment appointment);
    }

    private final List<Appointment> allAppointments =
            new ArrayList<>();

    private final List<Appointment> visibleAppointments =
            new ArrayList<>();

    private final OnAppointmentClickListener listener;

    private String currentFilter = "ALL";


    public CustomerAppointmentAdapter(
            OnAppointmentClickListener listener
    ) {
        this.listener = listener;
    }


    public void setAppointments(
            List<Appointment> appointments
    ) {

        allAppointments.clear();

        if (appointments != null) {
            allAppointments.addAll(
                    appointments
            );
        }

        applyCurrentFilter();
    }


    public void filterByStatus(
            String filter
    ) {

        if (filter == null
                || filter.trim().isEmpty()) {

            currentFilter = "ALL";

        } else {

            currentFilter =
                    filter.trim()
                            .toUpperCase(
                                    Locale.US
                            );
        }

        applyCurrentFilter();
    }


    private void applyCurrentFilter() {

        visibleAppointments.clear();

        for (Appointment appointment :
                allAppointments) {

            if (matchesFilter(
                    appointment,
                    currentFilter
            )) {

                visibleAppointments.add(
                        appointment
                );
            }
        }

        notifyDataSetChanged();
    }


    private boolean matchesFilter(
            Appointment appointment,
            String filter
    ) {

        if ("ALL".equals(filter)) {
            return true;
        }

        if (appointment == null
                || appointment.getStatus() == null) {

            return false;
        }

        String status =
                appointment.getStatus()
                        .trim()
                        .toUpperCase(
                                Locale.US
                        );

        if ("IN_PROGRESS".equals(filter)) {

            return status.equals(
                    "DEVICE_RECEIVED"
            )
                    || status.equals(
                    "DIAGNOSING"
            )
                    || status.equals(
                    "REPAIRING"
            )
                    || status.equals(
                    "TESTING"
            )
                    || status.equals(
                    "READY"
            );
        }

        return status.equals(filter);
    }


    public int getVisibleCount() {
        return visibleAppointments.size();
    }


    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view =
                LayoutInflater
                        .from(
                                parent.getContext()
                        )
                        .inflate(
                                R.layout.item_customer_appointment,
                                parent,
                                false
                        );

        return new AppointmentViewHolder(
                view
        );
    }


    @Override
    public void onBindViewHolder(
            @NonNull AppointmentViewHolder holder,
            int position
    ) {

        Appointment appointment =
                visibleAppointments.get(
                        position
                );

        // Appointment number
        holder.tvAppointmentId.setText(
                safeText(
                        appointment.getAppointment_number(),
                        "Appointment"
                )
        );


        // Device information now comes directly
        // from the backend JOIN.
        String deviceName =
                appointment.getDevice_name();

        if (deviceName == null
                || deviceName.trim().isEmpty()) {

            String brand =
                    safeText(
                            appointment.getDevice_brand(),
                            ""
                    );

            String model =
                    safeText(
                            appointment.getDevice_model(),
                            ""
                    );

            deviceName =
                    (brand + " " + model)
                            .trim();
        }

        holder.tvDevice.setText(
                safeText(
                        deviceName,
                        "Device"
                )
        );


        // Service information from backend JOIN.
        holder.tvService.setText(
                safeText(
                        appointment.getService_name(),
                        "Repair Service"
                )
        );


        // Branch information from backend JOIN.
        String branchDisplay =
                safeText(
                        appointment.getBranch_name(),
                        "TECHFIX Branch"
                );

        if (appointment.getBranch_city() != null
                && !appointment
                .getBranch_city()
                .trim()
                .isEmpty()) {

            branchDisplay +=
                    " - "
                            + appointment
                            .getBranch_city()
                            .trim();
        }

        holder.tvBranch.setText(
                branchDisplay
        );


        // Date
        holder.tvDate.setText(
                safeText(
                        appointment.getRequested_date(),
                        "Not available"
                )
        );


        // Time
        holder.tvTime.setText(
                safeText(
                        appointment.getRequested_time(),
                        "Not specified"
                )
        );


        // Status
        holder.tvStatus.setText(
                formatStatus(
                        appointment.getStatus()
                )
        );


        // Technician name now comes directly
        // from the joined API response.
        String technicianName =
                appointment.getTechnician_name();

        if (appointment.getTechnician_id() == null
                || appointment
                .getTechnician_id()
                .trim()
                .isEmpty()) {

            holder.tvTechnician.setText(
                    "Not assigned yet"
            );

        } else if (technicianName == null
                || technicianName.trim().isEmpty()) {

            holder.tvTechnician.setText(
                    "Technician assigned"
            );

        } else {

            holder.tvTechnician.setText(
                    technicianName.trim()
            );
        }


        holder.itemView.setOnClickListener(
                v -> {

                    int clickedPosition =
                            holder
                                    .getBindingAdapterPosition();

                    if (clickedPosition
                            == RecyclerView.NO_POSITION) {

                        return;
                    }

                    if (listener != null) {

                        listener.onAppointmentClick(
                                visibleAppointments.get(
                                        clickedPosition
                                )
                        );
                    }
                }
        );
    }


    @Override
    public int getItemCount() {
        return visibleAppointments.size();
    }


    private String formatStatus(
            String status
    ) {

        if (status == null
                || status.trim().isEmpty()) {

            return "UNKNOWN";
        }

        return status
                .trim()
                .replace(
                        "_",
                        " "
                )
                .toUpperCase(
                        Locale.US
                );
    }


    private String safeText(
            String value,
            String fallback
    ) {

        if (value == null
                || value.trim().isEmpty()) {

            return fallback;
        }

        return value.trim();
    }


    static class AppointmentViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvAppointmentId;
        TextView tvDevice;
        TextView tvService;
        TextView tvStatus;
        TextView tvBranch;
        TextView tvDate;
        TextView tvTime;
        TextView tvTechnician;


        public AppointmentViewHolder(
                @NonNull View itemView
        ) {

            super(itemView);

            tvAppointmentId =
                    itemView.findViewById(
                            R.id.tv_appointment_id
                    );

            tvDevice =
                    itemView.findViewById(
                            R.id.tv_appointment_device
                    );

            tvService =
                    itemView.findViewById(
                            R.id.tv_appointment_service
                    );

            tvStatus =
                    itemView.findViewById(
                            R.id.tv_appointment_status
                    );

            tvBranch =
                    itemView.findViewById(
                            R.id.tv_appointment_branch
                    );

            tvDate =
                    itemView.findViewById(
                            R.id.tv_appointment_date
                    );

            tvTime =
                    itemView.findViewById(
                            R.id.tv_appointment_time
                    );

            tvTechnician =
                    itemView.findViewById(
                            R.id.tv_appointment_technician
                    );
        }
    }
}