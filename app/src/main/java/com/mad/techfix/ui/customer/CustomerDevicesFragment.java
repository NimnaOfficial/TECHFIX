package com.mad.techfix.ui.customer;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.mad.techfix.R;
import com.mad.techfix.data.SessionManager;
import com.mad.techfix.models.ApiResponse;
import com.mad.techfix.models.Device;
import com.mad.techfix.network.RetrofitClient;
import com.mad.techfix.ui.customer.adapters.DeviceAdapter;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerDevicesFragment extends Fragment {

    private RecyclerView rvDevices;
    private FloatingActionButton fabAddDevice;
    private DeviceAdapter deviceAdapter;
    private SessionManager sessionManager;
    private List<Device> deviceList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_devices, container, false);

        sessionManager = new SessionManager(requireContext());
        rvDevices = view.findViewById(R.id.rvDevices);
        fabAddDevice = view.findViewById(R.id.fabAddDevice);

        rvDevices.setLayoutManager(new LinearLayoutManager(getContext()));
        deviceAdapter = new DeviceAdapter(deviceList, new DeviceAdapter.OnDeviceClickListener() {
            @Override
            public void onEditClick(Device device) {
                showDeviceDialog(device);
            }

            @Override
            public void onDeleteClick(Device device) {
                deleteDevice(device.getId());
            }
        });
        rvDevices.setAdapter(deviceAdapter);

        fabAddDevice.setOnClickListener(v -> showDeviceDialog(null));

        loadDevices();

        return view;
    }

    private void loadDevices() {
        String token = sessionManager.getBearerToken();
        RetrofitClient.getApiService().getCustomerDevices(token).enqueue(new Callback<ApiResponse<List<Device>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<Device>>> call, @NonNull Response<ApiResponse<List<Device>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    deviceList = response.body().getData();
                    deviceAdapter.updateData(deviceList);
                } else {
                    Toast.makeText(getContext(), "Failed to load devices", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<Device>>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDeviceDialog(@Nullable Device existingDevice) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View view = getLayoutInflater().inflate(R.layout.dialog_device_form, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);

        TextView tvTitle = view.findViewById(R.id.tvDialogTitle);
        AutoCompleteTextView actCategory = view.findViewById(R.id.actCategory);
        TextInputEditText etBrand = view.findViewById(R.id.etBrand);
        TextInputEditText etModel = view.findViewById(R.id.etModel);
        TextInputEditText etSerialNumber = view.findViewById(R.id.etSerialNumber);
        MaterialButton btnSave = view.findViewById(R.id.btnSave);
        MaterialButton btnCancel = view.findViewById(R.id.btnCancel);

        // Dummy categories for now, ideally fetched from API
        String[] categories = {"Laptop", "Smartphone", "Tablet", "Desktop", "Smartwatch"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, categories);
        actCategory.setAdapter(adapter);

        if (existingDevice != null) {
            tvTitle.setText("Edit Device");
            actCategory.setText("Laptop", false); // Default for demo, normally match existing
            etBrand.setText(existingDevice.getBrand());
            etModel.setText(existingDevice.getModel());
            etSerialNumber.setText(existingDevice.getSerialNumber());
        } else {
            tvTitle.setText("Add New Device");
            actCategory.setText("Smartphone", false);
        }

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String brand = etBrand.getText() != null ? etBrand.getText().toString().trim() : "";
            String model = etModel.getText() != null ? etModel.getText().toString().trim() : "";
            String serial = etSerialNumber.getText() != null ? etSerialNumber.getText().toString().trim() : "";

            if (TextUtils.isEmpty(brand) || TextUtils.isEmpty(model)) {
                Toast.makeText(getContext(), "Brand and Model are required", Toast.LENGTH_SHORT).show();
                return;
            }

            Device device = new Device();
            device.setBrand(brand);
            device.setModel(model);
            device.setSerialNumber(serial);
            device.setCategoryId("1"); // Hardcoded for demo

            if (existingDevice == null) {
                saveNewDevice(device, dialog);
            } else {
                updateExistingDevice(existingDevice.getId(), device, dialog);
            }
        });

        dialog.show();
    }

    private void saveNewDevice(Device device, AlertDialog dialog) {
        String token = sessionManager.getBearerToken();
        RetrofitClient.getApiService().addCustomerDevice(token, device).enqueue(new Callback<ApiResponse<Device>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Device>> call, @NonNull Response<ApiResponse<Device>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(getContext(), "Device added successfully", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    loadDevices();
                } else {
                    Toast.makeText(getContext(), "Failed to add device", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Device>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateExistingDevice(String deviceId, Device device, AlertDialog dialog) {
        String token = sessionManager.getBearerToken();
        RetrofitClient.getApiService().updateCustomerDevice(token, deviceId, device).enqueue(new Callback<ApiResponse<Device>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Device>> call, @NonNull Response<ApiResponse<Device>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(getContext(), "Device updated successfully", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    loadDevices();
                } else {
                    Toast.makeText(getContext(), "Failed to update device", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Device>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteDevice(String deviceId) {
        String token = sessionManager.getBearerToken();
        RetrofitClient.getApiService().deleteCustomerDevice(token, deviceId).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Object>> call, @NonNull Response<ApiResponse<Object>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(getContext(), "Device deleted", Toast.LENGTH_SHORT).show();
                    loadDevices(); // Refresh list
                } else {
                    Toast.makeText(getContext(), "Failed to delete device", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Object>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
