package com.mad.techfix.ui.admin.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.mad.techfix.R;
import com.mad.techfix.models.admin.Service;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ServiceCheckboxAdapter extends RecyclerView.Adapter<ServiceCheckboxAdapter.ViewHolder> {

    private List<Service> serviceList = new ArrayList<>();
    private final Set<String> selectedServiceIds = new HashSet<>();

    public void updateData(List<Service> services, Set<String> preSelectedIds) {
        this.serviceList = services;
        this.selectedServiceIds.clear();
        if (preSelectedIds != null) {
            this.selectedServiceIds.addAll(preSelectedIds);
        }
        notifyDataSetChanged();
    }

    public List<String> getSelectedServiceIds() {
        return new ArrayList<>(selectedServiceIds);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service_checkbox, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Service service = serviceList.get(position);
        holder.bind(service, selectedServiceIds.contains(service.getId()));

        holder.checkboxService.setOnCheckedChangeListener(null); // Clear previous listener
        holder.checkboxService.setChecked(selectedServiceIds.contains(service.getId()));

        holder.checkboxService.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedServiceIds.add(service.getId());
            } else {
                selectedServiceIds.remove(service.getId());
            }
        });

        holder.itemView.setOnClickListener(v -> holder.checkboxService.setChecked(!holder.checkboxService.isChecked()));
    }

    @Override
    public int getItemCount() {
        return serviceList == null ? 0 : serviceList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final CheckBox checkboxService;
        private final TextView tvServiceName;
        private final TextView tvServiceDesc;
        private final TextView tvServicePrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkboxService = itemView.findViewById(R.id.checkbox_service);
            tvServiceName = itemView.findViewById(R.id.tv_service_name);
            tvServiceDesc = itemView.findViewById(R.id.tv_service_desc);
            tvServicePrice = itemView.findViewById(R.id.tv_service_price);
        }

        public void bind(Service service, boolean isChecked) {
            tvServiceName.setText(service.getName());
            tvServiceDesc.setText(service.getDescription());
            tvServicePrice.setText(String.format("LKR %.2f", service.getBasePrice()));
        }
    }
}
