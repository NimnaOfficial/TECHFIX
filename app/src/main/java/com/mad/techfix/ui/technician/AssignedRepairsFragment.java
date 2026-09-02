package com.mad.techfix.ui.technician;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.mad.techfix.R;
import com.mad.techfix.data.SessionManager;
import com.mad.techfix.models.ApiResponse;
import com.mad.techfix.models.Appointment;
import com.mad.techfix.models.AppointmentDetail;
import com.mad.techfix.network.ApiService;
import com.mad.techfix.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AssignedRepairsFragment extends Fragment {

    private RecyclerView recyclerAssignedRepairs;
    private ProgressBar progressAssignedRepairs;
    private View layoutEmptyAssignedRepairs;
    private TextView tvAssignedRepairsTotal;
    private ImageButton btnRefreshAssignedRepairs;

    private Chip chipAll;
    private Chip chipDeviceReceived;
    private Chip chipDiagnosing;
    private Chip chipRepairing;
    private Chip chipTesting;
    private Chip chipCompleted;

    private AssignedRepairAdapter repairAdapter;

    private ApiService apiService;
    private SessionManager sessionManager;

    private final List<AppointmentDetail> allRepairs =
            new ArrayList<>();

    private final List<AppointmentDetail> filteredRepairs =
            new ArrayList<>();

    private String currentFilter = "ALL";

    private int pendingDetailRequests = 0;

    public AssignedRepairsFragment() {
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
                R.layout.fragment_assigned_repairs,
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
        setupRecyclerView();
        setupFilters();
        setupListeners();
        loadAssignedRepairs();
    }

    private void bindViews(
            View view
    ) {

        recyclerAssignedRepairs =
                view.findViewById(
                        R.id.recycler_assigned_repairs
                );

        progressAssignedRepairs =
                view.findViewById(
                        R.id.progress_assigned_repairs
                );

        layoutEmptyAssignedRepairs =
                view.findViewById(
                        R.id.layout_empty_assigned_repairs
                );

        tvAssignedRepairsTotal =
                view.findViewById(
                        R.id.tv_assigned_repairs_total
                );

        btnRefreshAssignedRepairs =
                view.findViewById(
                        R.id.btn_refresh_assigned_repairs
                );

        chipAll =
                view.findViewById(
                        R.id.chip_repairs_all
                );

        chipDeviceReceived =
                view.findViewById(
                        R.id.chip_device_received
                );

        chipDiagnosing =
                view.findViewById(
                        R.id.chip_diagnosing
                );

        chipRepairing =
                view.findViewById(
                        R.id.chip_repairing
                );

        chipTesting =
                view.findViewById(
                        R.id.chip_testing
                );

        chipCompleted =
                view.findViewById(
                        R.id.chip_repairs_completed
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

        recyclerAssignedRepairs.setLayoutManager(
                new LinearLayoutManager(
                        requireContext()
                )
        );

        recyclerAssignedRepairs.setAdapter(
                repairAdapter
        );
    }

    private void setupFilters() {

        chipAll.setOnClickListener(
                v -> applyFilter(
                        "ALL"
                )
        );

        chipDeviceReceived.setOnClickListener(
                v -> applyFilter(
                        "DEVICE_RECEIVED"
                )
        );

        chipDiagnosing.setOnClickListener(
                v -> applyFilter(
                        "DIAGNOSING"
                )
        );

        chipRepairing.setOnClickListener(
                v -> applyFilter(
                        "REPAIRING"
                )
        );

        chipTesting.setOnClickListener(
                v -> applyFilter(
                        "TESTING"
                )
        );

        chipCompleted.setOnClickListener(
                v -> applyFilter(
                        "COMPLETED"
                )
        );
    }

    private void setupListeners() {

        btnRefreshAssignedRepairs
                .setOnClickListener(
                        v -> loadAssignedRepairs()
                );
    }

    private void loadAssignedRepairs() {

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

        showLoading(true);

        btnRefreshAssignedRepairs
                .setEnabled(false);

        allRepairs.clear();
        filteredRepairs.clear();

        repairAdapter.setRepairs(
                filteredRepairs
        );

        apiService
                .getTechnicianAppointments(
                        token
                )
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

                                if (response.isSuccessful()
                                        && response.body() != null
                                        && response.body()
                                        .isSuccess()) {

                                    List<Appointment> appointments =
                                            response.body()
                                                    .getData();

                                    if (appointments == null
                                            || appointments.isEmpty()) {

                                        pendingDetailRequests = 0;

                                        finishLoading();

                                        return;
                                    }

                                    loadRepairDetails(
                                            token,
                                            appointments
                                    );

                                } else {

                                    pendingDetailRequests = 0;

                                    finishLoading();

                                    Toast.makeText(
                                            requireContext(),
                                            "Unable to load assigned repairs",
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

                                pendingDetailRequests = 0;

                                finishLoading();

                                String message =
                                        "Unable to load assigned repairs";

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

    private void loadRepairDetails(
            String token,
            List<Appointment> appointments
    ) {

        pendingDetailRequests =
                appointments.size();

        for (Appointment appointment :
                appointments) {

            if (appointment == null
                    || appointment.getId() == null
                    || appointment.getId()
                    .trim()
                    .isEmpty()) {

                pendingDetailRequests--;

                checkDetailLoadingComplete();

                continue;
            }

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

                                        fillMissingValues(
                                                detail,
                                                appointment
                                        );

                                        allRepairs.add(
                                                detail
                                        );

                                    } else {

                                        addBasicRepair(
                                                appointment
                                        );
                                    }

                                    pendingDetailRequests--;

                                    checkDetailLoadingComplete();
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

                                    addBasicRepair(
                                            appointment
                                    );

                                    pendingDetailRequests--;

                                    checkDetailLoadingComplete();
                                }
                            }
                    );
        }
    }

    private void addBasicRepair(
            Appointment appointment
    ) {

        AppointmentDetail detail =
                new AppointmentDetail();

        fillMissingValues(
                detail,
                appointment
        );

        allRepairs.add(
                detail
        );
    }

    private void fillMissingValues(
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
                    appointment
                            .getAppointment_number()
            );
        }

        if (detail.getStatus() == null) {

            detail.setStatus(
                    appointment.getStatus()
            );
        }

        if (detail.getRequested_date() == null) {

            detail.setRequested_date(
                    appointment
                            .getRequested_date()
            );
        }

        if (detail.getRequested_time() == null) {

            detail.setRequested_time(
                    appointment
                            .getRequested_time()
            );
        }

        if (detail.getCustomer_id() == null) {

            detail.setCustomer_id(
                    appointment
                            .getCustomer_id()
            );
        }

        if (detail.getDevice_id() == null) {

            detail.setDevice_id(
                    appointment
                            .getDevice_id()
            );
        }

        if (detail.getService_id() == null) {

            detail.setService_id(
                    appointment
                            .getService_id()
            );
        }

        if (detail.getBranch_id() == null) {

            detail.setBranch_id(
                    appointment
                            .getBranch_id()
            );
        }

        if (detail.getTechnician_id() == null) {

            detail.setTechnician_id(
                    appointment
                            .getTechnician_id()
            );
        }

        if (detail.getEstimated_price() == 0) {

            detail.setEstimated_price(
                    appointment
                            .getEstimated_price()
            );
        }

        if (detail.getFinal_price() == null) {

            detail.setFinal_price(
                    appointment
                            .getFinal_price()
            );
        }
    }

    private void checkDetailLoadingComplete() {

        if (pendingDetailRequests <= 0) {

            finishLoading();
        }
    }

    private void finishLoading() {

        showLoading(false);

        btnRefreshAssignedRepairs
                .setEnabled(true);

        applyFilter(
                currentFilter
        );
    }

    private void applyFilter(
            String filter
    ) {

        if (filter == null
                || filter.trim().isEmpty()) {

            currentFilter =
                    "ALL";

        } else {

            currentFilter =
                    filter.trim()
                            .toUpperCase(
                                    Locale.US
                            );
        }

        filteredRepairs.clear();

        for (AppointmentDetail repair :
                allRepairs) {

            if (repair == null) {
                continue;
            }

            if ("ALL".equals(
                    currentFilter
            )) {

                filteredRepairs.add(
                        repair
                );

                continue;
            }

            String status =
                    normalizeStatus(
                            repair.getStatus()
                    );

            if (status.equals(
                    currentFilter
            )) {

                filteredRepairs.add(
                        repair
                );
            }
        }

        repairAdapter.setRepairs(
                filteredRepairs
        );

        updateScreenState();
    }

    private void updateScreenState() {

        int count =
                filteredRepairs.size();

        if (count == 1) {

            tvAssignedRepairsTotal
                    .setText(
                            "1 repair"
                    );

        } else {

            tvAssignedRepairsTotal
                    .setText(
                            count
                                    + " repairs"
                    );
        }

        if (count == 0) {

            recyclerAssignedRepairs
                    .setVisibility(
                            View.GONE
                    );

            layoutEmptyAssignedRepairs
                    .setVisibility(
                            View.VISIBLE
                    );

        } else {

            recyclerAssignedRepairs
                    .setVisibility(
                            View.VISIBLE
                    );

            layoutEmptyAssignedRepairs
                    .setVisibility(
                            View.GONE
                    );
        }
    }

    private void showLoading(
            boolean loading
    ) {

        if (loading) {

            progressAssignedRepairs
                    .setVisibility(
                            View.VISIBLE
                    );

            recyclerAssignedRepairs
                    .setVisibility(
                            View.GONE
                    );

            layoutEmptyAssignedRepairs
                    .setVisibility(
                            View.GONE
                    );

        } else {

            progressAssignedRepairs
                    .setVisibility(
                            View.GONE
                    );
        }
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

    private String normalizeStatus(
            String status
    ) {

        if (status == null) {

            return "";
        }

        return status.trim()
                .toUpperCase(
                        Locale.US
                );
    }
}