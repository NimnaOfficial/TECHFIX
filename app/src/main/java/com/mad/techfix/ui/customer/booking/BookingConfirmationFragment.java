package com.mad.techfix.ui.customer.booking;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.mad.techfix.R;

public class BookingConfirmationFragment extends Fragment {

    private static final String ARG_APPOINTMENT_NUMBER =
            "appointment_number";

    private static final String ARG_DEVICE_NAME =
            "device_name";

    private static final String ARG_SERVICE_NAME =
            "service_name";

    private static final String ARG_BRANCH_NAME =
            "branch_name";

    private static final String ARG_DATE =
            "requested_date";

    private static final String ARG_TIME =
            "requested_time";

    private static final String ARG_STATUS =
            "status";

    private TextView tvAppointmentId;
    private TextView tvDevice;
    private TextView tvService;
    private TextView tvBranch;
    private TextView tvDate;
    private TextView tvTime;
    private TextView tvStatus;

    private MaterialButton btnViewAppointments;
    private MaterialButton btnHome;

    private String appointmentNumber;
    private String deviceName;
    private String serviceName;
    private String branchName;
    private String requestedDate;
    private String requestedTime;
    private String status;

    public BookingConfirmationFragment() {
        // Required empty constructor
    }

    public static BookingConfirmationFragment newInstance(
            String appointmentNumber,
            String deviceName,
            String serviceName,
            String branchName,
            String requestedDate,
            String requestedTime,
            String status
    ) {

        BookingConfirmationFragment fragment =
                new BookingConfirmationFragment();

        Bundle args =
                new Bundle();

        args.putString(
                ARG_APPOINTMENT_NUMBER,
                appointmentNumber
        );

        args.putString(
                ARG_DEVICE_NAME,
                deviceName
        );

        args.putString(
                ARG_SERVICE_NAME,
                serviceName
        );

        args.putString(
                ARG_BRANCH_NAME,
                branchName
        );

        args.putString(
                ARG_DATE,
                requestedDate
        );

        args.putString(
                ARG_TIME,
                requestedTime
        );

        args.putString(
                ARG_STATUS,
                status
        );

        fragment.setArguments(
                args
        );

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {

        return inflater.inflate(
                R.layout.fragment_booking_confirmation,
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

        readArguments();

        bindViews(view);

        displayConfirmation();

        setupListeners();
    }

    private void readArguments() {

        Bundle args =
                getArguments();

        if (args == null) {
            return;
        }

        appointmentNumber =
                args.getString(
                        ARG_APPOINTMENT_NUMBER
                );

        deviceName =
                args.getString(
                        ARG_DEVICE_NAME
                );

        serviceName =
                args.getString(
                        ARG_SERVICE_NAME
                );

        branchName =
                args.getString(
                        ARG_BRANCH_NAME
                );

        requestedDate =
                args.getString(
                        ARG_DATE
                );

        requestedTime =
                args.getString(
                        ARG_TIME
                );

        status =
                args.getString(
                        ARG_STATUS
                );
    }

    private void bindViews(
            View view
    ) {

        tvAppointmentId =
                view.findViewById(
                        R.id.tv_confirmation_appointment_id
                );

        tvDevice =
                view.findViewById(
                        R.id.tv_confirmation_device
                );

        tvService =
                view.findViewById(
                        R.id.tv_confirmation_service
                );

        tvBranch =
                view.findViewById(
                        R.id.tv_confirmation_branch
                );

        tvDate =
                view.findViewById(
                        R.id.tv_confirmation_date
                );

        tvTime =
                view.findViewById(
                        R.id.tv_confirmation_time
                );

        tvStatus =
                view.findViewById(
                        R.id.tv_confirmation_status
                );

        btnViewAppointments =
                view.findViewById(
                        R.id.btn_view_appointments
                );

        btnHome =
                view.findViewById(
                        R.id.btn_confirmation_home
                );
    }

    private void displayConfirmation() {

        tvAppointmentId.setText(
                safeText(
                        appointmentNumber,
                        "Appointment Created"
                )
        );

        tvDevice.setText(
                safeText(
                        deviceName,
                        "Device"
                )
        );

        tvService.setText(
                safeText(
                        serviceName,
                        "Service"
                )
        );

        tvBranch.setText(
                safeText(
                        branchName,
                        "Branch"
                )
        );

        tvDate.setText(
                safeText(
                        requestedDate,
                        "Not available"
                )
        );

        tvTime.setText(
                safeText(
                        requestedTime,
                        "Not available"
                )
        );

        tvStatus.setText(
                formatStatus(
                        safeText(
                                status,
                                "REQUESTED"
                        )
                )
        );
    }

    private void setupListeners() {

        btnViewAppointments.setOnClickListener(
                v -> openMyAppointments()
        );

        btnHome.setOnClickListener(
                v -> returnHome()
        );
    }

    private void openMyAppointments() {

        MyAppointmentsFragment fragment =
                new MyAppointmentsFragment();

        getParentFragmentManager()
                .beginTransaction()
                .replace(
                        getId(),
                        fragment
                )
                .addToBackStack(
                        null
                )
                .commit();
    }

    private void returnHome() {

        getParentFragmentManager()
                .popBackStack(
                        null,
                        androidx.fragment.app.FragmentManager
                                .POP_BACK_STACK_INCLUSIVE
                );
    }

    private String formatStatus(
            String value
    ) {

        return value
                .replace(
                        "_",
                        " "
                )
                .toUpperCase();
    }

    private String safeText(
            String value,
            String fallback
    ) {

        if (value == null
                || value.trim().isEmpty()) {

            return fallback;
        }

        return value.trim();
    }
}