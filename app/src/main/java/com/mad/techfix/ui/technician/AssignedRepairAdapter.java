package com.mad.techfix.ui.technician;

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

public class AssignedRepairAdapter
        extends RecyclerView.Adapter<
        AssignedRepairAdapter.RepairViewHolder> {

    public interface OnRepairClickListener {

        void onRepairClick(
                Appointment repair
        );
    }


    private final List<Appointment> repairs =
            new ArrayList<>();


    private final OnRepairClickListener listener;


    public AssignedRepairAdapter(
            OnRepairClickListener listener
    ) {

        this.listener =
                listener;
    }


    public void setRepairs(
            List<Appointment> newRepairs
    ) {

        repairs.clear();


        if (newRepairs != null) {

            repairs.addAll(
                    newRepairs
            );
        }


        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public RepairViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view =
                LayoutInflater
                        .from(
                                parent.getContext()
                        )
                        .inflate(
                                R.layout.item_assigned_repair,
                                parent,
                                false
                        );


        return new RepairViewHolder(
                view
        );
    }


    @Override
    public void onBindViewHolder(
            @NonNull RepairViewHolder holder,
            int position
    ) {

        Appointment repair =
                repairs.get(
                        position
                );


        holder.tvRepairId.setText(
                safeText(
                        repair.getAppointment_number(),
                        "Repair"
                )
        );


        holder.tvDevice.setText(
                getDeviceName(
                        repair
                )
        );


        holder.tvStatus.setText(
                formatStatus(
                        repair.getStatus()
                )
        );


        holder.tvService.setText(
                safeText(
                        repair.getService_name(),
                        "Repair Service"
                )
        );


        holder.tvCustomer.setText(
                getCustomerName(
                        repair
                )
        );


        String branch =
                safeText(
                        repair.getBranch_name(),
                        "TECHFIX Branch"
                );


        if (repair.getBranch_city() != null
                && !repair.getBranch_city()
                .trim()
                .isEmpty()) {

            branch +=
                    " - "
                            + repair
                            .getBranch_city()
                            .trim();
        }


        holder.tvBranch.setText(
                branch
        );


        String date =
                safeText(
                        repair.getRequested_date(),
                        "Date unavailable"
                );


        if (repair.getRequested_time() != null
                && !repair.getRequested_time()
                .trim()
                .isEmpty()) {

            date +=
                    " • "
                            + repair
                            .getRequested_time()
                            .trim();
        }


        holder.tvDate.setText(
                date
        );


        holder.tvProblem.setText(
                safeText(
                        repair.getProblem_description(),
                        "No problem description provided"
                )
        );


        holder.itemView
                .setOnClickListener(
                        view -> {

                            int clickedPosition =
                                    holder
                                            .getBindingAdapterPosition();


                            if (clickedPosition
                                    == RecyclerView.NO_POSITION) {

                                return;
                            }


                            if (listener != null) {

                                listener.onRepairClick(
                                        repairs.get(
                                                clickedPosition
                                        )
                                );
                            }
                        }
                );
    }


    @Override
    public int getItemCount() {

        return repairs.size();
    }


    private String getDeviceName(
            Appointment appointment
    ) {

        if (appointment.getDevice_name() != null
                && !appointment.getDevice_name()
                .trim()
                .isEmpty()) {

            return appointment
                    .getDevice_name()
                    .trim();
        }


        String brand =
                appointment.getDevice_brand();

        String model =
                appointment.getDevice_model();


        String value =
                (
                        brand == null
                                ? ""
                                : brand.trim()
                )
                        + " "
                        + (
                        model == null
                                ? ""
                                : model.trim()
                );


        value =
                value.trim();


        if (value.isEmpty()) {

            return "Device information unavailable";
        }


        return value;
    }


    private String getCustomerName(
            Appointment appointment
    ) {

        if (appointment.getCustomer_name() != null
                && !appointment.getCustomer_name()
                .trim()
                .isEmpty()) {

            return appointment
                    .getCustomer_name()
                    .trim();
        }


        String first =
                appointment
                        .getCustomer_first_name();

        String last =
                appointment
                        .getCustomer_last_name();


        String value =
                (
                        first == null
                                ? ""
                                : first.trim()
                )
                        + " "
                        + (
                        last == null
                                ? ""
                                : last.trim()
                );


        value =
                value.trim();


        if (value.isEmpty()) {

            return "Customer";
        }


        return value;
    }


    private String formatStatus(
            String status
    ) {

        if (status == null
                || status.trim()
                .isEmpty()) {

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
                || value.trim()
                .isEmpty()) {

            return fallback;
        }


        return value.trim();
    }


    static class RepairViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvRepairId;
        TextView tvDevice;
        TextView tvStatus;
        TextView tvService;
        TextView tvCustomer;
        TextView tvBranch;
        TextView tvDate;
        TextView tvProblem;


        RepairViewHolder(
                @NonNull View itemView
        ) {

            super(itemView);


            tvRepairId =
                    itemView.findViewById(
                            R.id.tv_assigned_repair_id
                    );


            tvDevice =
                    itemView.findViewById(
                            R.id.tv_assigned_device
                    );


            tvStatus =
                    itemView.findViewById(
                            R.id.tv_assigned_status
                    );


            tvService =
                    itemView.findViewById(
                            R.id.tv_assigned_service
                    );


            tvCustomer =
                    itemView.findViewById(
                            R.id.tv_assigned_customer
                    );


            tvBranch =
                    itemView.findViewById(
                            R.id.tv_assigned_branch
                    );


            tvDate =
                    itemView.findViewById(
                            R.id.tv_assigned_date
                    );


            tvProblem =
                    itemView.findViewById(
                            R.id.tv_assigned_problem
                    );
        }
    }
}