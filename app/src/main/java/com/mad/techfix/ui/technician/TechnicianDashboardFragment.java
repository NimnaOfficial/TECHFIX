package com.mad.techfix.ui.technician;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.mad.techfix.R;
import com.mad.techfix.data.SessionManager;
import com.mad.techfix.models.ApiResponse;
import com.mad.techfix.models.Appointment;
import com.mad.techfix.models.AppointmentDetail;
import com.mad.techfix.network.ApiService;
import com.mad.techfix.network.RetrofitClient;
import com.mad.techfix.ui.history.RepairHistoryFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TechnicianDashboardFragment extends Fragment {

    private TextView tvTechnicianWelcome;
    private TextView tvTechnicianName;
    private TextView tvTechnicianSpecialization;
    private TextView tvTechnicianStatus;

    private TextView tvAssignedRepairsCount;
    private TextView tvInProgressCount;
    private TextView tvCompletedRepairsCount;

    private TextView btnViewAllAssigned;

    private ImageButton btnRefreshDashboard;

    private MaterialButton btnAssignedRepairs;
    private MaterialButton btnRepairHistory;

    private RecyclerView recyclerDashboardRepairs;

    private View cardNoCurrentRepairs;

    private AssignedRepairAdapter repairAdapter;

    private ApiService apiService;
    private SessionManager sessionManager;

    private final List<AppointmentDetail> previewRepairs =
            new ArrayList<>();

    public TechnicianDashboardFragment() {
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
                R.layout.fragment_technician_dashboard,
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

        bindViews(view);

        setupTechnicianInformation();

        setupRecyclerView();

        setupListeners();

        loadDashboard();
    }

    private void bindViews(
            View view
    ) {

        tvTechnicianWelcome =
                view.findViewById(
                        R.id.tv_technician_welcome
                );

        tvTechnicianName =
                view.findViewById(
                        R.id.tv_technician_name
                );

        tvTechnicianSpecialization =
                view.findViewById(
                        R.id.tv_technician_specialization
                );

        tvTechnicianStatus =
                view.findViewById(
                        R.id.tv_technician_status
                );

        tvAssignedRepairsCount =
                view.findViewById(
                        R.id.tv_assigned_repairs_count
                );

        tvInProgressCount =
                view.findViewById(
                        R.id.tv_in_progress_count
                );

        tvCompletedRepairsCount =
                view.findViewById(
                        R.id.tv_completed_repairs_count
                );

        btnViewAllAssigned =
                view.findViewById(
                        R.id.btn_view_all_assigned
                );

        btnRefreshDashboard =
                view.findViewById(
                        R.id.btn_refresh_technician_dashboard
                );

        btnAssignedRepairs =
                view.findViewById(
                        R.id.btn_dashboard_assigned_repairs
                );

        btnRepairHistory =
                view.findViewById(
                        R.id.btn_dashboard_repair_history
                );

        recyclerDashboardRepairs =
                view.findViewById(
                        R.id.recycler_dashboard_repairs
                );

        cardNoCurrentRepairs =
                view.findViewById(
                        R.id.card_no_current_repairs
                );
    }

    private void setupTechnicianInformation() {

        String technicianName =
                sessionManager.getUserName();

        if (technicianName == null
                || technicianName.trim().isEmpty()
                || technicianName.equalsIgnoreCase("User")) {

            technicianName =
                    "TECHFIX Technician";
        }

        tvTechnicianName.setText(
                technicianName
        );

        tvTechnicianWelcome.setText(
                "Welcome back, " + technicianName
        );

        tvTechnicianSpecialization.setText(
                "Device Repair Technician"
        );

        tvTechnicianStatus.setText(
                "AVAILABLE"
        );
    }

    private void setupRecyclerView() {

        repairAdapter =
                new AssignedRepairAdapter(
                        repair -> {

                            if (repair == null
                                    || repair.getId() == null
                                    || repair.getId()
                                    .trim()
                                    .isEmpty()) {

                                Toast.makeText(
                                        requireContext(),
                                        "Repair ID is unavailable",
                                        Toast.LENGTH_SHORT
                                ).show();

                                return;
                            }

                            openRepairDetail(
                                    repair.getId()
                            );
                        }
                );

        recyclerDashboardRepairs.setLayoutManager(
                new LinearLayoutManager(
                        requireContext()
                )
        );

        recyclerDashboardRepairs.setAdapter(
                repairAdapter
        );

        recyclerDashboardRepairs.setNestedScrollingEnabled(
                false
        );
    }

    private void setupListeners() {

        btnRefreshDashboard.setOnClickListener(
                v -> loadDashboard()
        );

        btnViewAllAssigned.setOnClickListener(
                v -> openAssignedRepairs()
        );

        btnAssignedRepairs.setOnClickListener(
                v -> openAssignedRepairs()
        );

        btnRepairHistory.setOnClickListener(
                v -> openRepairHistory()
        );
    }

    private void loadDashboard() {

        String token =
                sessionManager.getBearerToken();

        if (token == null
                || token.trim().isEmpty()) {

            Toast.makeText(
                    requireContext(),
                    "Please sign in again",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        btnRefreshDashboard.setEnabled(
                false
        );

        apiService
                .getTechnicianAppointments(token)
                .enqueue(
                        new Callback<
                                ApiResponse<
                                        List<Appointment>
                                        >
                                >() {

                            @Override
                            public void onResponse(
                                    @NonNull
                                    Call<
                                            ApiResponse<
                                                    List<Appointment>
                                                    >
                                            > call,

                                    @NonNull
                                    Response<
                                            ApiResponse<
                                                    List<Appointment>
                                                    >
                                            > response
                            ) {

                                if (!isAdded()) {
                                    return;
                                }

                                btnRefreshDashboard.setEnabled(
                                        true
                                );

                                if (response.isSuccessful()
                                        && response.body() != null
                                        && response.body()
                                        .isSuccess()) {

                                    List<Appointment> appointments =
                                            response.body()
                                                    .getData();

                                    if (appointments == null) {

                                        appointments =
                                                new ArrayList<>();
                                    }

                                    updateDashboardCounts(
                                            appointments
                                    );

                                    loadRepairPreviews(
                                            appointments
                                    );

                                } else {

                                    clearDashboard();

                                    Toast.makeText(
                                            requireContext(),
                                            "Unable to load technician dashboard",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                }
                            }

                            @Override
                            public void onFailure(
                                    @NonNull
                                    Call<
                                            ApiResponse<
                                                    List<Appointment>
                                                    >
                                            > call,

                                    @NonNull
                                    Throwable t
                            ) {

                                if (!isAdded()) {
                                    return;
                                }

                                btnRefreshDashboard.setEnabled(
                                        true
                                );

                                clearDashboard();

                                String message =
                                        "Unable to load dashboard";

                                if (t.getMessage() != null
                                        && !t.getMessage()
                                        .trim()
                                        .isEmpty()) {

                                    message +=
                                            ": "
                                                    + t.getMessage();
                                }

                                Toast.makeText(
                                        requireContext(),
                                        message,
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
    }

    private void updateDashboardCounts(
            List<Appointment> appointments
    ) {

        int assignedCount = 0;
        int inProgressCount = 0;
        int completedCount = 0;
        int activeCount = 0;

        for (Appointment appointment :
                appointments) {

            if (appointment == null) {
                continue;
            }

            String status =
                    normalizeStatus(
                            appointment.getStatus()
                    );

            if (status.equals("ASSIGNED")) {

                assignedCount++;
                activeCount++;

            } else if (isInProgressStatus(status)) {

                inProgressCount++;
                activeCount++;

            } else if (status.equals("READY")) {

                inProgressCount++;
                activeCount++;

            } else if (status.equals("COMPLETED")) {

                completedCount++;
            }
        }

        tvAssignedRepairsCount.setText(
                String.valueOf(
                        assignedCount
                )
        );

        tvInProgressCount.setText(
                String.valueOf(
                        inProgressCount
                )
        );

        tvCompletedRepairsCount.setText(
                String.valueOf(
                        completedCount
                )
        );

        if (activeCount > 0) {

            tvTechnicianStatus.setText(
                    "BUSY"
            );

        } else {

            tvTechnicianStatus.setText(
                    "AVAILABLE"
            );
        }
    }

    private void loadRepairPreviews(
            List<Appointment> appointments
    ) {

        previewRepairs.clear();

        repairAdapter.setRepairs(
                previewRepairs
        );

        List<Appointment> activeAppointments =
                new ArrayList<>();

        for (Appointment appointment :
                appointments) {

            if (appointment == null) {
                continue;
            }

            String status =
                    normalizeStatus(
                            appointment.getStatus()
                    );

            if (isActiveRepairStatus(status)) {

                activeAppointments.add(
                        appointment
                );
            }
        }

        if (activeAppointments.isEmpty()) {

            showEmptyState(true);

            return;
        }

        showEmptyState(false);

        int limit =
                Math.min(
                        activeAppointments.size(),
                        3
                );

        String token =
                sessionManager.getBearerToken();

        for (int i = 0; i < limit; i++) {

            Appointment appointment =
                    activeAppointments.get(i);

            if (appointment.getId() == null
                    || appointment.getId()
                    .trim()
                    .isEmpty()) {

                continue;
            }

            loadSingleRepairDetail(
                    token,
                    appointment
            );
        }
    }

    private void loadSingleRepairDetail(
            String token,
            Appointment appointment
    ) {

        apiService
                .getAppointmentDetail(
                        token,
                        appointment.getId()
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

                                    AppointmentDetail detail =
                                            response.body()
                                                    .getData();

                                    fillMissingAppointmentValues(
                                            detail,
                                            appointment
                                    );

                                    previewRepairs.add(
                                            detail
                                    );

                                    repairAdapter.setRepairs(
                                            previewRepairs
                                    );

                                    showEmptyState(
                                            previewRepairs.isEmpty()
                                    );

                                } else {

                                    addBasicPreview(
                                            appointment
                                    );
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

                                addBasicPreview(
                                        appointment
                                );
                            }
                        }
                );
    }

    private void addBasicPreview(
            Appointment appointment
    ) {

        AppointmentDetail detail =
                new AppointmentDetail();

        fillMissingAppointmentValues(
                detail,
                appointment
        );

        previewRepairs.add(
                detail
        );

        repairAdapter.setRepairs(
                previewRepairs
        );

        showEmptyState(
                false
        );
    }

    private void fillMissingAppointmentValues(
            AppointmentDetail detail,
            Appointment appointment
    ) {

        if (detail.getId() == null) {

            detail.setId(
                    appointment.getId()
            );
        }

        if (detail.getAppointment_number() == null) {

            detail.setAppointment_number(
                    appointment.getAppointment_number()
            );
        }

        if (detail.getStatus() == null) {

            detail.setStatus(
                    appointment.getStatus()
            );
        }

        if (detail.getRequested_date() == null) {

            detail.setRequested_date(
                    appointment.getRequested_date()
            );
        }

        if (detail.getRequested_time() == null) {

            detail.setRequested_time(
                    appointment.getRequested_time()
            );
        }

        if (detail.getCustomer_id() == null) {

            detail.setCustomer_id(
                    appointment.getCustomer_id()
            );
        }

        if (detail.getDevice_id() == null) {

            detail.setDevice_id(
                    appointment.getDevice_id()
            );
        }

        if (detail.getService_id() == null) {

            detail.setService_id(
                    appointment.getService_id()
            );
        }

        if (detail.getBranch_id() == null) {

            detail.setBranch_id(
                    appointment.getBranch_id()
            );
        }

        if (detail.getTechnician_id() == null) {

            detail.setTechnician_id(
                    appointment.getTechnician_id()
            );
        }
    }

    private boolean isInProgressStatus(
            String status
    ) {

        return status.equals(
                "DEVICE_RECEIVED"
        )
                || status.equals(
                "DIAGNOSING"
        )
                || status.equals(
                "REPAIRING"
        )
                || status.equals(
                "TESTING"
        );
    }

    private boolean isActiveRepairStatus(
            String status
    ) {

        return status.equals(
                "ASSIGNED"
        )
                || status.equals(
                "DEVICE_RECEIVED"
        )
                || status.equals(
                "DIAGNOSING"
        )
                || status.equals(
                "REPAIRING"
        )
                || status.equals(
                "TESTING"
        )
                || status.equals(
                "READY"
        );
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

    private void showEmptyState(
            boolean empty
    ) {

        if (empty) {

            recyclerDashboardRepairs.setVisibility(
                    View.GONE
            );

            cardNoCurrentRepairs.setVisibility(
                    View.VISIBLE
            );

        } else {

            recyclerDashboardRepairs.setVisibility(
                    View.VISIBLE
            );

            cardNoCurrentRepairs.setVisibility(
                    View.GONE
            );
        }
    }

    private void clearDashboard() {

        tvAssignedRepairsCount.setText(
                "0"
        );

        tvInProgressCount.setText(
                "0"
        );

        tvCompletedRepairsCount.setText(
                "0"
        );

        tvTechnicianStatus.setText(
                "AVAILABLE"
        );

        previewRepairs.clear();

        repairAdapter.setRepairs(
                previewRepairs
        );

        showEmptyState(
                true
        );
    }

    private void openRepairDetail(
            String appointmentId
    ) {

        TechnicianRepairDetailFragment fragment =
                TechnicianRepairDetailFragment
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

    private void openAssignedRepairs() {

        AssignedRepairsFragment fragment =
                new AssignedRepairsFragment();

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

    private void openRepairHistory() {

        RepairHistoryFragment fragment =
                new RepairHistoryFragment();

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
}