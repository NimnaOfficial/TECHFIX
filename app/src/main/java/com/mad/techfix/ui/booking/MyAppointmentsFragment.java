package com.mad.techfix.ui.booking;

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

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.mad.techfix.R;
import com.mad.techfix.data.SessionManager;
import com.mad.techfix.models.ApiResponse;
import com.mad.techfix.models.Appointment;
import com.mad.techfix.models.Device;
import com.mad.techfix.models.admin.Branch;
import com.mad.techfix.models.admin.Service;
import com.mad.techfix.network.ApiService;
import com.mad.techfix.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyAppointmentsFragment extends Fragment {

    private RecyclerView recyclerAppointments;

    private ProgressBar progressAppointments;

    private View layoutEmptyAppointments;

    private TextView tvAppointmentCount;

    private ImageButton btnRefreshAppointments;

    private MaterialButton btnBookRepairEmpty;

    private Chip chipAll;
    private Chip chipRequested;
    private Chip chipAssigned;
    private Chip chipInProgress;
    private Chip chipCompleted;
    private Chip chipCancelled;

    private CustomerAppointmentAdapter appointmentAdapter;

    private ApiService apiService;
    private SessionManager sessionManager;

    private final List<Device> devices =
            new ArrayList<>();

    private final List<Service> services =
            new ArrayList<>();

    private final List<Branch> branches =
            new ArrayList<>();

    public MyAppointmentsFragment() {
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
                R.layout.fragment_my_appointments,
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

        loadAllData();
    }

    private void bindViews(
            View view
    ) {

        recyclerAppointments =
                view.findViewById(
                        R.id.recycler_my_appointments
                );

        progressAppointments =
                view.findViewById(
                        R.id.progress_appointments
                );

        layoutEmptyAppointments =
                view.findViewById(
                        R.id.layout_empty_appointments
                );

        tvAppointmentCount =
                view.findViewById(
                        R.id.tv_appointment_count
                );

        btnRefreshAppointments =
                view.findViewById(
                        R.id.btn_refresh_appointments
                );

        btnBookRepairEmpty =
                view.findViewById(
                        R.id.btn_book_repair_empty
                );

        chipAll =
                view.findViewById(
                        R.id.chip_all_appointments
                );

        chipRequested =
                view.findViewById(
                        R.id.chip_requested
                );

        chipAssigned =
                view.findViewById(
                        R.id.chip_assigned
                );

        chipInProgress =
                view.findViewById(
                        R.id.chip_in_progress
                );

        chipCompleted =
                view.findViewById(
                        R.id.chip_completed
                );

        chipCancelled =
                view.findViewById(
                        R.id.chip_cancelled
                );
    }

    private void setupRecyclerView() {

        appointmentAdapter =
                new CustomerAppointmentAdapter(
                        appointment -> {

                            String appointmentId =
                                    appointment.getId();

                            if (appointmentId == null
                                    || appointmentId.trim().isEmpty()) {

                                Toast.makeText(
                                        requireContext(),
                                        "Appointment ID is unavailable",
                                        Toast.LENGTH_SHORT
                                ).show();

                                return;
                            }

                            CustomerAppointmentDetailBottomSheet
                                    bottomSheet =
                                    CustomerAppointmentDetailBottomSheet
                                            .newInstance(
                                                    appointmentId
                                            );

                            bottomSheet.show(
                                    getParentFragmentManager(),
                                    "CustomerAppointmentDetail"
                            );
                        }
                );

        recyclerAppointments.setLayoutManager(
                new LinearLayoutManager(
                        requireContext()
                )
        );

        recyclerAppointments.setAdapter(
                appointmentAdapter
        );
    }

    private void setupFilters() {

        chipAll.setOnClickListener(
                v -> applyFilter("ALL")
        );

        chipRequested.setOnClickListener(
                v -> applyFilter("REQUESTED")
        );

        chipAssigned.setOnClickListener(
                v -> applyFilter("ASSIGNED")
        );

        chipInProgress.setOnClickListener(
                v -> applyFilter("IN_PROGRESS")
        );

        chipCompleted.setOnClickListener(
                v -> applyFilter("COMPLETED")
        );

        chipCancelled.setOnClickListener(
                v -> applyFilter("CANCELLED")
        );
    }

    private void setupListeners() {

        btnRefreshAppointments
                .setOnClickListener(
                        v -> loadAllData()
                );

        btnBookRepairEmpty
                .setOnClickListener(
                        v -> openRepairBooking()
                );
    }

    private void loadAllData() {

        loadAppointments();

        loadDevices();

        loadServices();

        loadBranches();
    }

    private void loadAppointments() {

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

        apiService
                .getAppointments(token)
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

                                showLoading(false);

                                if (response.isSuccessful()
                                        && response.body() != null
                                        && response.body()
                                        .isSuccess()) {

                                    List<Appointment> appointments =
                                            response.body()
                                                    .getData();

                                    appointmentAdapter
                                            .setAppointments(
                                                    appointments
                                            );

                                    updateAppointmentState();

                                } else {

                                    appointmentAdapter
                                            .setAppointments(
                                                    new ArrayList<>()
                                            );

                                    updateAppointmentState();

                                    Toast.makeText(
                                            requireContext(),
                                            "Unable to load appointments",
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

                                showLoading(false);

                                appointmentAdapter
                                        .setAppointments(
                                                new ArrayList<>()
                                        );

                                updateAppointmentState();

                                Toast.makeText(
                                        requireContext(),
                                        "Appointment loading failed: "
                                                + getErrorMessage(t),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
    }

    private void loadDevices() {

        String token =
                sessionManager
                        .getBearerToken();

        if (token == null
                || token.trim().isEmpty()) {

            return;
        }

        apiService
                .getMyDevices(token)
                .enqueue(
                        new Callback<
                                ApiResponse<
                                        List<Device>
                                        >
                                >() {

                            @Override
                            public void onResponse(
                                    @NonNull
                                    Call<
                                            ApiResponse<
                                                    List<Device>
                                                    >
                                            > call,

                                    @NonNull
                                    Response<
                                            ApiResponse<
                                                    List<Device>
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

                                    devices.clear();

                                    if (response.body()
                                            .getData() != null) {

                                        devices.addAll(
                                                response.body()
                                                        .getData()
                                        );
                                    }

                                    updateLookupData();
                                }
                            }

                            @Override
                            public void onFailure(
                                    @NonNull
                                    Call<
                                            ApiResponse<
                                                    List<Device>
                                                    >
                                            > call,

                                    @NonNull
                                    Throwable t
                            ) {

                                // List can still work
                                // without device display names.
                            }
                        }
                );
    }

    private void loadServices() {

        apiService
                .getServices()
                .enqueue(
                        new Callback<
                                ApiResponse<
                                        List<Service>
                                        >
                                >() {

                            @Override
                            public void onResponse(
                                    @NonNull
                                    Call<
                                            ApiResponse<
                                                    List<Service>
                                                    >
                                            > call,

                                    @NonNull
                                    Response<
                                            ApiResponse<
                                                    List<Service>
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

                                    services.clear();

                                    if (response.body()
                                            .getData() != null) {

                                        services.addAll(
                                                response.body()
                                                        .getData()
                                        );
                                    }

                                    updateLookupData();
                                }
                            }

                            @Override
                            public void onFailure(
                                    @NonNull
                                    Call<
                                            ApiResponse<
                                                    List<Service>
                                                    >
                                            > call,

                                    @NonNull
                                    Throwable t
                            ) {

                                // List can still work
                                // without service display names.
                            }
                        }
                );
    }

    private void loadBranches() {

        apiService
                .getBranches()
                .enqueue(
                        new Callback<
                                ApiResponse<
                                        List<Branch>
                                        >
                                >() {

                            @Override
                            public void onResponse(
                                    @NonNull
                                    Call<
                                            ApiResponse<
                                                    List<Branch>
                                                    >
                                            > call,

                                    @NonNull
                                    Response<
                                            ApiResponse<
                                                    List<Branch>
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

                                    branches.clear();

                                    if (response.body()
                                            .getData() != null) {

                                        branches.addAll(
                                                response.body()
                                                        .getData()
                                        );
                                    }

                                    updateLookupData();
                                }
                            }

                            @Override
                            public void onFailure(
                                    @NonNull
                                    Call<
                                            ApiResponse<
                                                    List<Branch>
                                                    >
                                            > call,

                                    @NonNull
                                    Throwable t
                            ) {

                                // List can still work
                                // without branch display names.
                            }
                        }
                );
    }

    private void updateLookupData() {

        appointmentAdapter
                .setLookupData(
                        devices,
                        services,
                        branches
                );
    }

    private void applyFilter(
            String filter
    ) {

        appointmentAdapter
                .filterByStatus(
                        filter
                );

        updateAppointmentState();
    }

    private void updateAppointmentState() {

        int count =
                appointmentAdapter
                        .getVisibleCount();

        if (count == 1) {

            tvAppointmentCount.setText(
                    "1 appointment"
            );

        } else {

            tvAppointmentCount.setText(
                    count + " appointments"
            );
        }

        if (count == 0) {

            recyclerAppointments.setVisibility(
                    View.GONE
            );

            layoutEmptyAppointments.setVisibility(
                    View.VISIBLE
            );

        } else {

            recyclerAppointments.setVisibility(
                    View.VISIBLE
            );

            layoutEmptyAppointments.setVisibility(
                    View.GONE
            );
        }
    }

    private void showLoading(
            boolean loading
    ) {

        if (loading) {

            progressAppointments.setVisibility(
                    View.VISIBLE
            );

            recyclerAppointments.setVisibility(
                    View.GONE
            );

            layoutEmptyAppointments.setVisibility(
                    View.GONE
            );

        } else {

            progressAppointments.setVisibility(
                    View.GONE
            );
        }
    }

    private void openRepairBooking() {

        RepairBookingFragment repairBookingFragment =
                new RepairBookingFragment();

        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(
                        getId(),
                        repairBookingFragment
                )
                .addToBackStack(null)
                .commit();
    }

    private String getErrorMessage(
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
}