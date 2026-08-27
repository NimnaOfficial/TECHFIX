package com.mad.techfix.ui.admin.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mad.techfix.R;
import com.mad.techfix.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ServiceCheckboxAdapter extends RecyclerView.Adapter<ServiceCheckboxAdapter.ServiceCheckboxViewHolder> {
    private List<Service> serviceList;
    private Set<String> assignedServiceIds;

    public ServiceCheckboxAdapter(List<Service> serviceList, Set<String> assignedServiceIds) {
        this.serviceList = serviceList;
        this.assignedServiceIds = assignedServiceIds != null ? new HashSet<>(assignedServiceIds) : new HashSet<>();
    }

    public void updateData(List<Service> services, Set<String> assignedIds) {
        this.serviceList = services;
        this.assignedServiceIds = assignedIds != null ? new HashSet<>(assignedIds) : new HashSet<>();
        notifyDataSetChanged();
    }

    public List<String> getSelectedServiceIds() {
        return new ArrayList<>(assignedServiceIds);
    }

    @NonNull
    @Override
    public ServiceCheckboxViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service_checkbox, parent, false);
        return new ServiceCheckboxViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceCheckboxViewHolder holder, int position) {
        holder.bind(serviceList.get(position));
    }

    @Override
    public int getItemCount() {
        return serviceList != null ? serviceList.size() : 0;
    }

    class ServiceCheckboxViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbService;
        TextView tvServiceName;

        public ServiceCheckboxViewHolder(@NonNull View itemView) {
            super(itemView);
            cbService = itemView.findViewById(R.id.cbService);
            tvServiceName = itemView.findViewById(R.id.tvServiceName);
        }

        public void bind(Service service) {
            String serviceId = String.valueOf(service.getId());
            
            if (tvServiceName != null) {
                tvServiceName.setText(service.getName());
            }

            if (cbService != null) {
                // Remove previous listener to prevent recycling issues
                cbService.setOnCheckedChangeListener(null);
                
                cbService.setChecked(assignedServiceIds.contains(serviceId));
                
                cbService.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        assignedServiceIds.add(serviceId);
                    } else {
                        assignedServiceIds.remove(serviceId);
                    }
                });
            }
            
            itemView.setOnClickListener(v -> {
                if (cbService != null) {
                    cbService.setChecked(!cbService.isChecked());
                }
            });
        }
    }
}
