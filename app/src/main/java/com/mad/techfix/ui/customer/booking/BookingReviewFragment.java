package com.mad.techfix.ui.customer.booking;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.mad.techfix.R;
import com.mad.techfix.viewmodel.BookingReviewViewModel;

import java.util.Locale;

public class BookingReviewFragment extends Fragment {

    private static final String ARG_DEVICE_ID =
            "device_id";

    private static final String ARG_DEVICE_NAME =
            "device_name";

    private static final String ARG_SERVICE_ID =
            "service_id";

    private static final String ARG_SERVICE_NAME =
            "service_name";

    private static final String ARG_SERVICE_PRICE =
            "service_price";

    private static final String ARG_BRANCH_ID =
            "branch_id";

    private static final String ARG_BRANCH_NAME =
            "branch_name";

    private static final String ARG_DATE =
            "requested_date";

    private static final String ARG_TIME =
            "requested_time";


    private TextView tvDevice;
    private TextView tvService;
    private TextView tvBranch;
    private TextView tvDateTime;
    private TextView tvPrice;

    private TextInputEditText etProblemDescription;

    private MaterialButton btnBack;
    private MaterialButton btnConfirm;


    private BookingReviewViewModel viewModel;

    private LocationManager locationManager;
    private LocationListener activeLocationListener;


    private String deviceId;
    private String deviceName;

    private String serviceId;
    private String serviceName;
    private double servicePrice;

    private String branchId;
    private String branchName;

    private String requestedDate;
    private String requestedTime;

    private String pendingProblemDescription;

    private boolean waitingForLocation = false;


    // ==========================================
    // LOCATION PERMISSION
    // ==========================================

    private final ActivityResultLauncher<String[]>
            locationPermissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts
                            .RequestMultiplePermissions(),

                    result -> {

                        boolean fineGranted =
                                Boolean.TRUE.equals(
                                        result.get(
                                                Manifest.permission
                                                        .ACCESS_FINE_LOCATION
                                        )
                                );

                        boolean coarseGranted =
                                Boolean.TRUE.equals(
                                        result.get(
                                                Manifest.permission
                                                        .ACCESS_COARSE_LOCATION
                                        )
                                );


                        if (fineGranted
                                || coarseGranted) {

                            obtainLocationAndSubmit();

                        } else {

                            waitingForLocation =
                                    false;

                            resetConfirmButton();

                            Toast.makeText(
                                    requireContext(),
                                    "Location permission is required to create a repair booking",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }
            );


    public BookingReviewFragment() {
        // Required empty constructor
    }


    // ==========================================
    // NEW INSTANCE
    // ==========================================

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

        Bundle args =
                new Bundle();


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


        fragment.setArguments(
                args
        );

        return fragment;
    }


    // ==========================================
    // FRAGMENT
    // ==========================================

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


        locationManager =
                (LocationManager)
                        requireContext()
                                .getSystemService(
                                        android.content.Context
                                                .LOCATION_SERVICE
                                );


        readArguments();

        bindViews(
                view
        );

        setupViewModel();

        observeViewModel();

        displayBookingDetails();

        setupListeners();
    }


    // ==========================================
    // ARGUMENTS
    // ==========================================

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


    // ==========================================
    // VIEW BINDING
    // ==========================================

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


    // ==========================================
    // VIEW MODEL
    // ==========================================

    private void setupViewModel() {

        viewModel =
                new ViewModelProvider(this)
                        .get(
                                BookingReviewViewModel.class
                        );
    }


    private void observeViewModel() {

        viewModel
                .getIsLoading()
                .observe(
                        getViewLifecycleOwner(),
                        loading -> {

                            boolean isLoading =
                                    loading != null
                                            && loading;


                            if (isLoading) {

                                btnConfirm.setEnabled(
                                        false
                                );

                                btnConfirm.setText(
                                        "Creating..."
                                );

                            } else if (!waitingForLocation) {

                                resetConfirmButton();
                            }
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


                            waitingForLocation =
                                    false;

                            resetConfirmButton();


                            Toast.makeText(
                                    requireContext(),
                                    message,
                                    Toast.LENGTH_LONG
                            ).show();


                            viewModel.clearError();
                        }
                );


        viewModel
                .getBookingResult()
                .observe(
                        getViewLifecycleOwner(),
                        data -> {

                            if (data == null) {

                                return;
                            }


                            waitingForLocation =
                                    false;


                            String appointmentNumber =
                                    "";

                            String appointmentStatus =
                                    "REQUESTED";


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
                             * an available technician when
                             * possible.
                             *
                             * If technician_id exists,
                             * status is ASSIGNED.
                             *
                             * Otherwise the appointment
                             * remains REQUESTED.
                             */

                            if (technicianId != null
                                    && !String.valueOf(
                                            technicianId
                                    )
                                    .trim()
                                    .isEmpty()
                                    && !"null".equalsIgnoreCase(
                                    String.valueOf(
                                            technicianId
                                    )
                            )) {

                                appointmentStatus =
                                        "ASSIGNED";
                            }


                            viewModel
                                    .clearBookingResult();


                            openConfirmationScreen(
                                    appointmentNumber,
                                    appointmentStatus
                            );
                        }
                );
    }


    // ==========================================
    // DISPLAY BOOKING
    // ==========================================

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


    // ==========================================
    // LISTENERS
    // ==========================================

    private void setupListeners() {

        btnBack.setOnClickListener(
                v -> {

                    getParentFragmentManager()
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


                    if (!bookingArgumentsAreValid()) {

                        Toast.makeText(
                                requireContext(),
                                "Booking information is incomplete",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }


                    beginLocationAndBooking(
                            problemDescription
                    );
                }
        );
    }


    // ==========================================
    // VALIDATION
    // ==========================================

    private boolean bookingArgumentsAreValid() {

        return deviceId != null
                && !deviceId.trim().isEmpty()

                && serviceId != null
                && !serviceId.trim().isEmpty()

                && branchId != null
                && !branchId.trim().isEmpty()

                && requestedDate != null
                && !requestedDate.trim().isEmpty()

                && requestedTime != null
                && !requestedTime.trim().isEmpty();
    }


    // ==========================================
    // LOCATION FLOW
    // ==========================================

    private void beginLocationAndBooking(
            String problemDescription
    ) {

        if (waitingForLocation) {

            return;
        }


        pendingProblemDescription =
                problemDescription;

        waitingForLocation =
                true;


        btnConfirm.setEnabled(
                false
        );

        btnConfirm.setText(
                "Getting location..."
        );


        if (hasLocationPermission()) {

            obtainLocationAndSubmit();

        } else {

            locationPermissionLauncher.launch(
                    new String[]{
                            Manifest.permission
                                    .ACCESS_FINE_LOCATION,

                            Manifest.permission
                                    .ACCESS_COARSE_LOCATION
                    }
            );
        }
    }


    private boolean hasLocationPermission() {

        boolean fineGranted =
                ActivityCompat
                        .checkSelfPermission(
                                requireContext(),
                                Manifest.permission
                                        .ACCESS_FINE_LOCATION
                        )
                        == PackageManager
                        .PERMISSION_GRANTED;


        boolean coarseGranted =
                ActivityCompat
                        .checkSelfPermission(
                                requireContext(),
                                Manifest.permission
                                        .ACCESS_COARSE_LOCATION
                        )
                        == PackageManager
                        .PERMISSION_GRANTED;


        return fineGranted
                || coarseGranted;
    }


    // ==========================================
    // OBTAIN LOCATION
    // ==========================================

    private void obtainLocationAndSubmit() {

        if (!isAdded()) {

            return;
        }


        if (!hasLocationPermission()) {

            waitingForLocation =
                    false;

            resetConfirmButton();

            return;
        }


        if (locationManager == null) {

            locationUnavailable(
                    "Location service is unavailable on this device"
            );

            return;
        }


        boolean gpsEnabled =
                false;

        boolean networkEnabled =
                false;


        try {

            gpsEnabled =
                    locationManager
                            .isProviderEnabled(
                                    LocationManager
                                            .GPS_PROVIDER
                            );

        } catch (Exception ignored) {
        }


        try {

            networkEnabled =
                    locationManager
                            .isProviderEnabled(
                                    LocationManager
                                            .NETWORK_PROVIDER
                            );

        } catch (Exception ignored) {
        }


        if (!gpsEnabled
                && !networkEnabled) {

            waitingForLocation =
                    false;

            resetConfirmButton();


            Toast.makeText(
                    requireContext(),
                    "Please turn on Location and try again",
                    Toast.LENGTH_LONG
            ).show();


            try {

                Intent intent =
                        new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS
                        );

                startActivity(
                        intent
                );

            } catch (Exception ignored) {
            }


            return;
        }


        try {

            Location bestLocation =
                    getBestLastKnownLocation(
                            gpsEnabled,
                            networkEnabled
                    );


            if (bestLocation != null) {

                submitWithLocation(
                        bestLocation
                );

                return;
            }


            requestFreshLocation(
                    gpsEnabled,
                    networkEnabled
            );

        } catch (SecurityException exception) {

            locationUnavailable(
                    "Location permission is required to create a booking"
            );
        }
    }


    // ==========================================
    // LAST KNOWN LOCATION
    // ==========================================

    @Nullable
    private Location getBestLastKnownLocation(
            boolean gpsEnabled,
            boolean networkEnabled
    ) {

        Location gpsLocation =
                null;

        Location networkLocation =
                null;


        boolean fineGranted =
                ActivityCompat
                        .checkSelfPermission(
                                requireContext(),
                                Manifest.permission
                                        .ACCESS_FINE_LOCATION
                        )
                        == PackageManager
                        .PERMISSION_GRANTED;


        boolean coarseGranted =
                ActivityCompat
                        .checkSelfPermission(
                                requireContext(),
                                Manifest.permission
                                        .ACCESS_COARSE_LOCATION
                        )
                        == PackageManager
                        .PERMISSION_GRANTED;


        if (!fineGranted
                && !coarseGranted) {

            return null;
        }


        try {

            if (gpsEnabled
                    && fineGranted) {

                gpsLocation =
                        locationManager
                                .getLastKnownLocation(
                                        LocationManager
                                                .GPS_PROVIDER
                                );
            }


            if (networkEnabled) {

                networkLocation =
                        locationManager
                                .getLastKnownLocation(
                                        LocationManager
                                                .NETWORK_PROVIDER
                                );
            }

        } catch (SecurityException ignored) {

            return null;
        }


        if (gpsLocation == null) {

            return networkLocation;
        }


        if (networkLocation == null) {

            return gpsLocation;
        }


        if (gpsLocation.getTime()
                >= networkLocation.getTime()) {

            return gpsLocation;
        }


        return networkLocation;
    }


    // ==========================================
    // FRESH LOCATION
    // ==========================================

    private void requestFreshLocation(
            boolean gpsEnabled,
            boolean networkEnabled
    ) {

        boolean fineGranted =
                ActivityCompat
                        .checkSelfPermission(
                                requireContext(),
                                Manifest.permission
                                        .ACCESS_FINE_LOCATION
                        )
                        == PackageManager
                        .PERMISSION_GRANTED;


        boolean coarseGranted =
                ActivityCompat
                        .checkSelfPermission(
                                requireContext(),
                                Manifest.permission
                                        .ACCESS_COARSE_LOCATION
                        )
                        == PackageManager
                        .PERMISSION_GRANTED;


        if (!fineGranted
                && !coarseGranted) {

            locationUnavailable(
                    "Location permission is required"
            );

            return;
        }


        String provider =
                null;


        /*
         * Network location is usually faster
         * indoors.
         *
         * If it is unavailable, use GPS.
         */

        if (networkEnabled) {

            provider =
                    LocationManager
                            .NETWORK_PROVIDER;

        } else if (gpsEnabled
                && fineGranted) {

            provider =
                    LocationManager
                            .GPS_PROVIDER;
        }


        if (provider == null) {

            locationUnavailable(
                    "Unable to obtain your current location"
            );

            return;
        }


        activeLocationListener =
                new LocationListener() {

                    @Override
                    public void onLocationChanged(
                            @NonNull Location location
                    ) {

                        removeLocationUpdates();

                        submitWithLocation(
                                location
                        );
                    }


                    @Override
                    public void onProviderEnabled(
                            @NonNull String provider
                    ) {
                    }


                    @Override
                    public void onProviderDisabled(
                            @NonNull String provider
                    ) {
                    }


                    @Override
                    @SuppressWarnings("deprecation")
                    public void onStatusChanged(
                            String provider,
                            int status,
                            Bundle extras
                    ) {
                    }
                };


        try {

            locationManager
                    .requestSingleUpdate(
                            provider,
                            activeLocationListener,
                            Looper.getMainLooper()
                    );

        } catch (SecurityException exception) {

            locationUnavailable(
                    "Location permission is required"
            );

        } catch (Exception exception) {

            locationUnavailable(
                    "Unable to obtain your current location"
            );
        }
    }


    // ==========================================
    // SUBMIT USING LOCATION
    // ==========================================

    private void submitWithLocation(
            Location location
    ) {

        if (!isAdded()) {

            return;
        }


        if (location == null) {

            locationUnavailable(
                    "Unable to obtain your current location"
            );

            return;
        }


        double latitude =
                location.getLatitude();

        double longitude =
                location.getLongitude();


        if (Double.isNaN(latitude)
                || Double.isNaN(longitude)) {

            locationUnavailable(
                    "Invalid location received"
            );

            return;
        }


        waitingForLocation =
                false;


        viewModel
                .createAppointment(
                        deviceId,
                        serviceId,
                        branchId,
                        requestedDate,
                        requestedTime,
                        pendingProblemDescription,
                        latitude,
                        longitude
                );
    }


    // ==========================================
    // LOCATION FAILURE
    // ==========================================

    private void locationUnavailable(
            String message
    ) {

        waitingForLocation =
                false;

        removeLocationUpdates();

        resetConfirmButton();


        if (!isAdded()) {

            return;
        }


        Toast.makeText(
                requireContext(),
                message,
                Toast.LENGTH_LONG
        ).show();
    }


    private void removeLocationUpdates() {

        if (locationManager == null
                || activeLocationListener == null) {

            return;
        }


        try {

            locationManager
                    .removeUpdates(
                            activeLocationListener
                    );

        } catch (SecurityException ignored) {
        }


        activeLocationListener =
                null;
    }


    // ==========================================
    // CONFIRMATION
    // ==========================================

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


        getParentFragmentManager()
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


    // ==========================================
    // BUTTON STATE
    // ==========================================

    private void resetConfirmButton() {

        if (btnConfirm == null) {

            return;
        }


        btnConfirm.setEnabled(
                true
        );

        btnConfirm.setText(
                "Confirm Booking"
        );
    }


    // ==========================================
    // HELPERS
    // ==========================================

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


    // ==========================================
    // CLEANUP
    // ==========================================

    @Override
    public void onDestroyView() {

        removeLocationUpdates();

        super.onDestroyView();
    }
}