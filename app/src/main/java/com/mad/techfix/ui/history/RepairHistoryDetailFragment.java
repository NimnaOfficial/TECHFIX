package com.mad.techfix.ui.history;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mad.techfix.R;
import com.mad.techfix.models.ApiResponse;
import com.mad.techfix.models.AppointmentDetail;
import com.mad.techfix.network.ApiService;
import com.mad.techfix.network.RetrofitClient;
import com.mad.techfix.utils.TokenManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RepairHistoryDetailFragment extends Fragment {

    private static final String ARG_APPOINTMENT_ID = "appointment_id";
    private static final String TAG = "HISTORY_DETAIL";

    // Views
    private TextView tvAppointmentNumber, tvStatus, tvProblemDescription;
    private TextView tvDate, tvTime, tvEstimatedPrice, tvFinalPrice;
    private TextView tvCustomerName, tvCustomerId;
    private TextView tvDeviceName, tvSerialNumber, tvPurchaseYear;
    private TextView tvServiceName;
    private TextView tvBranchName, tvBranchCity;
    private TextView tvTechnicianName, tvEmployeeCode;
    private TextView tvPaymentAmount, tvPaymentMethod, tvPaymentStatus;
    private RecyclerView rvStatusHistory;
    private ProgressBar progressBar;
    private View cardPayment;

    private ApiService apiService;
    private TokenManager tokenManager;
    private String appointmentId;

    public static RepairHistoryDetailFragment newInstance(String appointmentId) {
        RepairHistoryDetailFragment fragment = new RepairHistoryDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_APPOINTMENT_ID, appointmentId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_repair_history_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get appointment ID
        if (getArguments() != null) {
            appointmentId = getArguments().getString(ARG_APPOINTMENT_ID);
        }

        // Init views
        initViews(view);

        // Setup back button
        view.findViewById(R.id.btn_back).setOnClickListener(v -> requireActivity().onBackPressed());

        // Init helpers
        apiService = RetrofitClient.getClient().create(ApiService.class);
        tokenManager = new TokenManager(requireContext());

        // Fetch data
        fetchAppointmentDetail();
    }

    private void initViews(View view) {
        tvAppointmentNumber = view.findViewById(R.id.tv_appointment_number);
        tvStatus = view.findViewById(R.id.tv_status);
        tvProblemDescription = view.findViewById(R.id.tv_problem_description);
        tvDate = view.findViewById(R.id.tv_date);
        tvTime = view.findViewById(R.id.tv_time);
        tvEstimatedPrice = view.findViewById(R.id.tv_estimated_price);
        tvFinalPrice = view.findViewById(R.id.tv_final_price);
        tvCustomerName = view.findViewById(R.id.tv_customer_name);
        tvCustomerId = view.findViewById(R.id.tv_customer_id);
        tvDeviceName = view.findViewById(R.id.tv_device_name);
        tvSerialNumber = view.findViewById(R.id.tv_serial_number);
        tvPurchaseYear = view.findViewById(R.id.tv_purchase_year);
        tvServiceName = view.findViewById(R.id.tv_service_name);
        tvBranchName = view.findViewById(R.id.tv_branch_name);
        tvBranchCity = view.findViewById(R.id.tv_branch_city);
        tvTechnicianName = view.findViewById(R.id.tv_technician_name);
        tvEmployeeCode = view.findViewById(R.id.tv_employee_code);
        tvPaymentAmount = view.findViewById(R.id.tv_payment_amount);
        tvPaymentMethod = view.findViewById(R.id.tv_payment_method);
        tvPaymentStatus = view.findViewById(R.id.tv_payment_status);
        cardPayment = view.findViewById(R.id.card_payment);
        rvStatusHistory = view.findViewById(R.id.rv_status_history);
        progressBar = view.findViewById(R.id.progress_bar);

        rvStatusHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvStatusHistory.setNestedScrollingEnabled(false);
    }

    private void fetchAppointmentDetail() {
        String token = tokenManager.getToken();

        if (token == null) {
            Toast.makeText(getContext(), "⚠️ Please login first", Toast.LENGTH_SHORT).show();
            return;
        }

        if (appointmentId == null || appointmentId.isEmpty()) {
            Toast.makeText(getContext(), "❌ Invalid appointment ID", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        Log.d(TAG, "📡 Fetching appointment detail for ID: " + appointmentId);

        apiService.getAppointmentDetail("Bearer " + token, appointmentId).enqueue(new Callback<ApiResponse<AppointmentDetail>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<AppointmentDetail>> call,
                                   @NonNull Response<ApiResponse<AppointmentDetail>> response) {
                progressBar.setVisibility(View.GONE);

                Log.d(TAG, "📡 Response Code: " + response.code());

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    AppointmentDetail detail = response.body().getData();
                    if (detail != null) {
                        displayAppointmentDetail(detail);
                    } else {
                        Toast.makeText(getContext(), "No data found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMsg = "Failed to load details";
                    try {
                        if (response.errorBody() != null) {
                            errorMsg = response.errorBody().string();
                            Log.e(TAG, "❌ Error Body: " + errorMsg);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading error body", e);
                    }
                    Toast.makeText(getContext(), "❌ " + errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<AppointmentDetail>> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "❌ Network Failure: ", t);
                Toast.makeText(getContext(), "⚠️ Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void displayAppointmentDetail(AppointmentDetail detail) {
        // Appointment
        tvAppointmentNumber.setText(detail.getAppointment_number() != null ? detail.getAppointment_number() : "N/A");

        String status = detail.getStatus() != null ? detail.getStatus() : "UNKNOWN";
        tvStatus.setText(status);

        tvProblemDescription.setText(detail.getProblem_description() != null ? detail.getProblem_description() : "No description");

        // Date & Time
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            if (detail.getCreated_at() != null) {
                Date date = inputFormat.parse(detail.getCreated_at());
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                tvDate.setText(dateFormat.format(date));
                tvTime.setText(timeFormat.format(date));
            } else {
                tvDate.setText("N/A");
                tvTime.setText("N/A");
            }
        } catch (Exception e) {
            tvDate.setText("N/A");
            tvTime.setText("N/A");
        }

        tvEstimatedPrice.setText(String.format("$%.2f", detail.getEstimated_price()));
        tvFinalPrice.setText(detail.getFinal_price() != null ? String.format("$%.2f", detail.getFinal_price()) : "Not set");

        // Customer
        tvCustomerName.setText(detail.getCustomer_full_name() != null ? detail.getCustomer_full_name() : "N/A");
        tvCustomerId.setText(detail.getCustomer_id() != null ? detail.getCustomer_id() : "N/A");

        // Device
        tvDeviceName.setText(detail.getDevice_full_name() != null ? detail.getDevice_full_name() : "N/A");
        tvSerialNumber.setText(detail.getSerial_number() != null ? detail.getSerial_number() : "N/A");
        tvPurchaseYear.setText(detail.getPurchase_year() != null ? detail.getPurchase_year() : "N/A");

        // Service
        tvServiceName.setText(detail.getService_name() != null ? detail.getService_name() : "N/A");

        // Branch
        tvBranchName.setText(detail.getBranch_name() != null ? detail.getBranch_name() : "N/A");
        tvBranchCity.setText(detail.getBranch_city() != null ? detail.getBranch_city() : "N/A");

        // Technician
        tvTechnicianName.setText(detail.getTechnician_full_name() != null ? detail.getTechnician_full_name() : "Not assigned");
        tvEmployeeCode.setText(detail.getTechnician_employee_code() != null ? detail.getTechnician_employee_code() : "N/A");

        // Payment
        if (detail.getPayment() != null) {
            cardPayment.setVisibility(View.VISIBLE);
            tvPaymentAmount.setText(String.format("$%.2f", detail.getPayment().getAmount()));
            tvPaymentMethod.setText(detail.getPayment().getPayment_method() != null ? detail.getPayment().getPayment_method() : "N/A");
            tvPaymentStatus.setText(detail.getPayment().getPayment_status() != null ? detail.getPayment().getPayment_status() : "PENDING");
        } else {
            cardPayment.setVisibility(View.GONE);
        }

        // Status History
        List<AppointmentDetail.StatusHistory> history = detail.getStatus_history();
        if (history != null && !history.isEmpty()) {
            StatusHistoryAdapter historyAdapter = new StatusHistoryAdapter();
            historyAdapter.updateList(history);
            rvStatusHistory.setAdapter(historyAdapter);
        }
    }
}