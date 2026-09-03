package com.mad.techfix.ui.booking;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mad.techfix.R;
import com.mad.techfix.models.Appointment;
import com.mad.techfix.models.Device;
import com.mad.techfix.models.admin.Branch;
import com.mad.techfix.models.admin.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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

    private final Map<String, String> deviceNames =
            new HashMap<>();

    private final Map<String, String> serviceNames =
            new HashMap<>();

    private final Map<String, String> branchNames =
            new HashMap<>();

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

    public void setLookupData(
            List<Device> devices,
            List<Service> services,
            List<Branch> branches
    ) {

        deviceNames.clear();
        serviceNames.clear();
        branchNames.clear();

        if (devices != null) {

            for (Device device : devices) {

                if (device == null
                        || device.getId() == null) {
                    continue;
                }

                String name =
                        device.getDisplayName();

                if (name == null
                        || name.trim().isEmpty()) {

                    name = "Device";
                }

                deviceNames.put(
                        device.getId(),
                        name
                );
            }
        }

        if (services != null) {

            for (Service service : services) {

                if (service == null
                        || service.getId() == null) {
                    continue;
                }

                String name =
                        service.getName();

                if (name == null
                        || name.trim().isEmpty()) {

                    name = "Repair Service";
                }

                serviceNames.put(
                        service.getId(),
                        name
                );
            }
        }

        if (branches != null) {

            for (Branch branch : branches) {

                if (branch == null
                        || branch.getId() == null) {
                    continue;
                }

                String name =
                        branch.getName();

                if (name == null
                        || name.trim().isEmpty()) {

                    name = "TECHFIX Branch";
                }

                branchNames.put(
                        branch.getId(),
                        name
                );
            }
        }

        notifyDataSetChanged();
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

        String appointmentNumber =
                appointment
                        .getAppointment_number();

        holder.tvAppointmentId.setText(
                safeText(
                        appointmentNumber,
                        "Appointment"
                )
        );

        String deviceName =
                deviceNames.get(
                        appointment.getDevice_id()
                );

        holder.tvDevice.setText(
                safeText(
                        deviceName,
                        "Device"
                )
        );

        String serviceName =
                serviceNames.get(
                        appointment.getService_id()
                );

        holder.tvService.setText(
                safeText(
                        serviceName,
                        "Repair Service"
                )
        );

        String branchName =
                branchNames.get(
                        appointment.getBranch_id()
                );

        holder.tvBranch.setText(
                safeText(
                        branchName,
                        "TECHFIX Branch"
                )
        );

        String date =
                appointment
                        .getRequested_date();

        holder.tvDate.setText(
                safeText(
                        date,
                        "Not available"
                )
        );

        String time =
                appointment
                        .getRequested_time();

        holder.tvTime.setText(
                safeText(
                        time,
                        "Not specified"
                )
        );

        String status =
                appointment.getStatus();

        holder.tvStatus.setText(
                formatStatus(
                        status
                )
        );

        String technicianId =
                appointment
                        .getTechnician_id();

        if (technicianId == null
                || technicianId
                .trim()
                .isEmpty()) {

            holder.tvTechnician.setText(
                    "Not assigned yet"
            );

        } else {

            holder.tvTechnician.setText(
                    "Technician assigned"
            );
        }

        holder.itemView
                .setOnClickListener(
                        v -> {

                            int clickedPosition =
                                    holder
                                            .getBindingAdapterPosition();

                            if (clickedPosition
                                    == RecyclerView.NO_POSITION) {

                                return;
                            }

                            if (listener != null) {

                                listener
                                        .onAppointmentClick(
                                                visibleAppointments
                                                        .get(
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

        return value;
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