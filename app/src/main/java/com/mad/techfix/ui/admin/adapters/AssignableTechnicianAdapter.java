package com.mad.techfix.ui.admin.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mad.techfix.R;
import com.mad.techfix.Technician;

import java.util.List;

public class AssignableTechnicianAdapter extends RecyclerView.Adapter<AssignableTechnicianAdapter.AssignableTechnicianViewHolder> {
    private List<Technician> technicianList;
    private int selectedPosition = -1;

    public AssignableTechnicianAdapter(List<Technician> technicianList) {
        this.technicianList = technicianList;
    }

    public void updateData(List<Technician> newList) {
        this.technicianList = newList;
        this.selectedPosition = -1;
        notifyDataSetChanged();
    }

    public Technician getSelectedTechnician() {
        if (selectedPosition != -1 && technicianList != null && selectedPosition < technicianList.size()) {
            return technicianList.get(selectedPosition);
        }
        return null;
    }

    @NonNull
    @Override
    public AssignableTechnicianViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_assignable_technician, parent, false);
        return new AssignableTechnicianViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AssignableTechnicianViewHolder holder, int position) {
        holder.bind(technicianList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return technicianList != null ? technicianList.size() : 0;
    }

    class AssignableTechnicianViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmployeeCode, tvSpecialization;
        RadioButton rbSelect;

        public AssignableTechnicianViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvEmployeeCode = itemView.findViewById(R.id.tvEmployeeCode);
            tvSpecialization = itemView.findViewById(R.id.tvSpecialization);
            rbSelect = itemView.findViewById(R.id.rbSelect);
        }

        public void bind(Technician technician, int position) {
            if (tvName != null) tvName.setText(technician.getFullName());
            if (tvEmployeeCode != null) tvEmployeeCode.setText(technician.getEmployeeCode());
            if (tvSpecialization != null) tvSpecialization.setText(technician.getSpecialization());
            
            if (rbSelect != null) {
                rbSelect.setChecked(position == selectedPosition);
            }

            itemView.setOnClickListener(v -> handleSelection(position));
            if (rbSelect != null) {
                rbSelect.setOnClickListener(v -> handleSelection(position));
            }
        }

        private void handleSelection(int position) {
            int previousPosition = selectedPosition;
            selectedPosition = position;
            notifyItemChanged(previousPosition);
            notifyItemChanged(selectedPosition);
        }
    }
}
