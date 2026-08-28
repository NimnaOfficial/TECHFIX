package com.mad.techfix;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.mad.techfix.data.SessionManager;
import com.mad.techfix.models.AuthResponse;
import com.mad.techfix.models.User;
import com.mad.techfix.network.ApiService;
import com.mad.techfix.network.RetrofitClient;
import com.mad.techfix.ui.auth.LoginActivity;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private TextView tvUserName, tvUserEmail, tvUserRoleBadge;
    private TextView tvUserId, tvUserPhone, tvTokenPreview, tvLiveStatus;
    private MaterialButton btnVerifyToken, btnLogout;
    private SessionManager sessionManager;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);

        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        apiService = RetrofitClient.getApiService();

        initViews();
        displaySessionData();
        setupListeners();
        checkBackendHealth();
    }

    private void initViews() {
        tvUserName = findViewById(R.id.tvUserName);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        tvUserRoleBadge = findViewById(R.id.tvUserRoleBadge);
        tvUserId = findViewById(R.id.tvUserId);
        tvUserPhone = findViewById(R.id.tvUserPhone);
        tvTokenPreview = findViewById(R.id.tvTokenPreview);
        tvLiveStatus = findViewById(R.id.tvLiveStatus);
        btnVerifyToken = findViewById(R.id.btnVerifyToken);
        btnLogout = findViewById(R.id.btnLogout);
    }

    private void displaySessionData() {
        String name = sessionManager.getUserName();
        String email = sessionManager.getUserEmail();
        String role = sessionManager.getUserRole();
        String id = sessionManager.getUserId();
        String phone = sessionManager.getUserPhone();
        String token = sessionManager.getAuthToken();

        tvUserName.setText(name != null && !name.isEmpty() ? name : "TechFix User");
        tvUserEmail.setText(email);
        tvUserRoleBadge.setText(role != null ? role.toUpperCase() : "CUSTOMER");
        tvUserId.setText(id != null && !id.isEmpty() ? id : "N/A");
        tvUserPhone.setText(phone != null && !phone.isEmpty() ? phone : "N/A");

        if (token != null && token.length() > 24) {
            tvTokenPreview.setText(token.substring(0, 16) + "..." + token.substring(token.length() - 8));
        } else if (token != null && !token.isEmpty()) {
            tvTokenPreview.setText(token);
        } else {
            tvTokenPreview.setText("No Token Stored");
        }
    }

    private void setupListeners() {
        btnVerifyToken.setOnClickListener(v -> verifyTokenWithBackend());

        btnLogout.setOnClickListener(v -> {
            sessionManager.clearSession();
            Toast.makeText(MainActivity.this, "Signed out successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void checkBackendHealth() {
        tvLiveStatus.setText("Pinging Cloudflare Worker & D1 Database...");
        apiService.getHealth().enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> body = response.body();
                    String dbStatus = String.valueOf(body.get("database"));
                    tvLiveStatus.setText("✓ Cloudflare Edge: Online\n✓ D1 SQLite Database: " + ("connected".equalsIgnoreCase(dbStatus) ? "Connected (Healthy)" : dbStatus));
                    tvLiveStatus.setTextColor(getColor(R.color.status_success));
                } else {
                    tvLiveStatus.setText("⚠ Backend reachable but returned status: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                tvLiveStatus.setText("✕ Backend connection failed: " + t.getMessage());
                tvLiveStatus.setTextColor(getColor(R.color.status_error));
            }
        });
    }

    private void verifyTokenWithBackend() {
        tvLiveStatus.setText("Validating JWT token via GET /api/me...");
        apiService.getMe(sessionManager.getBearerToken()).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body().getUser();
                    if (user != null) {
                        tvLiveStatus.setText("✓ Token Verified! Sub ID: " + user.getId() + " | Role: " + user.getRole() + "\n✓ Authenticated as: " + user.getFullName());
                        tvLiveStatus.setTextColor(getColor(R.color.status_success));
                        Toast.makeText(MainActivity.this, "JWT Signature Validated on Cloudflare Edge!", Toast.LENGTH_SHORT).show();
                    } else {
                        tvLiveStatus.setText("✓ Token Accepted by Worker");
                    }
                } else {
                    tvLiveStatus.setText("✕ Authentication Rejected: HTTP " + response.code());
                    tvLiveStatus.setTextColor(getColor(R.color.status_error));
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                tvLiveStatus.setText("✕ Request Failed: " + t.getMessage());
                tvLiveStatus.setTextColor(getColor(R.color.status_error));
            }
        });
    }
}
