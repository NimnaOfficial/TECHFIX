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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.mad.techfix.R;
import com.mad.techfix.models.Appointment;
import com.mad.techfix.viewmodel.MyAppointmentsViewModel;

import java.util.ArrayList;
import java.util.List;

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

    private MyAppointmentsViewModel viewModel;

    private String currentFilter = "ALL";


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

        bindViews(view);

        setupRecyclerView();

        setupFilters();

        setupListeners();

        setupViewModel();

        observeViewModel();

        viewModel.loadAppointments();
    }


    // ==========================================
    // VIEW BINDING
    // ==========================================

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


    // ==========================================
    // RECYCLER VIEW
    // ==========================================

    private void setupRecyclerView() {

        appointmentAdapter =
                new CustomerAppointmentAdapter(
                        appointment ->
                                openAppointmentDetail(
                                        appointment
                                )
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


    // ==========================================
    // VIEW MODEL
    // ==========================================

    private void setupViewModel() {

        viewModel =
                new ViewModelProvider(this)
                        .get(
                                MyAppointmentsViewModel.class
                        );
    }


    private void observeViewModel() {

        viewModel
                .getAppointments()
                .observe(
                        getViewLifecycleOwner(),
                        appointments -> {

                            if (appointments == null) {

                                appointments =
                                        new ArrayList<>();
                            }

                            appointmentAdapter
                                    .setAppointments(
                                            appointments
                                    );

                            appointmentAdapter
                                    .filterByStatus(
                                            currentFilter
                                    );

                            updateAppointmentState();
                        }
                );


        viewModel
                .getIsLoading()
                .observe(
                        getViewLifecycleOwner(),
                        loading -> {

                            boolean isLoading =
                                    loading != null
                                            && loading;

                            showLoading(
                                    isLoading
                            );

                            btnRefreshAppointments
                                    .setEnabled(
                                            !isLoading
                                    );
                        }
                );


        viewModel
                .getErrorMessage()
                .observe(
                        getViewLifecycleOwner(),
                        message -> {

                            if (message == null
                                    || message
                                    .trim()
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


    // ==========================================
    // FILTERS
    // ==========================================

    private void setupFilters() {

        chipAll.setOnClickListener(
                v -> applyFilter(
                        "ALL"
                )
        );

        chipRequested.setOnClickListener(
                v -> applyFilter(
                        "REQUESTED"
                )
        );

        chipAssigned.setOnClickListener(
                v -> applyFilter(
                        "ASSIGNED"
                )
        );

        chipInProgress.setOnClickListener(
                v -> applyFilter(
                        "IN_PROGRESS"
                )
        );

        chipCompleted.setOnClickListener(
                v -> applyFilter(
                        "COMPLETED"
                )
        );

        chipCancelled.setOnClickListener(
                v -> applyFilter(
                        "CANCELLED"
                )
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
                    filter;
        }

        appointmentAdapter
                .filterByStatus(
                        currentFilter
                );

        updateAppointmentState();
    }


    // ==========================================
    // BUTTONS
    // ==========================================

    private void setupListeners() {

        btnRefreshAppointments
                .setOnClickListener(
                        v -> viewModel
                                .refreshAppointments()
                );

        btnBookRepairEmpty
                .setOnClickListener(
                        v -> openRepairBooking()
                );
    }


    // ==========================================
    // APPOINTMENT DETAILS
    // ==========================================

    private void openAppointmentDetail(
            Appointment appointment
    ) {

        if (appointment == null) {
            return;
        }

        String appointmentId =
                appointment.getId();

        if (appointmentId == null
                || appointmentId
                .trim()
                .isEmpty()) {

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


    // ==========================================
    // UI STATE
    // ==========================================

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
                    count
                            + " appointments"
            );
        }


        if (count == 0) {

            recyclerAppointments
                    .setVisibility(
                            View.GONE
                    );

            layoutEmptyAppointments
                    .setVisibility(
                            View.VISIBLE
                    );

        } else {

            recyclerAppointments
                    .setVisibility(
                            View.VISIBLE
                    );

            layoutEmptyAppointments
                    .setVisibility(
                            View.GONE
                    );
        }
    }


    private void showLoading(
            boolean loading
    ) {

        if (loading) {

            progressAppointments
                    .setVisibility(
                            View.VISIBLE
                    );

            recyclerAppointments
                    .setVisibility(
                            View.GONE
                    );

            layoutEmptyAppointments
                    .setVisibility(
                            View.GONE
                    );

        } else {

            progressAppointments
                    .setVisibility(
                            View.GONE
                    );

            updateAppointmentState();
        }
    }


    // ==========================================
    // BOOK REPAIR
    // ==========================================

    private void openRepairBooking() {

        RepairBookingFragment fragment =
                new RepairBookingFragment();

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
}