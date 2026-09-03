package com.mad.techfix.ui.payment;

import android.os.Bundle;
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

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.mad.techfix.R;
import com.mad.techfix.models.ApiResponse;
import com.mad.techfix.models.Payment;
import com.mad.techfix.models.PaymentIntentRequest;
import com.mad.techfix.models.PaymentIntentResponse;
import com.mad.techfix.network.ApiService;
import com.mad.techfix.network.RetrofitClient;
import com.mad.techfix.utils.StripeConfig;
import com.mad.techfix.utils.TokenManager;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentFragment extends Fragment {

    private TextInputEditText etAppointmentId, etAmount;
    private MaterialButton btnStripePay;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private PaymentAdapter adapter;
    private ApiService apiService;
    private TokenManager tokenManager;

    // Stripe PaymentSheet
    private PaymentSheet paymentSheet;
    private String clientSecret;
    private String currentPaymentId;

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
        btnStripePay = view.findViewById(R.id.btn_stripe_pay);
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

        // Initialize Stripe PaymentSheet
        PaymentConfiguration.init(requireContext(), StripeConfig.PUBLISHABLE_KEY);
        paymentSheet = new PaymentSheet(this, this::onPaymentSheetResult);

        // Stripe Payment Button
        btnStripePay.setOnClickListener(v -> startStripePayment());

        // Auto-fetch payments when appointment ID changes
        etAppointmentId.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                fetchPayments();
            }
        });
    }

    // ==========================================
    // STRIPE PAYMENT
    // ==========================================
    private void startStripePayment() {
        String appointmentId = etAppointmentId.getText() != null ? etAppointmentId.getText().toString().trim() : "";
        String amountStr = etAmount.getText() != null ? etAmount.getText().toString().trim() : "";

        if (appointmentId.isEmpty()) {
            Toast.makeText(getContext(), "Please enter an Appointment ID", Toast.LENGTH_SHORT).show();
            return;
        }
        if (amountStr.isEmpty()) {
            Toast.makeText(getContext(), "Please enter an amount", Toast.LENGTH_SHORT).show();
            return;
        }

        long amount;
        try {
            amount = (long) (Double.parseDouble(amountStr) * 100); // Convert to cents
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid amount format", Toast.LENGTH_SHORT).show();
            return;
        }

        String token = tokenManager.getToken();
        if (token == null) {
            Toast.makeText(getContext(), "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnStripePay.setEnabled(false);

        apiService.createPaymentIntent("Bearer " + token, new PaymentIntentRequest(appointmentId, amount))
                .enqueue(new Callback<PaymentIntentResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<PaymentIntentResponse> call, @NonNull Response<PaymentIntentResponse> response) {
                        progressBar.setVisibility(View.GONE);
                        btnStripePay.setEnabled(true);

                        if (response.isSuccessful() && response.body() != null) {
                            clientSecret = response.body().getClientSecret();
                            currentPaymentId = response.body().getPaymentId();
                            presentPaymentSheet();
                        } else {
                            String errorMsg = "Failed to create payment intent";
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
                    public void onFailure(@NonNull Call<PaymentIntentResponse> call, @NonNull Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        btnStripePay.setEnabled(true);
                        Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void presentPaymentSheet() {
        PaymentSheet.Configuration configuration = new PaymentSheet.Configuration("TechFixCustomer");
        paymentSheet.presentWithPaymentIntent(clientSecret, configuration);
    }

    private void onPaymentSheetResult(@NonNull PaymentSheetResult result) {
        if (result instanceof PaymentSheetResult.Completed) {
            Toast.makeText(getContext(), "✅ Stripe payment successful!", Toast.LENGTH_SHORT).show();
            updatePaymentStatus(currentPaymentId, "PAID");

        } else if (result instanceof PaymentSheetResult.Canceled) {
            Toast.makeText(getContext(), "Payment canceled", Toast.LENGTH_SHORT).show();

        } else if (result instanceof PaymentSheetResult.Failed) {
            String error = ((PaymentSheetResult.Failed) result).getError().getMessage();
            Toast.makeText(getContext(), "❌ Payment failed: " + error, Toast.LENGTH_LONG).show();
        }
    }

    private void updatePaymentStatus(String paymentId, String status) {
        String token = tokenManager.getToken();
        if (token == null) return;

        Payment payment = new Payment();
        payment.setPayment_status(status);

        apiService.updatePaymentStatus("Bearer " + token, paymentId, payment)
                .enqueue(new Callback<ApiResponse<Object>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResponse<Object>> call, @NonNull Response<ApiResponse<Object>> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "✅ Payment confirmed!", Toast.LENGTH_SHORT).show();
                            fetchPayments();
                        } else {
                            Toast.makeText(getContext(), "Failed to update payment status", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiResponse<Object>> call, @NonNull Throwable t) {
                        Toast.makeText(getContext(), "Network error updating status", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // ==========================================
    // FETCH PAYMENTS
    // ==========================================
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

        apiService.getAppointmentPayments("Bearer " + token, appointmentId)
                .enqueue(new Callback<ApiResponse<List<Payment>>>() {
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