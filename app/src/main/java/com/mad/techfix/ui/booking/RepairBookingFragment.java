package com.mad.techfix.ui.booking;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.mad.techfix.R;
import com.mad.techfix.data.SessionManager;
import com.mad.techfix.models.ApiResponse;
import com.mad.techfix.models.Device;
import com.mad.techfix.models.admin.Branch;
import com.mad.techfix.models.admin.Service;
import com.mad.techfix.network.ApiService;
import com.mad.techfix.network.RetrofitClient;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RepairBookingFragment extends Fragment {

    private RecyclerView recyclerDevices;
    private RecyclerView recyclerServices;
    private RecyclerView recyclerBranches;

    private TextView tvSelectedDevice;
    private TextView tvSelectedService;
    private TextView tvSelectedBranch;

    private MaterialButton btnAddDevice;
    private MaterialButton btnContinue;

    private BookingDeviceAdapter deviceAdapter;
    private BookingServiceAdapter serviceAdapter;
    private BookingBranchAdapter branchAdapter;

    private Device selectedDevice;
    private Service selectedService;
    private Branch selectedBranch;

    private String selectedDate;
    private String selectedTime;

    private ApiService apiService;
    private SessionManager sessionManager;

    public RepairBookingFragment() {
        // Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(
                R.layout.fragment_repair_booking,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        apiService = RetrofitClient.getApiService();
        sessionManager = new SessionManager(requireContext());

        bindViews(view);
        setupRecyclerViews();
        setupListeners();
        loadBookingData();
    }

    private void bindViews(View view) {

        recyclerDevices =
                view.findViewById(R.id.recycler_booking_devices);

        recyclerServices =
                view.findViewById(R.id.recycler_booking_services);

        recyclerBranches =
                view.findViewById(R.id.recycler_booking_branches);

        tvSelectedDevice =
                view.findViewById(R.id.tv_selected_device);

        tvSelectedService =
                view.findViewById(R.id.tv_selected_service);

        tvSelectedBranch =
                view.findViewById(R.id.tv_selected_branch);

        btnAddDevice =
                view.findViewById(R.id.btn_add_device);

        btnContinue =
                view.findViewById(R.id.btn_continue_booking);
    }

    private void setupRecyclerViews() {

        deviceAdapter = new BookingDeviceAdapter(device -> {
            selectedDevice = device;

            String name = device.getDisplayName();

            if (name == null || name.trim().isEmpty()) {
                name = "Selected device";
            }

            tvSelectedDevice.setText(
                    "Device: " + name
            );
        });

        serviceAdapter = new BookingServiceAdapter(service -> {
            selectedService = service;

            String name = service.getName();

            if (name == null || name.trim().isEmpty()) {
                name = "Selected service";
            }

            tvSelectedService.setText(
                    "Service: " + name
            );
        });

        branchAdapter = new BookingBranchAdapter(branch -> {
            selectedBranch = branch;

            String name = branch.getName();

            if (name == null || name.trim().isEmpty()) {
                name = "Selected branch";
            }

            tvSelectedBranch.setText(
                    "Branch: " + name
            );
        });

        recyclerDevices.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );

        recyclerServices.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );

        recyclerBranches.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );

        recyclerDevices.setAdapter(deviceAdapter);
        recyclerServices.setAdapter(serviceAdapter);
        recyclerBranches.setAdapter(branchAdapter);
    }

    private void setupListeners() {

        btnContinue.setOnClickListener(v -> {

            if (!validateSelections()) {
                return;
            }

            showDateTimeDialog();
        });

        btnAddDevice.setOnClickListener(v -> {

            /*
             * Add Device belongs to the customer/device module.
             * We can connect this button once that screen is available.
             */
            Toast.makeText(
                    requireContext(),
                    "Add Device screen will open here",
                    Toast.LENGTH_SHORT
            ).show();
        });
    }

    private boolean validateSelections() {

        if (selectedDevice == null) {
            Toast.makeText(
                    requireContext(),
                    "Please select a device",
                    Toast.LENGTH_SHORT
            ).show();

            return false;
        }

        if (selectedService == null) {
            Toast.makeText(
                    requireContext(),
                    "Please select a service",
                    Toast.LENGTH_SHORT
            ).show();

            return false;
        }

        if (selectedBranch == null) {
            Toast.makeText(
                    requireContext(),
                    "Please select a branch",
                    Toast.LENGTH_SHORT
            ).show();

            return false;
        }

        return true;
    }

    private void loadBookingData() {

        loadDevices();
        loadServices();
        loadBranches();
    }

    private void loadDevices() {

        String token = sessionManager.getBearerToken();

        if (token.isEmpty()) {
            Toast.makeText(
                    requireContext(),
                    "Please sign in again",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        apiService.getMyDevices(token)
                .enqueue(new Callback<ApiResponse<List<Device>>>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<ApiResponse<List<Device>>> call,
                            @NonNull Response<ApiResponse<List<Device>>> response
                    ) {

                        if (!isAdded()) {
                            return;
                        }

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isSuccess()) {

                            deviceAdapter.setDevices(
                                    response.body().getData()
                            );

                        } else {

                            Toast.makeText(
                                    requireContext(),
                                    "Unable to load devices",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<ApiResponse<List<Device>>> call,
                            @NonNull Throwable t
                    ) {

                        if (!isAdded()) {
                            return;
                        }

                        Toast.makeText(
                                requireContext(),
                                "Device loading failed: " + t.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    private void loadServices() {

        apiService.getServices()
                .enqueue(new Callback<ApiResponse<List<Service>>>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<ApiResponse<List<Service>>> call,
                            @NonNull Response<ApiResponse<List<Service>>> response
                    ) {

                        if (!isAdded()) {
                            return;
                        }

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isSuccess()) {

                            serviceAdapter.setServices(
                                    response.body().getData()
                            );

                        } else {

                            Toast.makeText(
                                    requireContext(),
                                    "Unable to load services",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<ApiResponse<List<Service>>> call,
                            @NonNull Throwable t
                    ) {

                        if (!isAdded()) {
                            return;
                        }

                        Toast.makeText(
                                requireContext(),
                                "Service loading failed: " + t.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    private void loadBranches() {

        apiService.getBranches()
                .enqueue(new Callback<ApiResponse<List<Branch>>>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<ApiResponse<List<Branch>>> call,
                            @NonNull Response<ApiResponse<List<Branch>>> response
                    ) {

                        if (!isAdded()) {
                            return;
                        }

                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().isSuccess()) {

                            branchAdapter.setBranches(
                                    response.body().getData()
                            );

                        } else {

                            Toast.makeText(
                                    requireContext(),
                                    "Unable to load branches",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<ApiResponse<List<Branch>>> call,
                            @NonNull Throwable t
                    ) {

                        if (!isAdded()) {
                            return;
                        }

                        Toast.makeText(
                                requireContext(),
                                "Branch loading failed: " + t.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    private void showDateTimeDialog() {

        View dialogView = LayoutInflater.from(requireContext())
                .inflate(
                        R.layout.dialog_booking_datetime,
                        null,
                        false
                );

        TextView tvDate =
                dialogView.findViewById(R.id.tv_selected_date);

        TextView tvTime =
                dialogView.findViewById(R.id.tv_selected_time);

        View cardDate =
                dialogView.findViewById(R.id.card_select_date);

        View cardTime =
                dialogView.findViewById(R.id.card_select_time);

        MaterialButton btnCancel =
                dialogView.findViewById(R.id.btn_cancel_datetime);

        MaterialButton btnConfirm =
                dialogView.findViewById(R.id.btn_confirm_datetime);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();

        cardDate.setOnClickListener(v ->
                showDatePicker(tvDate)
        );

        cardTime.setOnClickListener(v ->
                showTimePicker(tvTime)
        );

        btnCancel.setOnClickListener(v ->
                dialog.dismiss()
        );

        btnConfirm.setOnClickListener(v -> {

            if (selectedDate == null) {

                Toast.makeText(
                        requireContext(),
                        "Please select a date",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            if (selectedTime == null) {

                Toast.makeText(
                        requireContext(),
                        "Please select a time",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            dialog.dismiss();

            /*
             * Next step:
             * Open BookingReviewFragment and pass:
             *
             * selectedDevice
             * selectedService
             * selectedBranch
             * selectedDate
             * selectedTime
             *
             * We will connect this immediately after creating
             * BookingReviewFragment.java.
             */

            BookingReviewFragment reviewFragment =
                    BookingReviewFragment.newInstance(
                            selectedDevice.getId(),
                            selectedDevice.getDisplayName(),
                            selectedService.getId(),
                            selectedService.getName(),
                            selectedService.getBasePrice(),
                            selectedBranch.getId(),
                            selectedBranch.getName(),
                            selectedDate,
                            selectedTime
                    );

            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(getId(), reviewFragment)
                    .addToBackStack(null)
                    .commit();
        });

        dialog.show();
    }

    private void showDatePicker(TextView tvDate) {

        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog picker = new DatePickerDialog(
                requireContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {

                    selectedDate = String.format(
                            Locale.getDefault(),
                            "%04d-%02d-%02d",
                            selectedYear,
                            selectedMonth + 1,
                            selectedDay
                    );

                    tvDate.setText(
                            String.format(
                                    Locale.getDefault(),
                                    "%02d/%02d/%04d",
                                    selectedDay,
                                    selectedMonth + 1,
                                    selectedYear
                            )
                    );
                },
                year,
                month,
                day
        );

        picker.getDatePicker().setMinDate(
                System.currentTimeMillis() - 1000
        );

        picker.show();
    }

    private void showTimePicker(TextView tvTime) {

        Calendar calendar = Calendar.getInstance();

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog picker = new TimePickerDialog(
                requireContext(),
                (view, selectedHour, selectedMinute) -> {

                    selectedTime = String.format(
                            Locale.getDefault(),
                            "%02d:%02d",
                            selectedHour,
                            selectedMinute
                    );

                    Calendar displayTime = Calendar.getInstance();

                    displayTime.set(
                            Calendar.HOUR_OF_DAY,
                            selectedHour
                    );

                    displayTime.set(
                            Calendar.MINUTE,
                            selectedMinute
                    );

                    String display = String.format(
                            Locale.getDefault(),
                            "%02d:%02d %s",
                            selectedHour % 12 == 0
                                    ? 12
                                    : selectedHour % 12,
                            selectedMinute,
                            selectedHour < 12
                                    ? "AM"
                                    : "PM"
                    );

                    tvTime.setText(display);
                },
                hour,
                minute,
                false
        );

        picker.show();
    }
}