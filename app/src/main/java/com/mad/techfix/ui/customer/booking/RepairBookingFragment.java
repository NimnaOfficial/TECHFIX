package com.mad.techfix.ui.customer.booking;

import android.app.DatePickerDialog;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.mad.techfix.R;
import com.mad.techfix.models.Device;
import com.mad.techfix.models.admin.Branch;
import com.mad.techfix.models.admin.Service;
import com.mad.techfix.viewmodel.RepairBookingViewModel;

import java.util.Calendar;
import java.util.Locale;

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

    private RepairBookingViewModel viewModel;

    private Device selectedDevice;
    private Service selectedService;
    private Branch selectedBranch;

    private String selectedDate;
    private String selectedTime;


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

        super.onViewCreated(
                view,
                savedInstanceState
        );

        bindViews(view);

        setupRecyclerViews();

        setupListeners();

        setupViewModel();

        observeViewModel();

        viewModel.loadBookingData();
    }


    // ==========================================
    // VIEW BINDING
    // ==========================================

    private void bindViews(
            View view
    ) {

        recyclerDevices =
                view.findViewById(
                        R.id.recycler_booking_devices
                );

        recyclerServices =
                view.findViewById(
                        R.id.recycler_booking_services
                );

        recyclerBranches =
                view.findViewById(
                        R.id.recycler_booking_branches
                );

        tvSelectedDevice =
                view.findViewById(
                        R.id.tv_selected_device
                );

        tvSelectedService =
                view.findViewById(
                        R.id.tv_selected_service
                );

        tvSelectedBranch =
                view.findViewById(
                        R.id.tv_selected_branch
                );

        btnAddDevice =
                view.findViewById(
                        R.id.btn_add_device
                );

        btnContinue =
                view.findViewById(
                        R.id.btn_continue_booking
                );
    }


    // ==========================================
    // RECYCLER VIEWS
    // ==========================================

    private void setupRecyclerViews() {

        deviceAdapter =
                new BookingDeviceAdapter(
                        device -> {

                            selectedDevice =
                                    device;

                            String name =
                                    device.getDisplayName();

                            if (name == null
                                    || name.trim().isEmpty()) {

                                name =
                                        "Selected device";
                            }

                            tvSelectedDevice.setText(
                                    "Device: " + name
                            );
                        }
                );


        serviceAdapter =
                new BookingServiceAdapter(
                        service -> {

                            selectedService =
                                    service;

                            String name =
                                    service.getName();

                            if (name == null
                                    || name.trim().isEmpty()) {

                                name =
                                        "Selected service";
                            }

                            tvSelectedService.setText(
                                    "Service: " + name
                            );
                        }
                );


        branchAdapter =
                new BookingBranchAdapter(
                        branch -> {

                            selectedBranch =
                                    branch;

                            String name =
                                    branch.getName();

                            if (name == null
                                    || name.trim().isEmpty()) {

                                name =
                                        "Selected branch";
                            }

                            tvSelectedBranch.setText(
                                    "Branch: " + name
                            );
                        }
                );


        recyclerDevices.setLayoutManager(
                new LinearLayoutManager(
                        requireContext()
                )
        );

        recyclerServices.setLayoutManager(
                new LinearLayoutManager(
                        requireContext()
                )
        );

        recyclerBranches.setLayoutManager(
                new LinearLayoutManager(
                        requireContext()
                )
        );


        recyclerDevices.setAdapter(
                deviceAdapter
        );

        recyclerServices.setAdapter(
                serviceAdapter
        );

        recyclerBranches.setAdapter(
                branchAdapter
        );
    }


    // ==========================================
    // VIEW MODEL
    // ==========================================

    private void setupViewModel() {

        viewModel =
                new ViewModelProvider(this)
                        .get(
                                RepairBookingViewModel.class
                        );
    }


    private void observeViewModel() {

        viewModel
                .getDevices()
                .observe(
                        getViewLifecycleOwner(),
                        devices -> {

                            deviceAdapter
                                    .setDevices(
                                            devices
                                    );
                        }
                );


        viewModel
                .getServices()
                .observe(
                        getViewLifecycleOwner(),
                        services -> {

                            serviceAdapter
                                    .setServices(
                                            services
                                    );
                        }
                );


        viewModel
                .getBranches()
                .observe(
                        getViewLifecycleOwner(),
                        branches -> {

                            branchAdapter
                                    .setBranches(
                                            branches
                                    );
                        }
                );


        viewModel
                .getIsLoading()
                .observe(
                        getViewLifecycleOwner(),
                        loading -> {

                            boolean isLoading =
                                    loading != null
                                            && loading;

                            setLoadingState(
                                    isLoading
                            );
                        }
                );


        viewModel
                .getErrorMessage()
                .observe(
                        getViewLifecycleOwner(),
                        message -> {

                            if (message == null
                                    || message.trim().isEmpty()) {

                                return;
                            }

                            Toast.makeText(
                                    requireContext(),
                                    message,
                                    Toast.LENGTH_LONG
                            ).show();

                            viewModel.clearError();
                        }
                );
    }


    private void setLoadingState(
            boolean loading
    ) {

        btnContinue.setEnabled(
                !loading
        );

        btnAddDevice.setEnabled(
                !loading
        );


        if (loading) {

            btnContinue.setText(
                    "Loading..."
            );

        } else {

            btnContinue.setText(
                    "Continue"
            );
        }
    }


    // ==========================================
    // BUTTON LISTENERS
    // ==========================================

    private void setupListeners() {

        btnContinue.setOnClickListener(
                v -> {

                    if (!validateSelections()) {
                        return;
                    }

                    showDateTimeDialog();
                }
        );


        btnAddDevice.setOnClickListener(
                v -> {

                    /*
                     * Device creation belongs to
                     * the customer device module.
                     *
                     * This button can be connected
                     * when that screen is available.
                     */

                    Toast.makeText(
                            requireContext(),
                            "Add Device screen will open here",
                            Toast.LENGTH_SHORT
                    ).show();
                }
        );
    }


    // ==========================================
    // VALIDATION
    // ==========================================

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


    // ==========================================
    // DATE / TIME DIALOG
    // ==========================================

    private void showDateTimeDialog() {

        View dialogView =
                LayoutInflater
                        .from(
                                requireContext()
                        )
                        .inflate(
                                R.layout.dialog_booking_datetime,
                                null,
                                false
                        );


        TextView tvDate =
                dialogView.findViewById(
                        R.id.tv_selected_date
                );

        TextView tvTime =
                dialogView.findViewById(
                        R.id.tv_selected_time
                );

        View cardDate =
                dialogView.findViewById(
                        R.id.card_select_date
                );

        View cardTime =
                dialogView.findViewById(
                        R.id.card_select_time
                );

        MaterialButton btnCancel =
                dialogView.findViewById(
                        R.id.btn_cancel_datetime
                );

        MaterialButton btnConfirm =
                dialogView.findViewById(
                        R.id.btn_confirm_datetime
                );


        AlertDialog dialog =
                new AlertDialog.Builder(
                        requireContext()
                )
                        .setView(
                                dialogView
                        )
                        .create();


        cardDate.setOnClickListener(
                v -> showDatePicker(
                        tvDate
                )
        );


        cardTime.setOnClickListener(
                v -> showTimePicker(
                        tvTime
                )
        );


        btnCancel.setOnClickListener(
                v -> dialog.dismiss()
        );


        btnConfirm.setOnClickListener(
                v -> {

                    if (selectedDate == null
                            || selectedDate.trim().isEmpty()) {

                        Toast.makeText(
                                requireContext(),
                                "Please select a date",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }


                    if (selectedTime == null
                            || selectedTime.trim().isEmpty()) {

                        Toast.makeText(
                                requireContext(),
                                "Please select a time",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }


                    dialog.dismiss();

                    openBookingReview();
                }
        );


        dialog.show();
    }


    // ==========================================
    // DATE PICKER
    // ==========================================

    private void showDatePicker(
            TextView tvDate
    ) {

        Calendar calendar =
                Calendar.getInstance();

        int year =
                calendar.get(
                        Calendar.YEAR
                );

        int month =
                calendar.get(
                        Calendar.MONTH
                );

        int day =
                calendar.get(
                        Calendar.DAY_OF_MONTH
                );


        DatePickerDialog picker =
                new DatePickerDialog(
                        requireContext(),

                        (view,
                         selectedYear,
                         selectedMonth,
                         selectedDay) -> {

                            selectedDate =
                                    String.format(
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


        picker.getDatePicker()
                .setMinDate(
                        System.currentTimeMillis()
                                - 1000
                );


        picker.show();
    }


    // ==========================================
    // TIME PICKER
    // ==========================================

    private void showTimePicker(
            TextView tvTime
    ) {

        Calendar calendar =
                Calendar.getInstance();

        int hour =
                calendar.get(
                        Calendar.HOUR_OF_DAY
                );

        int minute =
                calendar.get(
                        Calendar.MINUTE
                );


        TimePickerDialog picker =
                new TimePickerDialog(
                        requireContext(),

                        (view,
                         selectedHour,
                         selectedMinute) -> {

                            selectedTime =
                                    String.format(
                                            Locale.getDefault(),
                                            "%02d:%02d",
                                            selectedHour,
                                            selectedMinute
                                    );


                            String displayTime =
                                    String.format(
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


                            tvTime.setText(
                                    displayTime
                            );
                        },

                        hour,
                        minute,
                        false
                );


        picker.show();
    }


    // ==========================================
    // NAVIGATION TO BOOKING REVIEW
    // ==========================================

    private void openBookingReview() {

        if (selectedDevice == null
                || selectedService == null
                || selectedBranch == null) {

            Toast.makeText(
                    requireContext(),
                    "Booking information is incomplete",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        BookingReviewFragment reviewFragment =
                BookingReviewFragment
                        .newInstance(
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


        getParentFragmentManager()
                .beginTransaction()
                .replace(
                        getId(),
                        reviewFragment
                )
                .addToBackStack(
                        null
                )
                .commit();
    }
}