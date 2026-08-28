package com.mad.techfix.ui.admin.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.mad.techfix.R;
import com.mad.techfix.models.admin.Technician;
import java.util.ArrayList;
import java.util.List;

public class AssignableTechnicianAdapter extends RecyclerView.Adapter<AssignableTechnicianAdapter.ViewHolder> {

    private List<Technician> technicianList = new ArrayList<>();
    private int selectedPosition = -1;

    public void updateData(List<Technician> newTechnicians) {
        this.technicianList = newTechnicians;
        this.selectedPosition = -1;
        notifyDataSetChanged();
    }

    public Technician getSelectedTechnician() {
        if (selectedPosition != -1 && selectedPosition < technicianList.size()) {
            return technicianList.get(selectedPosition);
        }
        return null;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_assignable_technician, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Technician technician = technicianList.get(position);
        holder.bind(technician, position, selectedPosition);
        
        holder.itemView.setOnClickListener(v -> {
            int previousPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previousPosition);
            notifyItemChanged(selectedPosition);
        });
        
        holder.radioSelect.setOnClickListener(v -> {
            int previousPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previousPosition);
            notifyItemChanged(selectedPosition);
        });
    }

    @Override
    public int getItemCount() {
        return technicianList == null ? 0 : technicianList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final RadioButton radioSelect;
        private final TextView tvName;
        private final TextView tvCode;
        private final TextView tvSpecialization;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            radioSelect = itemView.findViewById(R.id.radio_select);
            tvName = itemView.findViewById(R.id.tv_assignable_name);
            tvCode = itemView.findViewById(R.id.tv_assignable_code);
            tvSpecialization = itemView.findViewById(R.id.tv_assignable_specialization);
        }

        public void bind(Technician technician, int position, int selectedPosition) {
            tvName.setText(technician.getFullName());
            tvCode.setText(technician.getEmployeeCode());
            tvSpecialization.setText(technician.getSpecialization());
            radioSelect.setChecked(position == selectedPosition);
        }
    }
}

