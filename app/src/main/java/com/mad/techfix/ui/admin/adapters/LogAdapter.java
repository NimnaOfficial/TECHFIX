package com.mad.techfix.ui.admin.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.mad.techfix.R;
import com.mad.techfix.models.admin.LogEntry;
import java.util.ArrayList;
import java.util.List;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.LogViewHolder> {
    private List<LogEntry> logs = new ArrayList<>();

    public void setLogs(List<LogEntry> logs) {
        this.logs = logs != null ? logs : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_log, parent, false);
        return new LogViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
        LogEntry log = logs.get(position);
        holder.tvPath.setText(log.getPath());
        holder.tvMessage.setText(log.getMessage());
        holder.tvTimestamp.setText(log.getCreatedAt());

        // Level Styling
        String level = log.getLevel() != null ? log.getLevel() : "INFO";
        holder.tvLevel.setText("[" + level + "]");
        if ("WARN".equalsIgnoreCase(level)) {
            holder.tvLevel.setTextColor(Color.parseColor("#FFC107"));
        } else if ("ERROR".equalsIgnoreCase(level) || "CRITICAL".equalsIgnoreCase(level)) {
            holder.tvLevel.setTextColor(Color.parseColor("#F44336"));
        } else {
            holder.tvLevel.setTextColor(Color.parseColor("#4CAF50")); // Green
        }

        // Method Styling
        String method = log.getMethod() != null ? log.getMethod() : "UNK";
        holder.tvMethod.setText(method);
        if ("GET".equalsIgnoreCase(method)) holder.tvMethod.setTextColor(Color.parseColor("#03A9F4"));
        else if ("POST".equalsIgnoreCase(method)) holder.tvMethod.setTextColor(Color.parseColor("#4CAF50"));
        else if ("PUT".equalsIgnoreCase(method)) holder.tvMethod.setTextColor(Color.parseColor("#FF9800"));
        else if ("DELETE".equalsIgnoreCase(method)) holder.tvMethod.setTextColor(Color.parseColor("#F44336"));
        else holder.tvMethod.setTextColor(Color.parseColor("#9E9E9E"));
    }

    @Override
    public int getItemCount() {
        return logs.size();
    }

    static class LogViewHolder extends RecyclerView.ViewHolder {
        TextView tvLevel, tvTimestamp, tvMethod, tvPath, tvMessage;
        public LogViewHolder(@NonNull View v) {
            super(v);
            tvLevel = v.findViewById(R.id.tv_log_level);
            tvTimestamp = v.findViewById(R.id.tv_log_timestamp);
            tvMethod = v.findViewById(R.id.tv_log_method);
            tvPath = v.findViewById(R.id.tv_log_path);
            tvMessage = v.findViewById(R.id.tv_log_message);
        }
    }
}
