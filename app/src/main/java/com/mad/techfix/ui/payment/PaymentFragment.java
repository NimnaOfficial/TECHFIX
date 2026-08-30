package com.mad.techfix.ui.payment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.mad.techfix.R;
import com.mad.techfix.models.ApiResponse;
import com.mad.techfix.models.Payment;
import com.mad.techfix.network.ApiService;
import com.mad.techfix.network.RetrofitClient;
import com.mad.techfix.utils.TokenManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentFragment extends Fragment {

    private TextInputEditText etAppointmentId, etAmount;
    private android.widget.Spinner spinnerPaymentMethod;
    private MaterialButton btnCreatePayment;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private PaymentAdapter adapter;
    private ApiService apiService;
    private TokenManager tokenManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Init views
        etAppointmentId = view.findViewById(R.id.et_appointment_id);
        etAmount = view.findViewById(R.id.et_amount);
        spinnerPaymentMethod = view.findViewById(R.id.spinner_payment_method);
        btnCreatePayment = view.findViewById(R.id.btn_create_payment);
        recyclerView = view.findViewById(R.id.rv_payments);
        progressBar = view.findViewById(R.id.progress_bar);
        tvEmpty = view.findViewById(R.id.tv_empty);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PaymentAdapter();
        recyclerView.setAdapter(adapter);

        // Init helpers
        apiService = RetrofitClient.getClient().create(ApiService.class);
        tokenManager = new TokenManager(requireContext());

        // Setup Spinner
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.payment_methods,
                android.R.layout.simple_spinner_item
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPaymentMethod.setAdapter(spinnerAdapter);

        // Create Payment Button
        btnCreatePayment.setOnClickListener(v -> createPayment());

        // Auto-fetch payments when appointment ID changes (optional, but good UX)
        etAppointmentId.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                fetchPayments();
            }
        });
    }

    private void createPayment() {
        String appointmentId = etAppointmentId.getText() != null ? etAppointmentId.getText().toString().trim() : "";
        String amountStr = etAmount.getText() != null ? etAmount.getText().toString().trim() : "";
        String method = spinnerPaymentMethod.getSelectedItem() != null ? spinnerPaymentMethod.getSelectedItem().toString() : "CASH";

        // Validation
        if (appointmentId.isEmpty()) {
            Toast.makeText(getContext(), "Please enter an Appointment ID", Toast.LENGTH_SHORT).show();
            return;
        }
        if (amountStr.isEmpty()) {
            Toast.makeText(getContext(), "Please enter an amount", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid amount format", Toast.LENGTH_SHORT).show();
            return;
        }

        String token = tokenManager.getToken();
        if (token == null) {
            Toast.makeText(getContext(), "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }

        // Build Payment object
        Payment payment = new Payment();
        payment.setAppointment_id(appointmentId);
        payment.setAmount(amount);
        payment.setPayment_method(method);
        payment.setPayment_status("PENDING");
        payment.setCreated_at(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));

        progressBar.setVisibility(View.VISIBLE);
        btnCreatePayment.setEnabled(false);

        apiService.createPayment("Bearer " + token, payment).enqueue(new Callback<ApiResponse<Payment>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Payment>> call, @NonNull Response<ApiResponse<Payment>> response) {
                progressBar.setVisibility(View.GONE);
                btnCreatePayment.setEnabled(true);

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(getContext(), "✅ Payment created successfully!", Toast.LENGTH_SHORT).show();
                    etAmount.setText(""); // Clear amount field
                    fetchPayments(); // Refresh the list
                } else {
                    String errorMsg = "Failed to create payment";
                    try {
                        if (response.errorBody() != null) {
                            errorMsg = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getContext(), "❌ " + errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<Payment>> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnCreatePayment.setEnabled(true);
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void fetchPayments() {
        String appointmentId = etAppointmentId.getText() != null ? etAppointmentId.getText().toString().trim() : "";

        if (appointmentId.isEmpty()) {
            adapter.updateList(new ArrayList<>());
            tvEmpty.setVisibility(View.VISIBLE);
            tvEmpty.setText("Enter an Appointment ID above.");
            return;
        }

        String token = tokenManager.getToken();
        if (token == null) {
            Toast.makeText(getContext(), "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        apiService.getAppointmentPayments("Bearer " + token, appointmentId).enqueue(new Callback<ApiResponse<List<Payment>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<Payment>>> call, @NonNull Response<ApiResponse<List<Payment>>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Payment> payments = response.body().getData();
                    if (payments != null && !payments.isEmpty()) {
                        adapter.updateList(payments);
                        tvEmpty.setVisibility(View.GONE);
                    } else {
                        adapter.updateList(new ArrayList<>());
                        tvEmpty.setVisibility(View.VISIBLE);
                        tvEmpty.setText("No payments for this appointment.");
                    }
                } else {
                    String errorMsg = "Failed to fetch payments";
                    try {
                        if (response.errorBody() != null) {
                            errorMsg = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getContext(), "❌ " + errorMsg, Toast.LENGTH_LONG).show();
                    adapter.updateList(new ArrayList<>());
                    tvEmpty.setVisibility(View.VISIBLE);
                    tvEmpty.setText("Could not fetch payments.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<Payment>>> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                adapter.updateList(new ArrayList<>());
                tvEmpty.setVisibility(View.VISIBLE);
                tvEmpty.setText("Network error.");
            }
        });
    }
}