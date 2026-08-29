package com.mad.techfix.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.mad.techfix.R;
import com.mad.techfix.models.LoginRequest;
import com.mad.techfix.models.AuthResponse;
import com.mad.techfix.network.ApiService;
import com.mad.techfix.network.RetrofitClient;
import com.mad.techfix.ui.admin.branches.BranchListFragment;
import com.mad.techfix.ui.admin.dashboard.AdminDashboardFragment;
import com.mad.techfix.ui.admin.technicians.TechnicianListFragment;
import com.mad.techfix.utils.TokenManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminActivity extends AppCompatActivity {

    private TokenManager tokenManager;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        tokenManager = new TokenManager(this);
        bottomNav = findViewById(R.id.bottom_nav);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_dashboard) {
                loadFragment(new AdminDashboardFragment());
                return true;
                        } else if (id == R.id.nav_appointments) {
                loadFragment(new com.mad.techfix.ui.admin.appointments.AppointmentsFragment());
                return true;
            } else if (id == R.id.nav_branches) {
                loadFragment(new BranchListFragment());
                return true;
            } else if (id == R.id.nav_technicians) {
                loadFragment(new TechnicianListFragment());
                return true;
            }
            return false;
        });

        // DEBUG/TESTING ONLY: Inject the provided Admin Token so you don't have to login every time
        if (!tokenManager.isLoggedIn()) {
            String testToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIzMWQ4Nzg3OS05MjFmLTQ0YTctOGE4OS03MmQ5Mzc0MTA0MzEiLCJyb2xlIjoiQURNSU4iLCJlbWFpbCI6ImFkbWluQHRlY2hmaXgudGVzdCIsImlhdCI6MTc4NzQ4MDM1MiwiZXhwIjoxNzg4MDg1MTUyfQ.gdDjVdrchQMfZI0tOLf1SELNPFLRmNo_sEV4mwN3KRs";
            tokenManager.saveToken(testToken);
            tokenManager.saveUserId("31d87879-921f-44a7-8a89-72d937410431");
        }

        if (savedInstanceState == null) {
            bottomNav.setSelectedItemId(R.id.nav_dashboard);
        }
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void showLoginDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_admin_login, null);
        builder.setView(dialogView);
        builder.setCancelable(false); // Force them to login

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        TextInputEditText etEmail = dialogView.findViewById(R.id.et_login_email);
        TextInputEditText etPassword = dialogView.findViewById(R.id.et_login_password);
        Button btnSubmit = dialogView.findViewById(R.id.btn_login_submit);
        ProgressBar progress = dialogView.findViewById(R.id.progress_login);

        btnSubmit.setOnClickListener(v -> {
            String email = (etEmail.getText() != null ? etEmail.getText().toString().trim() : "").trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            // Show loading
            btnSubmit.setText("");
            progress.setVisibility(View.VISIBLE);
            btnSubmit.setEnabled(false);

            ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
            LoginRequest request = new LoginRequest(email, password);
            
            apiService.loginUser(request).enqueue(new Callback<AuthResponse>() {
                @Override
                public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                    btnSubmit.setText("Sign In");
                    progress.setVisibility(View.GONE);
                    btnSubmit.setEnabled(true);

                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        String token = response.body().getToken();
                        if (token != null && !token.isEmpty()) {
                            tokenManager.saveToken(token);
                            if (response.body().getUser() != null) {
                                tokenManager.saveUserId(response.body().getUser().getId());
                            }
                            dialog.dismiss();
                            Toast.makeText(AdminActivity.this, "Welcome to Admin Portal", Toast.LENGTH_SHORT).show();
                            // Load dashboard now that we have a token
                            bottomNav.setSelectedItemId(R.id.nav_dashboard);
                        } else {
                            Toast.makeText(AdminActivity.this, "Invalid token received", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        String errMsg = response.body() != null ? response.body().getMessage() : "Login failed";
                        Toast.makeText(AdminActivity.this, errMsg, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<AuthResponse> call, Throwable t) {
                    btnSubmit.setText("Sign In");
                    progress.setVisibility(View.GONE);
                    btnSubmit.setEnabled(true);
                    Toast.makeText(AdminActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}





