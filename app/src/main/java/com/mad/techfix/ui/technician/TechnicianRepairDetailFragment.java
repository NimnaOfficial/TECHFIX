package com.mad.techfix.ui.technician;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.mad.techfix.R;
import com.mad.techfix.data.SessionManager;
import com.mad.techfix.models.ApiResponse;
import com.mad.techfix.models.AppointmentDetail;
import com.mad.techfix.network.ApiService;
import com.mad.techfix.network.RetrofitClient;
import com.mad.techfix.ui.history.RepairHistoryDetailFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TechnicianRepairDetailFragment extends Fragment {

    private static final String ARG_APPOINTMENT_ID =
            "appointment_id";

    private String appointmentId;

    private ApiService apiService;
    private SessionManager sessionManager;

    private AppointmentDetail currentDetail;

    private ImageButton btnBack;

    private TextView tvRepairId;
    private TextView tvRepairStatus;

    private TextView tvCustomerName;
    private TextView tvCustomerPhone;
    private TextView tvCustomerEmail;

    private TextView tvDeviceName;
    private TextView tvDeviceModel;
    private TextView tvDeviceSerial;

    private TextView tvService;
    private TextView tvProblem;
    private TextView tvBranch;

    private TextView tvStatusReceived;
    private TextView tvStatusDiagnosing;
    private TextView tvStatusRepairing;
    private TextView tvStatusTesting;
    private TextView tvStatusCompleted;

    private TextView btnEditRepairNotes;
    private TextView tvRepairNotes;

    private TextView btnAddRepairImage;

    private RecyclerView recyclerRepairImages;

    private MaterialButton btnUpdateRepairStatus;
    private MaterialButton btnViewRepairHistory;

    private final List<String> repairImageUrls =
            new ArrayList<>();

    private RepairImageUrlAdapter repairImageAdapter;

    public TechnicianRepairDetailFragment() {
        // Required empty constructor
    }

    public static TechnicianRepairDetailFragment newInstance(
            String appointmentId
    ) {

        TechnicianRepairDetailFragment fragment =
                new TechnicianRepairDetailFragment();

        Bundle args =
                new Bundle();

        args.putString(
                ARG_APPOINTMENT_ID,
                appointmentId
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
                R.layout.fragment_technician_repair_detail,
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

        setupRecyclerView();

        setupListeners();

        if (!isAppointmentIdValid()) {

            Toast.makeText(
                    requireContext(),
                    "Repair ID is unavailable",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        loadRepairData();
    }

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

    private void bindViews(
            View view
    ) {

        btnBack =
                view.findViewById(
                        R.id.btn_back_repair_detail
                );

        tvRepairId =
                view.findViewById(
                        R.id.tv_repair_detail_id
                );

        tvRepairStatus =
                view.findViewById(
                        R.id.tv_repair_detail_status
                );

        tvCustomerName =
                view.findViewById(
                        R.id.tv_repair_customer_name
                );

        tvCustomerPhone =
                view.findViewById(
                        R.id.tv_repair_customer_phone
                );

        tvCustomerEmail =
                view.findViewById(
                        R.id.tv_repair_customer_email
                );

        tvDeviceName =
                view.findViewById(
                        R.id.tv_repair_device_name
                );

        tvDeviceModel =
                view.findViewById(
                        R.id.tv_repair_device_model
                );

        tvDeviceSerial =
                view.findViewById(
                        R.id.tv_repair_device_serial
                );

        tvService =
                view.findViewById(
                        R.id.tv_repair_service
                );

        tvProblem =
                view.findViewById(
                        R.id.tv_repair_problem
                );

        tvBranch =
                view.findViewById(
                        R.id.tv_repair_branch
                );

        tvStatusReceived =
                view.findViewById(
                        R.id.tv_status_received
                );

        tvStatusDiagnosing =
                view.findViewById(
                        R.id.tv_status_diagnosing
                );

        tvStatusRepairing =
                view.findViewById(
                        R.id.tv_status_repairing
                );

        tvStatusTesting =
                view.findViewById(
                        R.id.tv_status_testing
                );

        tvStatusCompleted =
                view.findViewById(
                        R.id.tv_status_completed
                );

        btnEditRepairNotes =
                view.findViewById(
                        R.id.btn_edit_repair_notes
                );

        tvRepairNotes =
                view.findViewById(
                        R.id.tv_repair_notes
                );

        btnAddRepairImage =
                view.findViewById(
                        R.id.btn_add_repair_image
                );

        recyclerRepairImages =
                view.findViewById(
                        R.id.recycler_repair_images
                );

        btnUpdateRepairStatus =
                view.findViewById(
                        R.id.btn_update_repair_status
                );

        btnViewRepairHistory =
                view.findViewById(
                        R.id.btn_view_repair_history_technician
                );
    }

    private void setupRecyclerView() {

        repairImageAdapter =
                new RepairImageUrlAdapter(
                        repairImageUrls
                );

        recyclerRepairImages.setLayoutManager(
                new LinearLayoutManager(
                        requireContext()
                )
        );

        recyclerRepairImages.setAdapter(
                repairImageAdapter
        );

        recyclerRepairImages.setNestedScrollingEnabled(
                false
        );
    }

    private void setupListeners() {

        btnBack.setOnClickListener(
                v -> getParentFragmentManager()
                        .popBackStack()
        );

        btnUpdateRepairStatus.setOnClickListener(
                v -> showStatusDialog()
        );

        btnEditRepairNotes.setOnClickListener(
                v -> showRepairNotesDialog()
        );

        btnAddRepairImage.setOnClickListener(
                v -> showAddImageDialog()
        );

        btnViewRepairHistory.setOnClickListener(
                v -> openRepairHistory()
        );
    }

    private void loadRepairData() {

        loadAppointmentDetail();

        loadRepairNotes();

        loadRepairImages();
    }

    private void loadAppointmentDetail() {

        String token =
                getToken();

        if (token == null) {
            return;
        }

        btnUpdateRepairStatus.setEnabled(
                false
        );

        apiService
                .getAppointmentDetail(
                        token,
                        appointmentId
                )
                .enqueue(
                        new Callback<
                                ApiResponse<
                                        AppointmentDetail
                                        >
                                >() {

                            @Override
                            public void onResponse(
                                    @NonNull
                                    Call<
                                            ApiResponse<
                                                    AppointmentDetail
                                                    >
                                            > call,

                                    @NonNull
                                    Response<
                                            ApiResponse<
                                                    AppointmentDetail
                                                    >
                                            > response
                            ) {

                                if (!isAdded()) {
                                    return;
                                }

                                if (response.isSuccessful()
                                        && response.body() != null
                                        && response.body()
                                        .isSuccess()
                                        && response.body()
                                        .getData() != null) {

                                    currentDetail =
                                            response.body()
                                                    .getData();

                                    displayRepairDetail(
                                            currentDetail
                                    );

                                } else {

                                    Toast.makeText(
                                            requireContext(),
                                            "Unable to load repair details",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                }
                            }

                            @Override
                            public void onFailure(
                                    @NonNull
                                    Call<
                                            ApiResponse<
                                                    AppointmentDetail
                                                    >
                                            > call,

                                    @NonNull
                                    Throwable t
                            ) {

                                if (!isAdded()) {
                                    return;
                                }

                                Toast.makeText(
                                        requireContext(),
                                        "Repair loading failed: "
                                                + safeError(t),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
    }

    private void displayRepairDetail(
            AppointmentDetail detail
    ) {

        tvRepairId.setText(
                safeText(
                        detail.getAppointment_number(),
                        "Repair"
                )
        );

        String status =
                normalizeStatus(
                        detail.getStatus()
                );

        tvRepairStatus.setText(
                formatStatus(status)
        );

        String customerName =
                detail.getCustomer_full_name();

        if (customerName == null
                || customerName.trim().isEmpty()) {

            if (detail.getCustomer_id() != null
                    && !detail.getCustomer_id()
                    .trim()
                    .isEmpty()) {

                customerName =
                        "Customer "
                                + detail.getCustomer_id();

            } else {

                customerName =
                        "Customer information unavailable";
            }
        }

        tvCustomerName.setText(
                customerName.trim()
        );

        /*
         * The current appointment-detail API
         * does not return customer phone/email.
         */
        tvCustomerPhone.setText(
                "Phone: Not available"
        );

        tvCustomerEmail.setText(
                "Email: Not available"
        );

        String deviceName =
                detail.getDevice_full_name();

        if (deviceName == null
                || deviceName.trim().isEmpty()) {

            if (detail.getDevice_id() != null
                    && !detail.getDevice_id()
                    .trim()
                    .isEmpty()) {

                deviceName =
                        "Device "
                                + detail.getDevice_id();

            } else {

                deviceName =
                        "Device information unavailable";
            }
        }

        tvDeviceName.setText(
                deviceName.trim()
        );

        tvDeviceModel.setText(
                "Model: "
                        + safeText(
                        detail.getDevice_model(),
                        "Not available"
                )
        );

        tvDeviceSerial.setText(
                "Serial: "
                        + safeText(
                        detail.getSerial_number(),
                        "Not available"
                )
        );

        tvService.setText(
                safeText(
                        detail.getService_name(),
                        "Repair Service"
                )
        );

        tvProblem.setText(
                safeText(
                        detail.getProblem_description(),
                        "No problem description provided"
                )
        );

        String branch =
                safeText(
                        detail.getBranch_name(),
                        "TECHFIX Branch"
                );

        if (detail.getBranch_city() != null
                && !detail.getBranch_city()
                .trim()
                .isEmpty()) {

            branch +=
                    " - "
                            + detail.getBranch_city()
                            .trim();
        }

        tvBranch.setText(
                branch
        );

        updateRepairProgress(
                status
        );

        updateActionButtons(
                status
        );
    }

    private void updateRepairProgress(
            String status
    ) {

        int currentStage =
                getStageIndex(status);

        setStageText(
                tvStatusReceived,
                "DEVICE_RECEIVED",
                0,
                currentStage
        );

        setStageText(
                tvStatusDiagnosing,
                "DIAGNOSING",
                1,
                currentStage
        );

        setStageText(
                tvStatusRepairing,
                "REPAIRING",
                2,
                currentStage
        );

        setStageText(
                tvStatusTesting,
                "TESTING",
                3,
                currentStage
        );

        setStageText(
                tvStatusCompleted,
                "COMPLETED",
                4,
                currentStage
        );
    }

    private void setStageText(
            TextView textView,
            String stageName,
            int stageIndex,
            int currentStage
    ) {

        String prefix;

        if (currentStage > stageIndex) {

            prefix = "✓ ";

        } else if (currentStage == stageIndex) {

            prefix = "● ";

        } else {

            prefix = "○ ";
        }

        textView.setText(
                prefix + stageName
        );
    }

    private int getStageIndex(
            String status
    ) {

        switch (status) {

            case "DEVICE_RECEIVED":
                return 0;

            case "DIAGNOSING":
                return 1;

            case "REPAIRING":
                return 2;

            case "TESTING":
                return 3;

            case "READY":
                /*
                 * READY comes after TESTING but
                 * before COMPLETED.
                 */
                return 4;

            case "COMPLETED":
                return 5;

            default:
                return -1;
        }
    }

    private void updateActionButtons(
            String status
    ) {

        boolean finished =
                "COMPLETED".equals(status)
                        || "CANCELLED".equals(status);

        btnUpdateRepairStatus.setEnabled(
                !finished
        );

        btnEditRepairNotes.setEnabled(
                !finished
        );

        if (finished) {

            btnUpdateRepairStatus.setText(
                    "Repair Finished"
            );

        } else {

            btnUpdateRepairStatus.setText(
                    "Update Repair Status"
            );
        }
    }

    private void showStatusDialog() {

        if (currentDetail == null) {

            Toast.makeText(
                    requireContext(),
                    "Repair details are still loading",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        View dialogView =
                LayoutInflater
                        .from(
                                requireContext()
                        )
                        .inflate(
                                R.layout.dialog_update_repair_status,
                                null,
                                false
                        );

        TextView tvCurrentStatus =
                dialogView.findViewById(
                        R.id.tv_current_repair_status
                );

        RadioGroup radioGroup =
                dialogView.findViewById(
                        R.id.radio_group_repair_status
                );

        TextInputEditText etStatusNote =
                dialogView.findViewById(
                        R.id.et_status_note
                );

        MaterialButton btnCancel =
                dialogView.findViewById(
                        R.id.btn_cancel_status_update
                );

        MaterialButton btnConfirm =
                dialogView.findViewById(
                        R.id.btn_confirm_status_update
                );

        String currentStatus =
                normalizeStatus(
                        currentDetail.getStatus()
                );

        tvCurrentStatus.setText(
                formatStatus(
                        currentStatus
                )
        );

        selectCurrentStatus(
                radioGroup,
                currentStatus
        );

        AlertDialog dialog =
                new AlertDialog.Builder(
                        requireContext()
                )
                        .setView(dialogView)
                        .create();

        btnCancel.setOnClickListener(
                v -> dialog.dismiss()
        );

        btnConfirm.setOnClickListener(
                v -> {

                    String selectedStatus =
                            getSelectedStatus(
                                    radioGroup
                            );

                    if (selectedStatus == null) {

                        Toast.makeText(
                                requireContext(),
                                "Please select a status",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    String note =
                            "";

                    if (etStatusNote.getText()
                            != null) {

                        note =
                                etStatusNote
                                        .getText()
                                        .toString()
                                        .trim();
                    }

                    updateRepairStatus(
                            selectedStatus,
                            note,
                            dialog,
                            btnConfirm
                    );
                }
        );

        dialog.show();
    }

    private void selectCurrentStatus(
            RadioGroup radioGroup,
            String status
    ) {

        switch (status) {

            case "DEVICE_RECEIVED":

                radioGroup.check(
                        R.id.radio_device_received
                );

                break;

            case "DIAGNOSING":

                radioGroup.check(
                        R.id.radio_diagnosing
                );

                break;

            case "REPAIRING":

                radioGroup.check(
                        R.id.radio_repairing
                );

                break;

            case "TESTING":

                radioGroup.check(
                        R.id.radio_testing
                );

                break;

            case "COMPLETED":

                radioGroup.check(
                        R.id.radio_completed
                );

                break;
        }
    }

    private String getSelectedStatus(
            RadioGroup radioGroup
    ) {

        int checkedId =
                radioGroup
                        .getCheckedRadioButtonId();

        if (checkedId
                == R.id.radio_device_received) {

            return "DEVICE_RECEIVED";
        }

        if (checkedId
                == R.id.radio_diagnosing) {

            return "DIAGNOSING";
        }

        if (checkedId
                == R.id.radio_repairing) {

            return "REPAIRING";
        }

        if (checkedId
                == R.id.radio_testing) {

            return "TESTING";
        }

        if (checkedId
                == R.id.radio_completed) {

            return "COMPLETED";
        }

        return null;
    }

    private void updateRepairStatus(
            String newStatus,
            String note,
            AlertDialog dialog,
            MaterialButton btnConfirm
    ) {

        String token =
                getToken();

        if (token == null) {
            return;
        }

        Map<String, Object> body =
                new HashMap<>();

        body.put(
                "status",
                newStatus
        );

        body.put(
                "note",
                note
        );

        btnConfirm.setEnabled(
                false
        );

        btnConfirm.setText(
                "Updating..."
        );

        apiService
                .updateAppointmentStatus(
                        token,
                        appointmentId,
                        body
                )
                .enqueue(
                        new Callback<
                                ApiResponse<Object>
                                >() {

                            @Override
                            public void onResponse(
                                    @NonNull
                                    Call<
                                            ApiResponse<Object>
                                            > call,

                                    @NonNull
                                    Response<
                                            ApiResponse<Object>
                                            > response
                            ) {

                                if (!isAdded()) {
                                    return;
                                }

                                if (response.isSuccessful()
                                        && response.body() != null
                                        && response.body()
                                        .isSuccess()) {

                                    dialog.dismiss();

                                    Toast.makeText(
                                            requireContext(),
                                            "Repair status updated",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    loadAppointmentDetail();

                                    loadRepairNotes();

                                } else {

                                    btnConfirm.setEnabled(
                                            true
                                    );

                                    btnConfirm.setText(
                                            "Update Status"
                                    );

                                    Toast.makeText(
                                            requireContext(),
                                            getResponseMessage(
                                                    response,
                                                    "Unable to update status"
                                            ),
                                            Toast.LENGTH_LONG
                                    ).show();
                                }
                            }

                            @Override
                            public void onFailure(
                                    @NonNull
                                    Call<
                                            ApiResponse<Object>
                                            > call,

                                    @NonNull
                                    Throwable t
                            ) {

                                if (!isAdded()) {
                                    return;
                                }

                                btnConfirm.setEnabled(
                                        true
                                );

                                btnConfirm.setText(
                                        "Update Status"
                                );

                                Toast.makeText(
                                        requireContext(),
                                        "Status update failed: "
                                                + safeError(t),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
    }

    private void loadRepairNotes() {

        String token =
                getToken();

        if (token == null
                || !isAppointmentIdValid()) {

            return;
        }

        apiService
                .getAppointmentHistory(
                        token,
                        appointmentId
                )
                .enqueue(
                        new Callback<
                                ApiResponse<
                                        List<Object>
                                        >
                                >() {

                            @Override
                            public void onResponse(
                                    @NonNull
                                    Call<
                                            ApiResponse<
                                                    List<Object>
                                                    >
                                            > call,

                                    @NonNull
                                    Response<
                                            ApiResponse<
                                                    List<Object>
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

                                    String latestNote =
                                            findLatestRepairNote(
                                                    response.body()
                                                            .getData()
                                            );

                                    tvRepairNotes.setText(
                                            latestNote
                                    );
                                }
                            }

                            @Override
                            public void onFailure(
                                    @NonNull
                                    Call<
                                            ApiResponse<
                                                    List<Object>
                                                    >
                                            > call,

                                    @NonNull
                                    Throwable t
                            ) {

                                // The rest of the repair screen
                                // can still work without notes.
                            }
                        }
                );
    }

    private String findLatestRepairNote(
            List<Object> history
    ) {

        if (history == null
                || history.isEmpty()) {

            return "No repair notes added yet.";
        }

        for (int i =
             history.size() - 1;
             i >= 0;
             i--) {

            Object item =
                    history.get(i);

            if (!(item instanceof Map)) {
                continue;
            }

            Map<?, ?> map =
                    (Map<?, ?>) item;

            Object noteValue =
                    map.get("note");

            if (noteValue == null) {
                continue;
            }

            String note =
                    String.valueOf(
                            noteValue
                    ).trim();

            if (note.isEmpty()) {
                continue;
            }

            if (note.startsWith(
                    "System "
            )) {

                continue;
            }

            if (note.equalsIgnoreCase(
                    "Status updated"
            )) {

                continue;
            }

            return note;
        }

        return "No repair notes added yet.";
    }

    private void showRepairNotesDialog() {

        if (currentDetail == null) {

            Toast.makeText(
                    requireContext(),
                    "Repair details are still loading",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String currentStatus =
                normalizeStatus(
                        currentDetail.getStatus()
                );

        if (!canSaveRepairNote(
                currentStatus
        )) {

            Toast.makeText(
                    requireContext(),
                    "Start the repair before adding notes",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        View dialogView =
                LayoutInflater
                        .from(
                                requireContext()
                        )
                        .inflate(
                                R.layout.dialog_repair_notes,
                                null,
                                false
                        );

        TextView tvNotesRepairId =
                dialogView.findViewById(
                        R.id.tv_notes_repair_id
                );

        TextView tvNotesDevice =
                dialogView.findViewById(
                        R.id.tv_notes_device
                );

        TextInputEditText etRepairNote =
                dialogView.findViewById(
                        R.id.et_repair_note
                );

        MaterialButton btnCancel =
                dialogView.findViewById(
                        R.id.btn_cancel_repair_note
                );

        MaterialButton btnSave =
                dialogView.findViewById(
                        R.id.btn_save_repair_note
                );

        tvNotesRepairId.setText(
                safeText(
                        currentDetail
                                .getAppointment_number(),
                        "Repair"
                )
        );

        String deviceName =
                currentDetail
                        .getDevice_full_name();

        tvNotesDevice.setText(
                safeText(
                        deviceName,
                        "Device"
                )
        );

        String existingNote =
                tvRepairNotes
                        .getText()
                        .toString()
                        .trim();

        if (!existingNote.equals(
                "No repair notes added yet."
        )) {

            etRepairNote.setText(
                    existingNote
            );
        }

        AlertDialog dialog =
                new AlertDialog.Builder(
                        requireContext()
                )
                        .setView(dialogView)
                        .create();

        btnCancel.setOnClickListener(
                v -> dialog.dismiss()
        );

        btnSave.setOnClickListener(
                v -> {

                    String note =
                            "";

                    if (etRepairNote.getText()
                            != null) {

                        note =
                                etRepairNote
                                        .getText()
                                        .toString()
                                        .trim();
                    }

                    if (note.isEmpty()) {

                        Toast.makeText(
                                requireContext(),
                                "Please enter a repair note",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    saveRepairNote(
                            currentStatus,
                            note,
                            dialog,
                            btnSave
                    );
                }
        );

        dialog.show();
    }

    private boolean canSaveRepairNote(
            String status
    ) {

        return "DEVICE_RECEIVED".equals(status)
                || "DIAGNOSING".equals(status)
                || "REPAIRING".equals(status)
                || "TESTING".equals(status)
                || "READY".equals(status);
    }

    private void saveRepairNote(
            String status,
            String note,
            AlertDialog dialog,
            MaterialButton btnSave
    ) {

        String token =
                getToken();

        if (token == null) {
            return;
        }

        /*
         * The current backend stores notes in
         * repair_status_history together with
         * a status update.
         *
         * Sending the current active status again
         * lets the technician persist a note.
         */

        Map<String, Object> body =
                new HashMap<>();

        body.put(
                "status",
                status
        );

        body.put(
                "note",
                note
        );

        btnSave.setEnabled(
                false
        );

        btnSave.setText(
                "Saving..."
        );

        apiService
                .updateAppointmentStatus(
                        token,
                        appointmentId,
                        body
                )
                .enqueue(
                        new Callback<
                                ApiResponse<Object>
                                >() {

                            @Override
                            public void onResponse(
                                    @NonNull
                                    Call<
                                            ApiResponse<Object>
                                            > call,

                                    @NonNull
                                    Response<
                                            ApiResponse<Object>
                                            > response
                            ) {

                                if (!isAdded()) {
                                    return;
                                }

                                if (response.isSuccessful()
                                        && response.body() != null
                                        && response.body()
                                        .isSuccess()) {

                                    dialog.dismiss();

                                    tvRepairNotes.setText(
                                            note
                                    );

                                    Toast.makeText(
                                            requireContext(),
                                            "Repair note saved",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                } else {

                                    btnSave.setEnabled(
                                            true
                                    );

                                    btnSave.setText(
                                            "Save Note"
                                    );

                                    Toast.makeText(
                                            requireContext(),
                                            getResponseMessage(
                                                    response,
                                                    "Unable to save repair note"
                                            ),
                                            Toast.LENGTH_LONG
                                    ).show();
                                }
                            }

                            @Override
                            public void onFailure(
                                    @NonNull
                                    Call<
                                            ApiResponse<Object>
                                            > call,

                                    @NonNull
                                    Throwable t
                            ) {

                                if (!isAdded()) {
                                    return;
                                }

                                btnSave.setEnabled(
                                        true
                                );

                                btnSave.setText(
                                        "Save Note"
                                );

                                Toast.makeText(
                                        requireContext(),
                                        "Unable to save note: "
                                                + safeError(t),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
    }

    private void loadRepairImages() {

        String token =
                getToken();

        if (token == null
                || !isAppointmentIdValid()) {

            return;
        }

        apiService
                .getAppointmentImages(
                        token,
                        appointmentId
                )
                .enqueue(
                        new Callback<
                                ApiResponse<
                                        List<Object>
                                        >
                                >() {

                            @Override
                            public void onResponse(
                                    @NonNull
                                    Call<
                                            ApiResponse<
                                                    List<Object>
                                                    >
                                            > call,

                                    @NonNull
                                    Response<
                                            ApiResponse<
                                                    List<Object>
                                                    >
                                            > response
                            ) {

                                if (!isAdded()) {
                                    return;
                                }

                                repairImageUrls.clear();

                                if (response.isSuccessful()
                                        && response.body() != null
                                        && response.body()
                                        .isSuccess()
                                        && response.body()
                                        .getData() != null) {

                                    for (Object item :
                                            response.body()
                                                    .getData()) {

                                        if (!(item instanceof Map)) {
                                            continue;
                                        }

                                        Map<?, ?> map =
                                                (Map<?, ?>) item;

                                        Object url =
                                                map.get(
                                                        "image_url"
                                                );

                                        if (url != null
                                                && !String.valueOf(url)
                                                .trim()
                                                .isEmpty()) {

                                            repairImageUrls.add(
                                                    String.valueOf(
                                                            url
                                                    )
                                            );
                                        }
                                    }
                                }

                                repairImageAdapter
                                        .notifyDataSetChanged();
                            }

                            @Override
                            public void onFailure(
                                    @NonNull
                                    Call<
                                            ApiResponse<
                                                    List<Object>
                                                    >
                                            > call,

                                    @NonNull
                                    Throwable t
                            ) {

                                // Images are optional.
                            }
                        }
                );
    }

    private void showAddImageDialog() {

        EditText input =
                new EditText(
                        requireContext()
                );

        input.setHint(
                "https://example.com/repair-image.jpg"
        );

        input.setInputType(
                InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_VARIATION_URI
        );

        int padding =
                (int) (
                        20
                                * getResources()
                                .getDisplayMetrics()
                                .density
                );

        input.setPadding(
                padding,
                padding,
                padding,
                padding
        );

        new AlertDialog.Builder(
                requireContext()
        )
                .setTitle(
                        "Add Repair Image"
                )
                .setMessage(
                        "Enter the repair image URL"
                )
                .setView(
                        input
                )
                .setNegativeButton(
                        "Cancel",
                        null
                )
                .setPositiveButton(
                        "Add",
                        (dialog, which) -> {

                            String url =
                                    input.getText()
                                            .toString()
                                            .trim();

                            if (url.isEmpty()) {

                                Toast.makeText(
                                        requireContext(),
                                        "Image URL is required",
                                        Toast.LENGTH_SHORT
                                ).show();

                                return;
                            }

                            addRepairImage(
                                    url
                            );
                        }
                )
                .show();
    }

    private void addRepairImage(
            String imageUrl
    ) {

        String token =
                getToken();

        if (token == null) {
            return;
        }

        Map<String, Object> body =
                new HashMap<>();

        body.put(
                "image_url",
                imageUrl
        );

        body.put(
                "image_type",
                "REPAIR"
        );

        apiService
                .addAppointmentImage(
                        token,
                        appointmentId,
                        body
                )
                .enqueue(
                        new Callback<
                                ApiResponse<Object>
                                >() {

                            @Override
                            public void onResponse(
                                    @NonNull
                                    Call<
                                            ApiResponse<Object>
                                            > call,

                                    @NonNull
                                    Response<
                                            ApiResponse<Object>
                                            > response
                            ) {

                                if (!isAdded()) {
                                    return;
                                }

                                if (response.isSuccessful()
                                        && response.body() != null
                                        && response.body()
                                        .isSuccess()) {

                                    Toast.makeText(
                                            requireContext(),
                                            "Repair image added",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    loadRepairImages();

                                } else {

                                    Toast.makeText(
                                            requireContext(),
                                            getResponseMessage(
                                                    response,
                                                    "Unable to add image"
                                            ),
                                            Toast.LENGTH_LONG
                                    ).show();
                                }
                            }

                            @Override
                            public void onFailure(
                                    @NonNull
                                    Call<
                                            ApiResponse<Object>
                                            > call,

                                    @NonNull
                                    Throwable t
                            ) {

                                if (!isAdded()) {
                                    return;
                                }

                                Toast.makeText(
                                        requireContext(),
                                        "Unable to add image: "
                                                + safeError(t),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
    }

    private void openRepairHistory() {

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

    private String getToken() {

        String token =
                sessionManager
                        .getBearerToken();

        if (token == null
                || token.trim()
                .isEmpty()) {

            Toast.makeText(
                    requireContext(),
                    "Please sign in again",
                    Toast.LENGTH_SHORT
            ).show();

            return null;
        }

        return token;
    }

    private boolean isAppointmentIdValid() {

        return appointmentId != null
                && !appointmentId
                .trim()
                .isEmpty();
    }

    private String normalizeStatus(
            String status
    ) {

        if (status == null) {
            return "";
        }

        return status
                .trim()
                .toUpperCase(
                        Locale.US
                );
    }

    private String formatStatus(
            String status
    ) {

        if (status == null
                || status.trim()
                .isEmpty()) {

            return "UNKNOWN";
        }

        return status
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
                || value.trim()
                .isEmpty()) {

            return fallback;
        }

        return value.trim();
    }

    private String safeError(
            Throwable throwable
    ) {

        if (throwable == null
                || throwable.getMessage() == null
                || throwable.getMessage()
                .trim()
                .isEmpty()) {

            return "Unknown error";
        }

        return throwable
                .getMessage();
    }

    private String getResponseMessage(
            Response<ApiResponse<Object>> response,
            String fallback
    ) {

        if (response.body() != null
                && response.body()
                .getMessage() != null
                && !response.body()
                .getMessage()
                .trim()
                .isEmpty()) {

            return response.body()
                    .getMessage();
        }

        return fallback;
    }


    // ==========================================
    // SIMPLE REPAIR IMAGE URL ADAPTER
    // ==========================================

    private class RepairImageUrlAdapter
            extends RecyclerView.Adapter<
            RepairImageUrlAdapter.ImageViewHolder> {

        private final List<String> urls;

        RepairImageUrlAdapter(
                List<String> urls
        ) {

            this.urls =
                    urls;
        }

        @NonNull
        @Override
        public ImageViewHolder onCreateViewHolder(
                @NonNull ViewGroup parent,
                int viewType
        ) {

            TextView textView =
                    (TextView)
                            LayoutInflater
                                    .from(
                                            parent.getContext()
                                    )
                                    .inflate(
                                            android.R.layout
                                                    .simple_list_item_1,
                                            parent,
                                            false
                                    );

            return new ImageViewHolder(
                    textView
            );
        }

        @Override
        public void onBindViewHolder(
                @NonNull ImageViewHolder holder,
                int position
        ) {

            String url =
                    urls.get(
                            position
                    );

            holder.textView.setText(
                    "Image "
                            + (position + 1)
                            + "\n"
                            + url
            );

            holder.itemView.setOnClickListener(
                    v -> {

                        try {

                            Intent intent =
                                    new Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse(
                                                    url
                                            )
                                    );

                            startActivity(
                                    intent
                            );

                        } catch (Exception e) {

                            Toast.makeText(
                                    requireContext(),
                                    "Unable to open image",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
            );
        }

        @Override
        public int getItemCount() {

            return urls.size();
        }

        class ImageViewHolder
                extends RecyclerView.ViewHolder {

            TextView textView;

            ImageViewHolder(
                    @NonNull View itemView
            ) {

                super(itemView);

                textView =
                        (TextView) itemView;
            }
        }
    }
}