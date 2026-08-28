package com.mad.techfix.ui.admin.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.mad.techfix.R;
import com.mad.techfix.ui.admin.assignment.AssignTechnicianBottomSheet;

public class AppointmentDetailBottomSheet extends BottomSheetDialogFragment {

    private String id, number, status, date, time, customer, branch;

    public static AppointmentDetailBottomSheet newInstance(String id, String number, String status, String date, String time, String customer, String branch) {
        AppointmentDetailBottomSheet fragment = new AppointmentDetailBottomSheet();
        Bundle args = new Bundle();
        args.putString("id", id);
        args.putString("number", number);
        args.putString("status", status);
        args.putString("date", date);
        args.putString("time", time);
        args.putString("customer", customer);
        args.putString("branch", branch);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_appointment_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            id = getArguments().getString("id");
            number = getArguments().getString("number");
            status = getArguments().getString("status");
            date = getArguments().getString("date");
            time = getArguments().getString("time");
            customer = getArguments().getString("customer");
            branch = getArguments().getString("branch");
        }

        TextView tvNumber = view.findViewById(R.id.tv_detail_number);
        TextView tvStatus = view.findViewById(R.id.tv_detail_status);
        TextView tvDateTime = view.findViewById(R.id.tv_detail_datetime);
        TextView tvCustomer = view.findViewById(R.id.tv_detail_customer);
        TextView tvBranch = view.findViewById(R.id.tv_detail_branch);
        MaterialButton btnAction = view.findViewById(R.id.btn_action);

        tvNumber.setText(number != null ? number : "N/A");
        tvStatus.setText(status != null ? status : "N/A");
        tvDateTime.setText((date != null ? date : "") + " " + (time != null ? time : ""));
        tvCustomer.setText(customer != null ? customer : "N/A");
        tvBranch.setText(branch != null ? branch : "N/A");

        if ("REQUESTED".equalsIgnoreCase(status) || "CONFIRMED".equalsIgnoreCase(status)) {
            btnAction.setText("Assign Technician");
            btnAction.setVisibility(View.VISIBLE);
        } else {
            btnAction.setVisibility(View.GONE);
        }

        btnAction.setOnClickListener(v -> {
            dismiss();
            AssignTechnicianBottomSheet assignSheet = AssignTechnicianBottomSheet.newInstance(
                    id, branch, number, "Service", "Branch"
            );
            assignSheet.show(getParentFragmentManager(), "AssignBottomSheet");
        });
    }
}
