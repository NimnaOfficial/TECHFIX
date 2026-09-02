package com.mad.techfix.ui.booking;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.radiobutton.MaterialRadioButton;
import com.mad.techfix.R;
import com.mad.techfix.models.Device;

import java.util.ArrayList;
import java.util.List;

public class BookingDeviceAdapter
        extends RecyclerView.Adapter<BookingDeviceAdapter.DeviceViewHolder> {

    public interface OnDeviceSelectedListener {
        void onDeviceSelected(Device device);
    }

    private final List<Device> devices = new ArrayList<>();
    private final OnDeviceSelectedListener listener;

    private int selectedPosition = RecyclerView.NO_POSITION;

    public BookingDeviceAdapter(OnDeviceSelectedListener listener) {
        this.listener = listener;
    }

    public void setDevices(List<Device> newDevices) {
        devices.clear();

        if (newDevices != null) {
            devices.addAll(newDevices);
        }

        selectedPosition = RecyclerView.NO_POSITION;
        notifyDataSetChanged();
    }

    public Device getSelectedDevice() {
        if (selectedPosition == RecyclerView.NO_POSITION) {
            return null;
        }

        return devices.get(selectedPosition);
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_booking_device, parent, false);

        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull DeviceViewHolder holder,
            int position
    ) {
        Device device = devices.get(position);

        String displayName = device.getDisplayName();

        if (displayName == null || displayName.trim().isEmpty()) {
            displayName = "Unknown Device";
        }

        holder.tvDeviceName.setText(displayName);

        String model = device.getModel();

        if (model == null || model.trim().isEmpty()) {
            holder.tvDeviceModel.setText("Model: N/A");
        } else {
            holder.tvDeviceModel.setText("Model: " + model);
        }

        String category = device.getCategoryName();

        if (category == null || category.trim().isEmpty()) {
            category = "Device";
        }

        holder.tvDeviceType.setText(category);

        holder.radioSelectDevice.setChecked(
                selectedPosition == holder.getBindingAdapterPosition()
        );

        holder.itemView.setOnClickListener(v -> {

            int clickedPosition = holder.getBindingAdapterPosition();

            if (clickedPosition == RecyclerView.NO_POSITION) {
                return;
            }

            int previousPosition = selectedPosition;
            selectedPosition = clickedPosition;

            if (previousPosition != RecyclerView.NO_POSITION) {
                notifyItemChanged(previousPosition);
            }

            notifyItemChanged(selectedPosition);

            if (listener != null) {
                listener.onDeviceSelected(devices.get(selectedPosition));
            }
        });
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    static class DeviceViewHolder extends RecyclerView.ViewHolder {

        TextView tvDeviceName;
        TextView tvDeviceModel;
        TextView tvDeviceType;
        MaterialRadioButton radioSelectDevice;

        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);

            tvDeviceName =
                    itemView.findViewById(R.id.tv_device_name);

            tvDeviceModel =
                    itemView.findViewById(R.id.tv_device_model);

            tvDeviceType =
                    itemView.findViewById(R.id.tv_device_type);

            radioSelectDevice =
                    itemView.findViewById(R.id.radio_select_device);
        }
    }
}