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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.mad.techfix.R;
import com.mad.techfix.models.AppointmentDetail;
import com.mad.techfix.ui.history.RepairHistoryDetailFragment;
import com.mad.techfix.viewmodel.TechnicianRepairDetailViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TechnicianRepairDetailFragment
        extends Fragment {

    private static final String ARG_APPOINTMENT_ID =
            "appointment_id";


    private String appointmentId;


    private AppointmentDetail currentDetail;


    private TechnicianRepairDetailViewModel viewModel;


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


    public static TechnicianRepairDetailFragment
    newInstance(
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


        readArguments();

        bindViews(
                view
        );

        setupRecyclerView();

        setupViewModel();

        observeViewModel();

        setupListeners();


        if (!isAppointmentIdValid()) {

            Toast.makeText(
                    requireContext(),
                    "Repair ID is unavailable",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        viewModel.loadRepairData(
                appointmentId
        );
    }


    private void readArguments() {

        Bundle args =
                getArguments();


        if (args != null) {

            appointmentId =
                    args.getString(
                            ARG_APPOINTMENT_ID
                    );
        }
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


        recyclerRepairImages
                .setLayoutManager(
                        new LinearLayoutManager(
                                requireContext()
                        )
                );


        recyclerRepairImages
                .setAdapter(
                        repairImageAdapter
                );


        recyclerRepairImages
                .setNestedScrollingEnabled(
                        false
                );
    }


    private void setupViewModel() {

        viewModel =
                new ViewModelProvider(this)
                        .get(
                                TechnicianRepairDetailViewModel.class
                        );
    }


    private void observeViewModel() {

        viewModel
                .getRepairDetail()
                .observe(
                        getViewLifecycleOwner(),
                        detail -> {

                            if (detail == null) {

                                return;
                            }


                            currentDetail =
                                    detail;


                            displayRepairDetail(
                                    detail
                            );
                        }
                );


        viewModel
                .getRepairNote()
                .observe(
                        getViewLifecycleOwner(),
                        note -> {

                            tvRepairNotes.setText(
                                    safeText(
                                            note,
                                            "No repair notes added yet."
                                    )
                            );
                        }
                );


        viewModel
                .getRepairImages()
                .observe(
                        getViewLifecycleOwner(),
                        images -> {

                            repairImageUrls.clear();


                            if (images != null) {

                                repairImageUrls.addAll(
                                        images
                                );
                            }


                            repairImageAdapter
                                    .notifyDataSetChanged();
                        }
                );


        viewModel
                .getIsLoading()
                .observe(
                        getViewLifecycleOwner(),
                        loading -> {

                            boolean active =
                                    loading != null
                                            && loading;


                            if (active) {

                                btnUpdateRepairStatus
                                        .setEnabled(
                                                false
                                        );

                            } else if (currentDetail
                                    != null) {

                                updateActionButtons(
                                        normalizeStatus(
                                                currentDetail
                                                        .getStatus()
                                        )
                                );
                            }
                        }
                );


        viewModel
                .getActionLoading()
                .observe(
                        getViewLifecycleOwner(),
                        loading -> {

                            boolean active =
                                    loading != null
                                            && loading;


                            if (active) {

                                btnUpdateRepairStatus
                                        .setEnabled(false);

                                btnEditRepairNotes
                                        .setEnabled(false);

                                btnAddRepairImage
                                        .setEnabled(false);

                            } else if (currentDetail
                                    != null) {

                                updateActionButtons(
                                        normalizeStatus(
                                                currentDetail
                                                        .getStatus()
                                        )
                                );
                            }
                        }
                );


        viewModel
                .getSuccessMessage()
                .observe(
                        getViewLifecycleOwner(),
                        message -> {

                            if (message == null
                                    || message.trim()
                                    .isEmpty()) {

                                return;
                            }


                            Toast.makeText(
                                    requireContext(),
                                    message,
                                    Toast.LENGTH_SHORT
                            ).show();


                            viewModel
                                    .clearSuccessMessage();
                        }
                );


        viewModel
                .getErrorMessage()
                .observe(
                        getViewLifecycleOwner(),
                        message -> {

                            if (message == null
                                    || message.trim()
                                    .isEmpty()) {

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


    private void setupListeners() {

        btnBack.setOnClickListener(
                view ->
                        getParentFragmentManager()
                                .popBackStack()
        );


        btnUpdateRepairStatus
                .setOnClickListener(
                        view ->
                                showStatusDialog()
                );


        btnEditRepairNotes
                .setOnClickListener(
                        view ->
                                showRepairNotesDialog()
                );


        btnAddRepairImage
                .setOnClickListener(
                        view ->
                                showAddImageDialog()
                );


        btnViewRepairHistory
                .setOnClickListener(
                        view ->
                                openRepairHistory()
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
                formatStatus(
                        status
                )
        );


        String customerName =
                safeText(
                        detail.getCustomer_full_name(),
                        ""
                );


        if (customerName.isEmpty()) {

            if (detail.getCustomer_id() != null
                    && !detail.getCustomer_id()
                    .trim()
                    .isEmpty()) {

                customerName =
                        "Customer "
                                + detail
                                .getCustomer_id();

            } else {

                customerName =
                        "Customer information unavailable";
            }
        }


        tvCustomerName.setText(
                customerName
        );


        tvCustomerPhone.setText(
                "Phone: Not available"
        );


        tvCustomerEmail.setText(
                "Email: Not available"
        );


        String deviceName =
                safeText(
                        detail.getDevice_full_name(),
                        ""
                );


        if (deviceName.isEmpty()) {

            deviceName =
                    "Device information unavailable";
        }


        tvDeviceName.setText(
                deviceName
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
                        "Repair service unavailable"
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
                            + detail
                            .getBranch_city()
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
                getStageIndex(
                        status
                );


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


        /*
         * READY is stage 4.
         * COMPLETED is stage 5.
         *
         * This means READY correctly shows
         * TESTING as finished but COMPLETED
         * as still pending.
         */
        setStageText(
                tvStatusCompleted,
                "COMPLETED",
                5,
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

            prefix =
                    "✓ ";

        } else if (currentStage
                == stageIndex) {

            prefix =
                    "● ";

        } else {

            prefix =
                    "○ ";
        }


        textView.setText(
                prefix
                        + stageName
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
                "COMPLETED".equals(
                        status
                )
                        || "CANCELLED".equals(
                        status
                );


        btnUpdateRepairStatus
                .setEnabled(
                        !finished
                );


        btnEditRepairNotes
                .setEnabled(
                        !finished
                                && canSaveRepairNote(
                                status
                        )
                );


        btnAddRepairImage
                .setEnabled(
                        !finished
                );


        if (finished) {

            btnUpdateRepairStatus
                    .setText(
                            "Repair Finished"
                    );

        } else {

            btnUpdateRepairStatus
                    .setText(
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
                        currentDetail
                                .getStatus()
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
                        .setView(
                                dialogView
                        )
                        .create();


        btnCancel.setOnClickListener(
                view ->
                        dialog.dismiss()
        );


        btnConfirm.setOnClickListener(
                view -> {

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


                    viewModel
                            .updateRepairStatus(
                                    appointmentId,
                                    selectedStatus,
                                    note
                            );


                    dialog.dismiss();
                }
        );


        dialog.show();
    }


    private void selectCurrentStatus(
            RadioGroup group,
            String status
    ) {

        switch (status) {

            case "DEVICE_RECEIVED":

                group.check(
                        R.id.radio_device_received
                );

                break;


            case "DIAGNOSING":

                group.check(
                        R.id.radio_diagnosing
                );

                break;


            case "REPAIRING":

                group.check(
                        R.id.radio_repairing
                );

                break;


            case "TESTING":

                group.check(
                        R.id.radio_testing
                );

                break;


            case "COMPLETED":

                group.check(
                        R.id.radio_completed
                );

                break;
        }
    }


    @Nullable
    private String getSelectedStatus(
            RadioGroup group
    ) {

        int id =
                group.getCheckedRadioButtonId();


        if (id == R.id.radio_device_received) {

            return "DEVICE_RECEIVED";
        }


        if (id == R.id.radio_diagnosing) {

            return "DIAGNOSING";
        }


        if (id == R.id.radio_repairing) {

            return "REPAIRING";
        }


        if (id == R.id.radio_testing) {

            return "TESTING";
        }


        if (id == R.id.radio_completed) {

            return "COMPLETED";
        }


        return null;
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
                        currentDetail
                                .getStatus()
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


        tvNotesDevice.setText(
                safeText(
                        currentDetail
                                .getDevice_full_name(),
                        "Device"
                )
        );


        String existingNote =
                viewModel
                        .getRepairNote()
                        .getValue();


        if (existingNote != null
                && !existingNote.equals(
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
                        .setView(
                                dialogView
                        )
                        .create();


        btnCancel.setOnClickListener(
                view ->
                        dialog.dismiss()
        );


        btnSave.setOnClickListener(
                view -> {

                    String note =
                            "";


                    if (etRepairNote
                            .getText() != null) {

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


                    viewModel
                            .saveRepairNote(
                                    appointmentId,
                                    currentStatus,
                                    note
                            );


                    dialog.dismiss();
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
                                    input
                                            .getText()
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


                            viewModel
                                    .addRepairImage(
                                            appointmentId,
                                            url
                                    );
                        }
                )
                .show();
    }


    private void openRepairHistory() {

        if (!isAppointmentIdValid()) {

            return;
        }


        RepairHistoryDetailFragment fragment =
                RepairHistoryDetailFragment
                        .newInstance(
                                appointmentId
                        );


        getParentFragmentManager()
                .beginTransaction()
                .replace(
                        R.id.technician_fragment_container,
                        fragment
                )
                .addToBackStack(
                        null
                )
                .commit();
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
                || value.trim()
                .isEmpty()) {

            return fallback;
        }


        return value.trim();
    }


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


            holder.textView
                    .setText(
                            "Image "
                                    + (position + 1)
                                    + "\n"
                                    + url
                    );


            holder.itemView
                    .setOnClickListener(
                            view -> {

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

                                } catch (Exception exception) {

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

            final TextView textView;


            ImageViewHolder(
                    @NonNull TextView itemView
            ) {

                super(itemView);

                textView =
                        itemView;
            }
        }
    }
}