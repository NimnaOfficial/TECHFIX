package com.mad.techfix.ui.customer.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.mad.techfix.R;
import com.mad.techfix.models.Device;
import java.util.List;
import com.google.android.material.button.MaterialButton;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {

    private List<Device> devices;
    private OnDeviceClickListener listener;

    public interface OnDeviceClickListener {
        void onEditClick(Device device);
        void onDeleteClick(Device device);
    }

    public DeviceAdapter(List<Device> devices, OnDeviceClickListener listener) {
        this.devices = devices;
        this.listener = listener;
    }

    public void updateData(List<Device> newDevices) {
        this.devices = newDevices;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device, parent, false);
        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        Device device = devices.get(position);
        holder.tvDeviceBrand.setText(device.getBrand());
        holder.tvDeviceModel.setText(device.getModel());
        holder.tvSerialNumber.setText(device.getSerialNumber() != null ? "SN: " + device.getSerialNumber() : "SN: N/A");

        holder.btnEdit.setOnClickListener(v -> listener.onEditClick(device));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(device));
    }

    @Override
    public int getItemCount() {
        return devices == null ? 0 : devices.size();
    }

    static class DeviceViewHolder extends RecyclerView.ViewHolder {
        TextView tvDeviceBrand, tvDeviceModel, tvSerialNumber;
        MaterialButton btnEdit, btnDelete;

        DeviceViewHolder(View itemView) {
            super(itemView);
            tvDeviceBrand = itemView.findViewById(R.id.tvDeviceBrand);
            tvDeviceModel = itemView.findViewById(R.id.tvDeviceModel);
            tvSerialNumber = itemView.findViewById(R.id.tvSerialNumber);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
