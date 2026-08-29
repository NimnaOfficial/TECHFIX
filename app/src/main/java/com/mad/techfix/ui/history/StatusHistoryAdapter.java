package com.mad.techfix.ui.history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mad.techfix.R;
import com.mad.techfix.models.AppointmentDetail;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StatusHistoryAdapter extends RecyclerView.Adapter<StatusHistoryAdapter.ViewHolder> {

    private List<AppointmentDetail.StatusHistory> historyList = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_status_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppointmentDetail.StatusHistory item = historyList.get(position);

        holder.tvStatus.setText(item.getStatus() != null ? item.getStatus() : "UNKNOWN");
        holder.tvNote.setText(item.getNote() != null ? item.getNote() : "");
        holder.tvChangedBy.setText("by " + item.getChanged_by_full_name() + " (" + item.getChanged_by_role() + ")");

        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = inputFormat.parse(item.getCreated_at());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
            holder.tvTimestamp.setText(outputFormat.format(date));
        } catch (Exception e) {
            holder.tvTimestamp.setText(item.getCreated_at());
        }

        if (position == historyList.size() - 1) {
            holder.timelineLine.setVisibility(View.INVISIBLE);
        } else {
            holder.timelineLine.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public void updateList(List<AppointmentDetail.StatusHistory> newList) {
        this.historyList = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStatus, tvNote, tvChangedBy, tvTimestamp;
        View timelineDot, timelineLine;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvNote = itemView.findViewById(R.id.tv_note);
            tvChangedBy = itemView.findViewById(R.id.tv_changed_by);
            tvTimestamp = itemView.findViewById(R.id.tv_timestamp);
            timelineDot = itemView.findViewById(R.id.timeline_dot);
            timelineLine = itemView.findViewById(R.id.timeline_line);
        }
    }
}