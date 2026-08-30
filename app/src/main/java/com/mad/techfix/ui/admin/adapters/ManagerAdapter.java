package com.mad.techfix.ui.admin.adapters;

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

    private List<Manager> managers = new ArrayList<>();
    private final OnManagerInteractionListener listener;

    public interface OnManagerInteractionListener {
        void onEditClick(Manager manager);
        void onDeleteClick(Manager manager);
    }

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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_manager, parent, false);
        return new ManagerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ManagerViewHolder holder, int position) {
        Manager manager = managers.get(position);
        holder.tvName.setText(manager.getFirstName() + " " + manager.getLastName());
        holder.tvEmail.setText(manager.getEmail());
        
        if (manager.getIsActive() == 1) {
            holder.tvStatus.setText("Active");
            holder.tvStatus.setTextColor(android.graphics.Color.parseColor("#4CAF50"));
        } else {
            holder.tvStatus.setText("Inactive");
            holder.tvStatus.setTextColor(android.graphics.Color.parseColor("#F44336"));
        }

        holder.btnOptions.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(v.getContext(), holder.btnOptions);
            popup.getMenu().add(0, 1, 0, "Edit");
            popup.getMenu().add(0, 2, 1, "Delete");
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == 1) {
                    listener.onEditClick(manager);
                    return true;
                } else if (item.getItemId() == 2) {
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
        TextView tvName, tvEmail, tvStatus;
        ImageButton btnOptions;
        
        public ManagerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_manager_name);
            tvEmail = itemView.findViewById(R.id.tv_manager_email);
            tvStatus = itemView.findViewById(R.id.tv_manager_status);
            btnOptions = itemView.findViewById(R.id.btn_manager_options);
        }
    }
}
