package com.mad.techfix.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mad.techfix.MainActivity;
import com.mad.techfix.R;
import com.mad.techfix.data.SessionManager;
import com.mad.techfix.models.AuthResponse;
import com.mad.techfix.models.LoginRequest;
import com.mad.techfix.models.User;
import com.mad.techfix.network.RetrofitClient;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin, btnQuickCustomer, btnQuickAdmin;
    private ProgressBar progressBar;
    private TextView tvRegister;
    private SessionManager sessionManager;

    private static final String DEFAULT_CUSTOMER_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJmZDFjMGVmZC1lNDBkLTRmY2EtODI4Yy1hMDMyOGI5MDA2OTQiLCJyb2xlIjoiQ1VTVE9NRVIiLCJlbWFpbCI6ImpvaG5AZXhhbXBsZS5jb20iLCJpYXQiOjE3ODc5MzE1MzAsImV4cCI6MTc4ODUzNjMzMH0.U6px93U44LowTWPwuWVMbhKY71yzm2SSuGlF0rajsv4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(this);

        initViews();
        setupListeners();
        checkPassedEmail();
    }

    private void initViews() {
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnQuickCustomer = findViewById(R.id.btnQuickCustomer);
        btnQuickAdmin = findViewById(R.id.btnQuickAdmin);
        progressBar = findViewById(R.id.progressBar);
        tvRegister = findViewById(R.id.tvRegister);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> performLogin());

        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Quick Autofill Helpers for Live Evaluation
        btnQuickCustomer.setOnClickListener(v -> {
            etEmail.setText("john@example.com");
            etPassword.setText("Password123!");
            tilEmail.setError(null);
            tilPassword.setError(null);
        });

        // Long click on Customer button instantly validates and logs in with active Customer Token
        btnQuickCustomer.setOnLongClickListener(v -> {
            setLoading(true);
            RetrofitClient.getApiService().getMe("Bearer " + DEFAULT_CUSTOMER_TOKEN).enqueue(new Callback<AuthResponse>() {
                @Override
                public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                    setLoading(false);
                    if (response.isSuccessful() && response.body() != null && response.body().getUser() != null) {
                        User user = response.body().getUser();
                        sessionManager.saveAuthSession(DEFAULT_CUSTOMER_TOKEN, user);

                        Toast.makeText(LoginActivity.this,
                                "Welcome, " + user.getFullName() + " (Active Customer Session)",
                                Toast.LENGTH_SHORT).show();

                                                                        Intent intent;
                        String userRole = user.getRole();
                        if (userRole == null) userRole = "CUSTOMER"; // Fallback
                        
                        if ("ADMIN".equalsIgnoreCase(userRole)) {
                            intent = new Intent(LoginActivity.this, com.mad.techfix.ui.admin.SystemAdminActivity.class);
                        } else if ("MANAGER".equalsIgnoreCase(userRole)) {
                            intent = new Intent(LoginActivity.this, com.mad.techfix.ui.admin.AdminActivity.class);
                        } else if ("TECHNICIAN".equalsIgnoreCase(userRole)) {
                            intent = new Intent(LoginActivity.this, com.mad.techfix.ui.technician.TechnicianActivity.class);
                        } else {
                            intent = new Intent(LoginActivity.this, com.mad.techfix.ui.customer.CustomerDashboardActivity.class);
                        }
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Token validation returned code " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<AuthResponse> call, Throwable t) {
                    setLoading(false);
                    Toast.makeText(LoginActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            return true;
        });

        btnQuickAdmin.setOnClickListener(v -> {
            etEmail.setText("admin@techfix.test");
            etPassword.setText("AdminPass123!");
            tilEmail.setError(null);
            tilPassword.setError(null);
        });
    }

    private void checkPassedEmail() {
        String passedEmail = getIntent().getStringExtra("registered_email");
        if (passedEmail != null && !passedEmail.isEmpty()) {
            etEmail.setText(passedEmail);
            etPassword.requestFocus();
        }
    }

    private void performLogin() {
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        tilEmail.setError(null);
        tilPassword.setError(null);

        boolean hasError = false;

        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Email address is required");
            hasError = true;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Please enter a valid email address");
            hasError = true;
        }

        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("Password is required");
            hasError = true;
        }

        if (hasError) return;

        setLoading(true);

        LoginRequest request = new LoginRequest(email, password);

        RetrofitClient.getApiService().loginUser(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                setLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();
                    User user = authResponse.getUser();
                    String token = authResponse.getToken();

                    if (user != null && token != null) {
                        // Save session details including JWT Token
                        sessionManager.saveAuthSession(token, user);

                        Toast.makeText(LoginActivity.this,
                                "Welcome back, " + user.getFullName() + " (" + user.getRole() + ")",
                                Toast.LENGTH_SHORT).show();

                        // Navigate to Member 1 Implementation Hub
                                                                        Intent intent;
                        String userRole = user.getRole();
                        if (userRole == null) userRole = "CUSTOMER"; // Fallback
                        
                        if ("ADMIN".equalsIgnoreCase(userRole)) {
                            intent = new Intent(LoginActivity.this, com.mad.techfix.ui.admin.SystemAdminActivity.class);
                        } else if ("MANAGER".equalsIgnoreCase(userRole)) {
                            intent = new Intent(LoginActivity.this, com.mad.techfix.ui.admin.AdminActivity.class);
                        } else if ("TECHNICIAN".equalsIgnoreCase(userRole)) {
                            intent = new Intent(LoginActivity.this, com.mad.techfix.ui.technician.TechnicianActivity.class);
                        } else {
                            intent = new Intent(LoginActivity.this, com.mad.techfix.ui.customer.CustomerDashboardActivity.class);
                        }
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Authentication succeeded but user data was incomplete.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    String errorMsg = "Invalid email or password";
                    try {
                        if (response.errorBody() != null) {
                            String errStr = response.errorBody().string();
                            JSONObject json = new JSONObject(errStr);
                            if (json.has("message")) {
                                errorMsg = json.getString("message");
                            }
                        }
                    } catch (Exception ignored) {}

                    if (response.code() == 401) {
                        tilPassword.setError(errorMsg);
                    } else if (response.code() == 403) {
                        tilEmail.setError("Account is inactive");
                    }
                    Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                setLoading(false);
                Toast.makeText(LoginActivity.this, "Network Connection Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setLoading(boolean isLoading) {
        if (isLoading) {
            btnLogin.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            btnLogin.setEnabled(true);
            progressBar.setVisibility(View.GONE);
        }
    }
}



