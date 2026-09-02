package com.mad.techfix.ui.booking;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.mad.techfix.R;

public class BookingReviewFragment extends Fragment {

    private static final String ARG_DEVICE_ID = "device_id";
    private static final String ARG_DEVICE_NAME = "device_name";

    private static final String ARG_SERVICE_ID = "service_id";
    private static final String ARG_SERVICE_NAME = "service_name";
    private static final String ARG_SERVICE_PRICE = "service_price";

    private static final String ARG_BRANCH_ID = "branch_id";
    private static final String ARG_BRANCH_NAME = "branch_name";

    private static final String ARG_DATE = "requested_date";
    private static final String ARG_TIME = "requested_time";

    private TextView tvDevice;
    private TextView tvService;
    private TextView tvBranch;
    private TextView tvDateTime;
    private TextView tvPrice;

    private MaterialButton btnBack;
    private MaterialButton btnConfirm;

    private String deviceId;
    private String deviceName;

    private String serviceId;
    private String serviceName;
    private double servicePrice;

    private String branchId;
    private String branchName;

    private String requestedDate;
    private String requestedTime;

    public BookingReviewFragment() {
    }

    public static BookingReviewFragment newInstance(
            String deviceId,
            String deviceName,
            String serviceId,
            String serviceName,
            double servicePrice,
            String branchId,
            String branchName,
            String requestedDate,
            String requestedTime
    ) {

        BookingReviewFragment fragment =
                new BookingReviewFragment();

        Bundle args = new Bundle();

        args.putString(ARG_DEVICE_ID, deviceId);
        args.putString(ARG_DEVICE_NAME, deviceName);

        args.putString(ARG_SERVICE_ID, serviceId);
        args.putString(ARG_SERVICE_NAME, serviceName);
        args.putDouble(ARG_SERVICE_PRICE, servicePrice);

        args.putString(ARG_BRANCH_ID, branchId);
        args.putString(ARG_BRANCH_NAME, branchName);

        args.putString(ARG_DATE, requestedDate);
        args.putString(ARG_TIME, requestedTime);

        fragment.setArguments(args);

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
                R.layout.fragment_booking_review,
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

        readArguments();
        bindViews(view);
        displayBookingDetails();
        setupListeners();
    }

    private void readArguments() {

        Bundle args = getArguments();

        if (args == null) {
            return;
        }

        deviceId = args.getString(ARG_DEVICE_ID);
        deviceName = args.getString(ARG_DEVICE_NAME);

        serviceId = args.getString(ARG_SERVICE_ID);
        serviceName = args.getString(ARG_SERVICE_NAME);
        servicePrice = args.getDouble(
                ARG_SERVICE_PRICE,
                0.0
        );

        branchId = args.getString(ARG_BRANCH_ID);
        branchName = args.getString(ARG_BRANCH_NAME);

        requestedDate = args.getString(ARG_DATE);
        requestedTime = args.getString(ARG_TIME);
    }

    private void bindViews(View view) {

        tvDevice =
                view.findViewById(
                        R.id.tv_review_device
                );

        tvService =
                view.findViewById(
                        R.id.tv_review_service
                );

        tvBranch =
                view.findViewById(
                        R.id.tv_review_branch
                );

        tvDateTime =
                view.findViewById(
                        R.id.tv_review_datetime
                );

        tvPrice =
                view.findViewById(
                        R.id.tv_review_price
                );

        btnBack =
                view.findViewById(
                        R.id.btn_back_booking
                );

        btnConfirm =
                view.findViewById(
                        R.id.btn_confirm_booking
                );
    }

    private void displayBookingDetails() {

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

        String dateTime =
                safeText(
                        requestedDate,
                        "Date"
                )
                        + " • "
                        + safeText(
                        requestedTime,
                        "Time"
                );

        tvDateTime.setText(dateTime);

        tvPrice.setText(
                String.format(
                        java.util.Locale.getDefault(),
                        "LKR %,.2f",
                        servicePrice
                )
        );
    }

    private void setupListeners() {

        btnBack.setOnClickListener(v -> {

            requireActivity()
                    .getSupportFragmentManager()
                    .popBackStack();
        });

        btnConfirm.setOnClickListener(v -> {

            TextView problemField =
                    requireView().findViewById(
                            R.id.et_problem_description
                    );

            String problemDescription =
                    problemField.getText()
                            .toString()
                            .trim();

            if (problemDescription.isEmpty()) {

                Toast.makeText(
                        requireContext(),
                        "Please describe the problem",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            /*
             * Next step:
             * POST /api/appointments
             *
             * We will connect this button to the API
             * before moving to BookingConfirmationFragment.
             */

            Toast.makeText(
                    requireContext(),
                    "Booking ready to submit",
                    Toast.LENGTH_SHORT
            ).show();
        });
    }

    private String safeText(
            String value,
            String fallback
    ) {

        if (value == null
                || value.trim().isEmpty()) {

            return fallback;
        }

        return value;
    }
}