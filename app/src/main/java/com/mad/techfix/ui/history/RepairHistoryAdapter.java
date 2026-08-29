package com.mad.techfix.ui.history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mad.techfix.R;
import com.mad.techfix.data.local.database.RepairHistoryEntity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RepairHistoryAdapter extends RecyclerView.Adapter<RepairHistoryAdapter.ViewHolder> {

    private List<RepairHistoryEntity> historyList = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(RepairHistoryEntity item);
    }

    public RepairHistoryAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_repair_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RepairHistoryEntity item = historyList.get(position);

        String displayNumber = item.getAppointmentNumber() != null && !item.getAppointmentNumber().isEmpty()
                ? item.getAppointmentNumber()
                : item.getRepairId();
        holder.tvAppointmentNumber.setText(displayNumber);

        holder.tvDevice.setText(item.getDevice() != null ? item.getDevice() : "Unknown Device");
        holder.tvService.setText(item.getService() != null ? item.getService() : "Unknown Service");
        holder.tvPrice.setText(String.format("$%.2f", item.getPrice()));

        // Format date
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = inputFormat.parse(item.getDate());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
            holder.tvDate.setText(outputFormat.format(date));
        } catch (Exception e) {
            holder.tvDate.setText(item.getDate());
        }

        String status = item.getStatus() != null ? item.getStatus() : "REQUESTED";
        holder.tvStatus.setText(status);

        // Update progress steps (8 steps)
        updateProgressSteps(holder, status);

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });
    }

    private void updateProgressSteps(ViewHolder holder, String status) {
        int step = getStepForStatus(status);
        View[] steps = {holder.step1, holder.step2, holder.step3, holder.step4,
                holder.step5, holder.step6, holder.step7, holder.step8};

        for (int i = 0; i < steps.length; i++) {
            if (i < step) {
                steps[i].setBackgroundColor(0xFF00D4FF); // Accent color
            } else {
                steps[i].setBackgroundColor(0xFF333333); // Dark gray
            }
        }
    }

    private int getStepForStatus(String status) {
        switch (status.toUpperCase()) {
            case "REQUESTED": return 1;
            case "CONFIRMED": return 2;
            case "RECEIVED": return 3;
            case "DIAGNOSING": return 4;
            case "REPAIRING": return 5;
            case "TESTING": return 6;
            case "READY": return 7;
            case "COMPLETED": return 8;
            default: return 1;
        }
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public void updateList(List<RepairHistoryEntity> newList) {
        this.historyList = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvAppointmentNumber, tvStatus, tvDevice, tvService, tvPrice, tvDate;
        View step1, step2, step3, step4, step5, step6, step7, step8;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAppointmentNumber = itemView.findViewById(R.id.tv_appointment_number);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvDevice = itemView.findViewById(R.id.tv_device);
            tvService = itemView.findViewById(R.id.tv_service);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvDate = itemView.findViewById(R.id.tv_date);
            step1 = itemView.findViewById(R.id.step1);
            step2 = itemView.findViewById(R.id.step2);
            step3 = itemView.findViewById(R.id.step3);
            step4 = itemView.findViewById(R.id.step4);
            step5 = itemView.findViewById(R.id.step5);
            step6 = itemView.findViewById(R.id.step6);
            step7 = itemView.findViewById(R.id.step7);
            step8 = itemView.findViewById(R.id.step8);
        }
    }
}