package com.mad.techfix.ui.customer.booking;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.mad.techfix.R;
import com.mad.techfix.models.AppointmentDetail;
import com.mad.techfix.ui.history.RepairHistoryDetailFragment;
import com.mad.techfix.viewmodel.CustomerAppointmentDetailViewModel;

import java.util.Locale;

public class CustomerAppointmentDetailBottomSheet
        extends BottomSheetDialogFragment {

    private static final String ARG_APPOINTMENT_ID =
            "appointment_id";


    private String appointmentId;


    private TextView tvAppointmentId;
    private TextView tvStatus;

    private TextView tvDevice;
    private TextView tvService;
    private TextView tvProblem;

    private TextView tvBranch;
    private TextView tvDate;
    private TextView tvTime;
    private TextView tvTechnician;

    private TextView tvCurrentStatus;
    private TextView tvStatusMessage;


    private MaterialButton btnViewRepairHistory;
    private MaterialButton btnCancelAppointment;


    private CustomerAppointmentDetailViewModel viewModel;


    public CustomerAppointmentDetailBottomSheet() {
        // Required empty constructor
    }


    // ==========================================
    // NEW INSTANCE
    // ==========================================

    public static CustomerAppointmentDetailBottomSheet
    newInstance(
            String appointmentId
    ) {

        CustomerAppointmentDetailBottomSheet sheet =
                new CustomerAppointmentDetailBottomSheet();


        Bundle args =
                new Bundle();


        args.putString(
                ARG_APPOINTMENT_ID,
                appointmentId
        );


        sheet.setArguments(
                args
        );


        return sheet;
    }


    // ==========================================
    // VIEW
    // ==========================================

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {

        return inflater.inflate(
                R.layout.bottom_sheet_customer_appointment_detail,
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

        bindViews(
                view
        );

        setupViewModel();

        observeViewModel();

        setupListeners();


        viewModel.loadAppointmentDetail(
                appointmentId
        );
    }


    // ==========================================
    // EXPANDED BOTTOM SHEET
    // ==========================================

    @NonNull
    @Override
    public Dialog onCreateDialog(
            @Nullable Bundle savedInstanceState
    ) {

        BottomSheetDialog dialog =
                (BottomSheetDialog)
                        super.onCreateDialog(
                                savedInstanceState
                        );


        dialog.setOnShowListener(
                dialogInterface -> {

                    View bottomSheet =
                            dialog.findViewById(
                                    com.google.android.material.R.id
                                            .design_bottom_sheet
                            );


                    if (bottomSheet != null) {

                        BottomSheetBehavior<View> behavior =
                                BottomSheetBehavior
                                        .from(
                                                bottomSheet
                                        );


                        behavior.setState(
                                BottomSheetBehavior
                                        .STATE_EXPANDED
                        );


                        behavior.setSkipCollapsed(
                                true
                        );
                    }
                }
        );


        return dialog;
    }


    // ==========================================
    // ARGUMENT
    // ==========================================

    private void readArguments() {

        Bundle args =
                getArguments();


        if (args == null) {

            return;
        }


        appointmentId =
                args.getString(
                        ARG_APPOINTMENT_ID
                );
    }


    // ==========================================
    // BIND VIEWS
    // ==========================================

    private void bindViews(
            View view
    ) {

        tvAppointmentId =
                view.findViewById(
                        R.id.tv_detail_appointment_id
                );


        tvStatus =
                view.findViewById(
                        R.id.tv_detail_status
                );


        tvDevice =
                view.findViewById(
                        R.id.tv_detail_device
                );


        tvService =
                view.findViewById(
                        R.id.tv_detail_service
                );


        tvProblem =
                view.findViewById(
                        R.id.tv_detail_problem
                );


        tvBranch =
                view.findViewById(
                        R.id.tv_detail_branch
                );


        tvDate =
                view.findViewById(
                        R.id.tv_detail_date
                );


        tvTime =
                view.findViewById(
                        R.id.tv_detail_time
                );


        tvTechnician =
                view.findViewById(
                        R.id.tv_detail_technician
                );


        tvCurrentStatus =
                view.findViewById(
                        R.id.tv_detail_current_status
                );


        tvStatusMessage =
                view.findViewById(
                        R.id.tv_detail_status_message
                );


        btnViewRepairHistory =
                view.findViewById(
                        R.id.btn_view_repair_history
                );


        btnCancelAppointment =
                view.findViewById(
                        R.id.btn_cancel_appointment
                );
    }


    // ==========================================
    // VIEW MODEL
    // ==========================================

    private void setupViewModel() {

        viewModel =
                new ViewModelProvider(this)
                        .get(
                                CustomerAppointmentDetailViewModel.class
                        );
    }


    private void observeViewModel() {

        viewModel
                .getIsLoading()
                .observe(
                        getViewLifecycleOwner(),
                        loading -> {

                            if (loading != null
                                    && loading) {

                                displayLoadingState();
                            }
                        }
                );


        viewModel
                .getAppointmentDetail()
                .observe(
                        getViewLifecycleOwner(),
                        detail -> {

                            if (detail != null) {

                                displayAppointmentDetail(
                                        detail
                                );
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


                            displayLoadError();


                            Toast.makeText(
                                    requireContext(),
                                    message,
                                    Toast.LENGTH_LONG
                            ).show();


                            viewModel.clearError();
                        }
                );
    }


    // ==========================================
    // LISTENERS
    // ==========================================

    private void setupListeners() {

        btnViewRepairHistory
                .setOnClickListener(
                        view -> openRepairHistory()
                );


        btnCancelAppointment
                .setOnClickListener(
                        view -> showCancellationMessage()
                );
    }


    // ==========================================
    // DISPLAY DETAIL
    // ==========================================

    private void displayAppointmentDetail(
            AppointmentDetail detail
    ) {

        String status =
                safeText(
                        detail.getStatus(),
                        "UNKNOWN"
                );


        String formattedStatus =
                formatStatus(
                        status
                );


        tvAppointmentId.setText(
                safeText(
                        detail.getAppointment_number(),
                        "Appointment"
                )
        );


        tvStatus.setText(
                formattedStatus
        );


        tvDevice.setText(
                safeText(
                        detail.getDevice_full_name(),
                        "Device information unavailable"
                )
        );


        tvService.setText(
                safeText(
                        detail.getService_name(),
                        "Service information unavailable"
                )
        );


        tvProblem.setText(
                safeText(
                        detail.getProblem_description(),
                        "No problem description provided"
                )
        );


        // Branch
        String branchDisplay =
                safeText(
                        detail.getBranch_name(),
                        "TECHFIX Branch"
                );


        if (detail.getBranch_city() != null
                && !detail.getBranch_city()
                .trim()
                .isEmpty()) {

            branchDisplay +=
                    " - "
                            + detail.getBranch_city()
                            .trim();
        }


        tvBranch.setText(
                branchDisplay
        );


        tvDate.setText(
                safeText(
                        detail.getRequested_date(),
                        "Not available"
                )
        );


        tvTime.setText(
                safeText(
                        detail.getRequested_time(),
                        "Not specified"
                )
        );


        tvTechnician.setText(
                getTechnicianName(
                        detail
                )
        );


        tvCurrentStatus.setText(
                formattedStatus
        );


        tvStatusMessage.setText(
                getStatusMessage(
                        status
                )
        );


        btnViewRepairHistory.setEnabled(
                true
        );


        updateCancelButton(
                status
        );
    }


    // ==========================================
    // TECHNICIAN
    // ==========================================

    private String getTechnicianName(
            AppointmentDetail detail
    ) {

        if (detail.getTechnician_id() == null
                || detail.getTechnician_id()
                .trim()
                .isEmpty()) {

            return "Not assigned yet";
        }


        String fullName =
                detail.getTechnician_full_name();


        if (fullName == null
                || fullName.trim().isEmpty()) {

            return "Technician assigned";
        }


        return fullName.trim();
    }


    // ==========================================
    // CANCEL BUTTON
    // ==========================================

    private void updateCancelButton(
            String status
    ) {

        String normalized =
                status == null
                        ? ""
                        : status.trim()
                        .toUpperCase(
                                Locale.US
                        );


        boolean cancellable =
                normalized.equals(
                        "REQUESTED"
                )
                        || normalized.equals(
                        "ASSIGNED"
                );


        btnCancelAppointment.setEnabled(
                cancellable
        );


        if (cancellable) {

            btnCancelAppointment.setText(
                    "Cancel Appointment"
            );

        } else {

            btnCancelAppointment.setText(
                    "Appointment Cannot Be Cancelled"
            );
        }
    }


    // ==========================================
    // BACKEND CANCELLATION PLACEHOLDER
    // ==========================================

    private void showCancellationMessage() {

        /*
         * Customer cancellation endpoint is not
         * available in the current backend.
         *
         * Keep the required cancellation UI
         * without faking a successful cancellation.
         */

        Toast.makeText(
                requireContext(),
                "Appointment cancellation is not available from the server yet.",
                Toast.LENGTH_LONG
        ).show();
    }


    // ==========================================
    // REPAIR HISTORY
    // ==========================================

    private void openRepairHistory() {

        if (appointmentId == null
                || appointmentId.trim().isEmpty()) {

            return;
        }


        dismiss();


        RepairHistoryDetailFragment fragment =
                RepairHistoryDetailFragment
                        .newInstance(
                                appointmentId
                        );


        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(
                        android.R.id.content,
                        fragment
                )
                .addToBackStack(
                        null
                )
                .commit();
    }


    // ==========================================
    // LOADING
    // ==========================================

    private void displayLoadingState() {

        tvAppointmentId.setText(
                "Loading..."
        );

        tvStatus.setText(
                "LOADING"
        );

        tvDevice.setText(
                "Loading..."
        );

        tvService.setText(
                "Loading..."
        );

        tvProblem.setText(
                "Loading..."
        );

        tvBranch.setText(
                "Loading..."
        );

        tvDate.setText(
                "Loading..."
        );

        tvTime.setText(
                "Loading..."
        );

        tvTechnician.setText(
                "Loading..."
        );

        tvCurrentStatus.setText(
                "LOADING"
        );

        tvStatusMessage.setText(
                "Loading appointment information..."
        );


        btnViewRepairHistory.setEnabled(
                false
        );

        btnCancelAppointment.setEnabled(
                false
        );
    }


    // ==========================================
    // ERROR
    // ==========================================

    private void displayLoadError() {

        tvAppointmentId.setText(
                "Appointment"
        );

        tvStatus.setText(
                "ERROR"
        );

        tvDevice.setText(
                "Unable to load"
        );

        tvService.setText(
                "Unable to load"
        );

        tvProblem.setText(
                "Unable to load appointment information."
        );

        tvBranch.setText(
                "Unable to load"
        );

        tvDate.setText(
                "Unable to load"
        );

        tvTime.setText(
                "Unable to load"
        );

        tvTechnician.setText(
                "Unable to load"
        );

        tvCurrentStatus.setText(
                "ERROR"
        );

        tvStatusMessage.setText(
                "Appointment information could not be loaded."
        );


        btnViewRepairHistory.setEnabled(
                false
        );

        btnCancelAppointment.setEnabled(
                false
        );
    }


    // ==========================================
    // STATUS MESSAGE
    // ==========================================

    private String getStatusMessage(
            String status
    ) {

        if (status == null) {

            return "Appointment status is unavailable.";
        }


        switch (
                status.trim()
                        .toUpperCase(
                                Locale.US
                        )
        ) {

            case "REQUESTED":

                return "Your appointment is waiting for technician assignment.";


            case "ASSIGNED":

                return "A technician has been assigned to your repair.";


            case "DEVICE_RECEIVED":

                return "Your device has been received by TECHFIX.";


            case "DIAGNOSING":

                return "The technician is diagnosing your device.";


            case "REPAIRING":

                return "Your device is currently being repaired.";


            case "TESTING":

                return "Your repaired device is currently being tested.";


            case "READY":

                return "Your device is ready for collection.";


            case "COMPLETED":

                return "This repair has been completed.";


            case "CANCELLED":

                return "This appointment has been cancelled.";


            default:

                return "Your repair status has been updated.";
        }
    }


    // ==========================================
    // HELPERS
    // ==========================================

    private String formatStatus(
            String status
    ) {

        if (status == null
                || status.trim().isEmpty()) {

            return "UNKNOWN";
        }


        return status
                .trim()
                .replace(
                        "_",
                        " "
                )
                .toUpperCase(
                        Locale.US
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


        return value.trim();
    }
}