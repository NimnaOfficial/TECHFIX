package com.mad.techfix.ui.admin.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.mad.techfix.R;
import com.mad.techfix.models.admin.Manager;
import java.util.ArrayList;
import java.util.List;

public class ManagerAdapter extends RecyclerView.Adapter<ManagerAdapter.ManagerViewHolder> {

    public interface OnManagerInteractionListener {
        void onEditClick(Manager manager);
        void onUserClick(Manager manager);
        void onDeleteClick(Manager manager);
    }

    private List<Manager> managers = new ArrayList<>();
    private final OnManagerInteractionListener listener;

    public ManagerAdapter(OnManagerInteractionListener listener) {
        this.listener = listener;
    }

    public void setManagers(List<Manager> managers) {
        this.managers = managers != null ? managers : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ManagerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_manager, parent, false);
        return new ManagerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ManagerViewHolder holder, int position) {
        Manager manager = managers.get(position);
        holder.tvName.setText(manager.getFirstName() + " " + manager.getLastName());
        holder.tvEmail.setText(manager.getEmail());

        if (manager.getIsActive() == 1) {
            holder.tvStatus.setText("Active");
            holder.tvStatus.setTextColor(Color.parseColor("#4CAF50")); // Green
        } else {
            holder.tvStatus.setText("Inactive");
            holder.tvStatus.setTextColor(Color.parseColor("#F44336")); // Red
        }
        
        String role = manager.getRole() != null ? manager.getRole() : "USER";
        holder.tvRole.setText(role);
        if ("ADMIN".equalsIgnoreCase(role)) holder.tvRole.setBackgroundColor(Color.parseColor("#9C27B0")); // Purple
        else if ("MANAGER".equalsIgnoreCase(role)) holder.tvRole.setBackgroundColor(Color.parseColor("#2196F3")); // Blue
        else if ("TECHNICIAN".equalsIgnoreCase(role)) holder.tvRole.setBackgroundColor(Color.parseColor("#FF9800")); // Orange
        else holder.tvRole.setBackgroundColor(Color.parseColor("#607D8B")); // Grey-Blue for Customer

        holder.itemView.setOnClickListener(v -> listener.onUserClick(manager));

        holder.btnOptions.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(v.getContext(), holder.btnOptions);
            popup.inflate(R.menu.menu_manager_options);
            popup.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.action_edit) {
                    listener.onEditClick(manager);
                    return true;
                } else if (itemId == R.id.action_delete) {
                    listener.onDeleteClick(manager);
                    return true;
                }
                return false;
            });
            popup.show();
        });
    }

    @Override
    public int getItemCount() {
        return managers.size();
    }

    static class ManagerViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail, tvStatus, tvRole;
        ImageButton btnOptions;

        public ManagerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_manager_name);
            tvEmail = itemView.findViewById(R.id.tv_manager_email);
            tvStatus = itemView.findViewById(R.id.tv_manager_status);
            tvRole = itemView.findViewById(R.id.tv_user_role);
            btnOptions = itemView.findViewById(R.id.btn_manager_options);
        }
    }
}

