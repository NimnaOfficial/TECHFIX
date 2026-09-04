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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.mad.techfix.R;
import com.mad.techfix.models.Appointment;
import com.mad.techfix.ui.history.RepairHistoryFragment;
import com.mad.techfix.viewmodel.TechnicianAppointmentsViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TechnicianDashboardFragment
        extends Fragment {

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

    private TechnicianAppointmentsViewModel viewModel;


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


        bindViews(
                view
        );


        setupRecyclerView();

        setupViewModel();

        observeViewModel();

        setupListeners();

        setupTechnicianInformation();


        viewModel.loadAppointments();
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


        recyclerDashboardRepairs
                .setLayoutManager(
                        new LinearLayoutManager(
                                requireContext()
                        )
                );


        recyclerDashboardRepairs
                .setAdapter(
                        repairAdapter
                );


        recyclerDashboardRepairs
                .setNestedScrollingEnabled(
                        false
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

                            renderDashboard(
                                    appointments
                            );
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


                            btnRefreshDashboard
                                    .setEnabled(
                                            !active
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


    private void setupTechnicianInformation() {

        String technicianName =
                viewModel
                        .getTechnicianName();


        tvTechnicianName.setText(
                technicianName
        );


        tvTechnicianWelcome.setText(
                "Welcome back, "
                        + technicianName
        );


        tvTechnicianSpecialization.setText(
                "Device Repair Technician"
        );


        tvTechnicianStatus.setText(
                "AVAILABLE"
        );
    }


    private void setupListeners() {

        btnRefreshDashboard
                .setOnClickListener(
                        view ->
                                viewModel
                                        .refreshAppointments()
                );


        btnViewAllAssigned
                .setOnClickListener(
                        view ->
                                openAssignedRepairs()
                );


        btnAssignedRepairs
                .setOnClickListener(
                        view ->
                                openAssignedRepairs()
                );


        btnRepairHistory
                .setOnClickListener(
                        view ->
                                openRepairHistory()
                );
    }


    private void renderDashboard(
            List<Appointment> appointments
    ) {

        if (appointments == null) {

            appointments =
                    new ArrayList<>();
        }


        int assignedCount =
                0;

        int inProgressCount =
                0;

        int completedCount =
                0;

        int activeCount =
                0;


        List<Appointment> activeRepairs =
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


            if ("ASSIGNED".equals(status)) {

                assignedCount++;
                activeCount++;

            } else if (isInProgressStatus(
                    status
            )) {

                inProgressCount++;
                activeCount++;

            } else if ("READY".equals(
                    status
            )) {

                inProgressCount++;
                activeCount++;

            } else if ("COMPLETED".equals(
                    status
            )) {

                completedCount++;
            }


            if (isActiveRepairStatus(
                    status
            )) {

                activeRepairs.add(
                        appointment
                );
            }
        }


        tvAssignedRepairsCount
                .setText(
                        String.valueOf(
                                assignedCount
                        )
                );


        tvInProgressCount
                .setText(
                        String.valueOf(
                                inProgressCount
                        )
                );


        tvCompletedRepairsCount
                .setText(
                        String.valueOf(
                                completedCount
                        )
                );


        tvTechnicianStatus
                .setText(
                        activeCount > 0
                                ? "BUSY"
                                : "AVAILABLE"
                );


        List<Appointment> preview =
                new ArrayList<>();


        int limit =
                Math.min(
                        activeRepairs.size(),
                        3
                );


        for (int i = 0;
             i < limit;
             i++) {

            preview.add(
                    activeRepairs.get(i)
            );
        }


        repairAdapter.setRepairs(
                preview
        );


        showEmptyState(
                preview.isEmpty()
        );
    }


    private boolean isInProgressStatus(
            String status
    ) {

        return "DEVICE_RECEIVED".equals(status)
                || "DIAGNOSING".equals(status)
                || "REPAIRING".equals(status)
                || "TESTING".equals(status);
    }


    private boolean isActiveRepairStatus(
            String status
    ) {

        return "ASSIGNED".equals(status)
                || "DEVICE_RECEIVED".equals(status)
                || "DIAGNOSING".equals(status)
                || "REPAIRING".equals(status)
                || "TESTING".equals(status)
                || "READY".equals(status);
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

        recyclerDashboardRepairs
                .setVisibility(
                        empty
                                ? View.GONE
                                : View.VISIBLE
                );


        cardNoCurrentRepairs
                .setVisibility(
                        empty
                                ? View.VISIBLE
                                : View.GONE
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

        getParentFragmentManager()
                .beginTransaction()
                .replace(
                        R.id.technician_fragment_container,
                        new AssignedRepairsFragment()
                )
                .addToBackStack(
                        null
                )
                .commit();
    }


    private void openRepairHistory() {

        getParentFragmentManager()
                .beginTransaction()
                .replace(
                        R.id.technician_fragment_container,
                        new RepairHistoryFragment()
                )
                .addToBackStack(
                        null
                )
                .commit();
    }
}