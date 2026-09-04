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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.mad.techfix.R;
import com.mad.techfix.models.Appointment;
import com.mad.techfix.viewmodel.TechnicianAppointmentsViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AssignedRepairsFragment
        extends Fragment {

    private ImageButton btnRefreshAssignedRepairs;

    private Chip chipAll;
    private Chip chipDeviceReceived;
    private Chip chipDiagnosing;
    private Chip chipRepairing;
    private Chip chipTesting;
    private Chip chipCompleted;

    private TextView tvAssignedRepairsTotal;

    private RecyclerView recyclerAssignedRepairs;

    private ProgressBar progressAssignedRepairs;

    private View layoutEmptyAssignedRepairs;


    private AssignedRepairAdapter repairAdapter;

    private TechnicianAppointmentsViewModel viewModel;


    private final List<Appointment> allRepairs =
            new ArrayList<>();

    private final List<Appointment> filteredRepairs =
            new ArrayList<>();


    private String currentFilter =
            "ALL";


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


        bindViews(
                view
        );


        setupRecyclerView();

        setupFilters();

        setupViewModel();

        observeViewModel();

        setupListeners();


        viewModel.loadAppointments();
    }


    private void bindViews(
            View view
    ) {

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


        tvAssignedRepairsTotal =
                view.findViewById(
                        R.id.tv_assigned_repairs_total
                );


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


        recyclerAssignedRepairs
                .setLayoutManager(
                        new LinearLayoutManager(
                                requireContext()
                        )
                );


        recyclerAssignedRepairs
                .setAdapter(
                        repairAdapter
                );
    }


    private void setupFilters() {

        chipAll.setOnClickListener(
                view ->
                        applyFilter(
                                "ALL"
                        )
        );


        chipDeviceReceived
                .setOnClickListener(
                        view ->
                                applyFilter(
                                        "DEVICE_RECEIVED"
                                )
                );


        chipDiagnosing
                .setOnClickListener(
                        view ->
                                applyFilter(
                                        "DIAGNOSING"
                                )
                );


        chipRepairing
                .setOnClickListener(
                        view ->
                                applyFilter(
                                        "REPAIRING"
                                )
                );


        chipTesting
                .setOnClickListener(
                        view ->
                                applyFilter(
                                        "TESTING"
                                )
                );


        chipCompleted
                .setOnClickListener(
                        view ->
                                applyFilter(
                                        "COMPLETED"
                                )
                );
    }


    private void setupViewModel() {

        viewModel =
                new ViewModelProvider(this)
                        .get(
                                TechnicianAppointmentsViewModel.class
                        );
    }


    private void observeViewModel() {

        viewModel
                .getAppointments()
                .observe(
                        getViewLifecycleOwner(),
                        appointments -> {

                            allRepairs.clear();


                            if (appointments != null) {

                                allRepairs.addAll(
                                        appointments
                                );
                            }


                            applyFilter(
                                    currentFilter
                            );
                        }
                );


        viewModel
                .getIsLoading()
                .observe(
                        getViewLifecycleOwner(),
                        loading -> {

                            showLoading(
                                    loading != null
                                            && loading
                            );
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

        btnRefreshAssignedRepairs
                .setOnClickListener(
                        view ->
                                viewModel
                                        .refreshAppointments()
                );
    }


    private void applyFilter(
            String filter
    ) {

        currentFilter =
                filter == null
                        || filter.trim()
                        .isEmpty()
                        ? "ALL"
                        : filter
                        .trim()
                        .toUpperCase(
                                Locale.US
                        );


        filteredRepairs.clear();


        for (Appointment repair :
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


            if (currentFilter.equals(
                    status
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


        tvAssignedRepairsTotal
                .setText(
                        count == 1
                                ? "1 repair"
                                : count + " repairs"
                );


        recyclerAssignedRepairs
                .setVisibility(
                        count == 0
                                ? View.GONE
                                : View.VISIBLE
                );


        layoutEmptyAssignedRepairs
                .setVisibility(
                        count == 0
                                ? View.VISIBLE
                                : View.GONE
                );
    }


    private void showLoading(
            boolean loading
    ) {

        progressAssignedRepairs
                .setVisibility(
                        loading
                                ? View.VISIBLE
                                : View.GONE
                );


        btnRefreshAssignedRepairs
                .setEnabled(
                        !loading
                );


        if (loading) {

            recyclerAssignedRepairs
                    .setVisibility(
                            View.GONE
                    );


            layoutEmptyAssignedRepairs
                    .setVisibility(
                            View.GONE
                    );

        } else {

            updateScreenState();
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


        return status
                .trim()
                .toUpperCase(
                        Locale.US
                );
    }
}