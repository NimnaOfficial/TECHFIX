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
import com.google.android.material.textfield.TextInputEditText;
import com.mad.techfix.R;
import com.mad.techfix.data.SessionManager;
import com.mad.techfix.models.ApiResponse;
import com.mad.techfix.network.ApiService;
import com.mad.techfix.network.RetrofitClient;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    private TextInputEditText etProblemDescription;

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

    private ApiService apiService;
    private SessionManager sessionManager;

    public BookingReviewFragment() {
        // Required empty constructor
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

        args.putString(
                ARG_DEVICE_ID,
                deviceId
        );

        args.putString(
                ARG_DEVICE_NAME,
                deviceName
        );

        args.putString(
                ARG_SERVICE_ID,
                serviceId
        );

        args.putString(
                ARG_SERVICE_NAME,
                serviceName
        );

        args.putDouble(
                ARG_SERVICE_PRICE,
                servicePrice
        );

        args.putString(
                ARG_BRANCH_ID,
                branchId
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

        super.onViewCreated(
                view,
                savedInstanceState
        );

        apiService =
                RetrofitClient.getApiService();

        sessionManager =
                new SessionManager(
                        requireContext()
                );

        readArguments();

        bindViews(view);

        displayBookingDetails();

        setupListeners();
    }

    private void readArguments() {

        Bundle args =
                getArguments();

        if (args == null) {
            return;
        }

        deviceId =
                args.getString(
                        ARG_DEVICE_ID
                );

        deviceName =
                args.getString(
                        ARG_DEVICE_NAME
                );

        serviceId =
                args.getString(
                        ARG_SERVICE_ID
                );

        serviceName =
                args.getString(
                        ARG_SERVICE_NAME
                );

        servicePrice =
                args.getDouble(
                        ARG_SERVICE_PRICE,
                        0.0
                );

        branchId =
                args.getString(
                        ARG_BRANCH_ID
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
    }

    private void bindViews(
            View view
    ) {

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

        etProblemDescription =
                view.findViewById(
                        R.id.et_problem_description
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

        tvDateTime.setText(
                dateTime
        );

        tvPrice.setText(
                String.format(
                        Locale.getDefault(),
                        "LKR %,.2f",
                        servicePrice
                )
        );
    }

    private void setupListeners() {

        btnBack.setOnClickListener(
                v -> {

                    requireActivity()
                            .getSupportFragmentManager()
                            .popBackStack();
                }
        );

        btnConfirm.setOnClickListener(
                v -> {

                    String problemDescription =
                            "";

                    if (etProblemDescription
                            .getText() != null) {

                        problemDescription =
                                etProblemDescription
                                        .getText()
                                        .toString()
                                        .trim();
                    }

                    if (problemDescription.isEmpty()) {

                        Toast.makeText(
                                requireContext(),
                                "Please describe the problem",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    submitBooking(
                            problemDescription
                    );
                }
        );
    }

    private void submitBooking(
            String problemDescription
    ) {

        String token =
                sessionManager
                        .getBearerToken();

        if (token == null
                || token.trim().isEmpty()) {

            Toast.makeText(
                    requireContext(),
                    "Please sign in again",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (deviceId == null
                || deviceId.trim().isEmpty()
                || serviceId == null
                || serviceId.trim().isEmpty()
                || branchId == null
                || branchId.trim().isEmpty()
                || requestedDate == null
                || requestedDate.trim().isEmpty()
                || requestedTime == null
                || requestedTime.trim().isEmpty()) {

            Toast.makeText(
                    requireContext(),
                    "Booking information is incomplete",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        Map<String, Object> body =
                new HashMap<>();

        body.put(
                "device_id",
                deviceId
        );

        body.put(
                "service_id",
                serviceId
        );

        body.put(
                "branch_id",
                branchId
        );

        body.put(
                "requested_date",
                requestedDate
        );

        body.put(
                "requested_time",
                requestedTime
        );

        body.put(
                "problem_description",
                problemDescription
        );

        btnConfirm.setEnabled(
                false
        );

        btnConfirm.setText(
                "Creating..."
        );

        apiService
                .createAppointment(
                        token,
                        body
                )
                .enqueue(
                        new Callback<
                                ApiResponse<
                                        Map<String, Object>
                                        >
                                >() {

                            @Override
                            public void onResponse(

                                    @NonNull
                                    Call<
                                            ApiResponse<
                                                    Map<String, Object>
                                                    >
                                            > call,

                                    @NonNull
                                    Response<
                                            ApiResponse<
                                                    Map<String, Object>
                                                    >
                                            > response
                            ) {

                                if (!isAdded()) {
                                    return;
                                }

                                if (response.isSuccessful()
                                        && response.body() != null
                                        && response.body()
                                        .isSuccess()) {

                                    Map<String, Object> data =
                                            response.body()
                                                    .getData();

                                    String appointmentNumber =
                                            "";

                                    String appointmentStatus =
                                            "REQUESTED";

                                    if (data != null) {

                                        Object appointmentNumberValue =
                                                data.get(
                                                        "appointment_number"
                                                );

                                        if (appointmentNumberValue
                                                != null) {

                                            appointmentNumber =
                                                    String.valueOf(
                                                            appointmentNumberValue
                                                    );
                                        }

                                        Object technicianId =
                                                data.get(
                                                        "technician_id"
                                                );

                                        /*
                                         * Backend automatically assigns
                                         * an available technician.
                                         *
                                         * If technician_id exists,
                                         * appointment status is ASSIGNED.
                                         *
                                         * Otherwise it remains REQUESTED.
                                         */

                                        if (technicianId != null) {

                                            appointmentStatus =
                                                    "ASSIGNED";
                                        }
                                    }

                                    openConfirmationScreen(
                                            appointmentNumber,
                                            appointmentStatus
                                    );

                                } else {

                                    resetConfirmButton();

                                    String message =
                                            "Unable to create appointment";

                                    if (response.body() != null
                                            && response.body()
                                            .getMessage() != null
                                            && !response.body()
                                            .getMessage()
                                            .trim()
                                            .isEmpty()) {

                                        message =
                                                response.body()
                                                        .getMessage();
                                    }

                                    Toast.makeText(
                                            requireContext(),
                                            message,
                                            Toast.LENGTH_LONG
                                    ).show();
                                }
                            }

                            @Override
                            public void onFailure(

                                    @NonNull
                                    Call<
                                            ApiResponse<
                                                    Map<String, Object>
                                                    >
                                            > call,

                                    @NonNull
                                    Throwable t
                            ) {

                                if (!isAdded()) {
                                    return;
                                }

                                resetConfirmButton();

                                String errorMessage =
                                        "Connection failed";

                                if (t.getMessage()
                                        != null) {

                                    errorMessage +=
                                            ": "
                                                    + t.getMessage();
                                }

                                Toast.makeText(
                                        requireContext(),
                                        errorMessage,
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
    }

    private void openConfirmationScreen(
            String appointmentNumber,
            String appointmentStatus
    ) {

        BookingConfirmationFragment
                confirmationFragment =
                BookingConfirmationFragment
                        .newInstance(
                                appointmentNumber,
                                deviceName,
                                serviceName,
                                branchName,
                                requestedDate,
                                requestedTime,
                                appointmentStatus
                        );

        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(
                        getId(),
                        confirmationFragment
                )
                .addToBackStack(
                        null
                )
                .commit();
    }

    private void resetConfirmButton() {

        btnConfirm.setEnabled(
                true
        );

        btnConfirm.setText(
                "Confirm Booking"
        );
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