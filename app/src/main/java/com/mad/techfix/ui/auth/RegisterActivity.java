package com.mad.techfix.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mad.techfix.R;
import com.mad.techfix.models.AuthResponse;
import com.mad.techfix.models.RegisterRequest;
import com.mad.techfix.network.RetrofitClient;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout tilTitle, tilFirstName, tilLastName, tilEmail;
    private TextInputLayout tilCountryCode, tilPhone, tilCity;
    private TextInputLayout tilPassword, tilConfirmPassword;

    private AutoCompleteTextView actTitle, actCountryCode, actCity;
    private TextInputEditText etFirstName, etLastName, etEmail, etPhone, etPassword, etConfirmPassword;
    private CheckBox cbTerms;
    private MaterialButton btnRegister;
    private ProgressBar progressBar;
    private TextView tvLogin;

    private static final String[] TITLES = {"Mr.", "Mrs.", "Ms.", "Dr.", "Eng."};
    private static final String[] COUNTRY_CODES = {"+94 (LK)", "+1 (US)", "+44 (UK)", "+61 (AU)", "+971 (UAE)"};
    private static final String[] CITIES = {
            "Colombo", "Galle", "Kandy", "Gampaha", "Negombo",
            "Matara", "Kurunegala", "Kalutara", "Jaffna", "Ratnapura",
            "Batticaloa", "Anuradhapura", "Badulla", "Trincomalee", "Nuwara Eliya"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        setupDropdowns();
        setupListeners();
    }

    private void initViews() {
        tilTitle = findViewById(R.id.tilTitle);
        tilFirstName = findViewById(R.id.tilFirstName);
        tilLastName = findViewById(R.id.tilLastName);
        tilEmail = findViewById(R.id.tilEmail);
        tilCountryCode = findViewById(R.id.tilCountryCode);
        tilPhone = findViewById(R.id.tilPhone);
        tilCity = findViewById(R.id.tilCity);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);

        actTitle = findViewById(R.id.actTitle);
        actCountryCode = findViewById(R.id.actCountryCode);
        actCity = findViewById(R.id.actCity);

        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        cbTerms = findViewById(R.id.cbTerms);
        btnRegister = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.progressBar);
        tvLogin = findViewById(R.id.tvLogin);
    }

    private void setupDropdowns() {
        ArrayAdapter<String> titleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, TITLES);
        actTitle.setAdapter(titleAdapter);
        actTitle.setText(TITLES[0], false);

        ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, COUNTRY_CODES);
        actCountryCode.setAdapter(countryAdapter);
        actCountryCode.setText(COUNTRY_CODES[0], false);

        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, CITIES);
        actCity.setAdapter(cityAdapter);
        actCity.setText(CITIES[0], false);
    }

    private void setupListeners() {
        btnRegister.setOnClickListener(v -> performRegister());
        tvLogin.setOnClickListener(v -> finish());
    }

    private void performRegister() {
        // Clear previous error states
        tilFirstName.setError(null);
        tilLastName.setError(null);
        tilEmail.setError(null);
        tilPhone.setError(null);
        tilCity.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);

        String firstName = etFirstName.getText() != null ? etFirstName.getText().toString().trim() : "";
        String lastName = etLastName.getText() != null ? etLastName.getText().toString().trim() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String rawPhone = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";
        String selectedCity = actCity.getText() != null ? actCity.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString() : "";
        String confirmPassword = etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString() : "";

        boolean hasError = false;

        // 1. Validate First Name
        if (TextUtils.isEmpty(firstName)) {
            tilFirstName.setError("First name is required");
            hasError = true;
        } else if (firstName.length() < 2) {
            tilFirstName.setError("First name must be at least 2 characters");
            hasError = true;
        } else if (!firstName.matches("^[a-zA-Z\\s]+$")) {
            tilFirstName.setError("First name must contain letters only");
            hasError = true;
        }

        // 2. Validate Last Name
        if (TextUtils.isEmpty(lastName)) {
            tilLastName.setError("Last name is required");
            hasError = true;
        } else if (lastName.length() < 2) {
            tilLastName.setError("Last name must be at least 2 characters");
            hasError = true;
        } else if (!lastName.matches("^[a-zA-Z\\s]+$")) {
            tilLastName.setError("Last name must contain letters only");
            hasError = true;
        }

        // 3. Validate Email
        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Email address is required");
            hasError = true;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Please enter a valid email format (e.g. user@domain.com)");
            hasError = true;
        }

        // 4. Validate Numeric Phone Number (9-10 digits)
        if (TextUtils.isEmpty(rawPhone)) {
            tilPhone.setError("Phone number is required");
            hasError = true;
        } else if (!rawPhone.matches("^[0-9]{9,10}$")) {
            tilPhone.setError("Enter a valid 9 or 10-digit numeric phone number");
            hasError = true;
        }

        // 5. Validate Customer City Selection
        if (TextUtils.isEmpty(selectedCity)) {
            tilCity.setError("Please select your residential city");
            hasError = true;
        }

        // 6. Validate Password Strength (Min 8 chars, letter + digit)
        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("Password is required");
            hasError = true;
        } else if (password.length() < 8) {
            tilPassword.setError("Password must contain at least 8 characters");
            hasError = true;
        } else if (!password.matches("^(?=.*[A-Za-z])(?=.*\\d).+$")) {
            tilPassword.setError("Password must contain both letters and digits");
            hasError = true;
        }

        // 7. Validate Confirm Password Match
        if (TextUtils.isEmpty(confirmPassword)) {
            tilConfirmPassword.setError("Please confirm your password");
            hasError = true;
        } else if (!password.equals(confirmPassword)) {
            tilConfirmPassword.setError("Passwords do not match");
            hasError = true;
        }

        // 8. Validate Terms Acceptance
        if (!cbTerms.isChecked()) {
            Toast.makeText(this, "Please agree to the Terms of Service to continue", Toast.LENGTH_SHORT).show();
            hasError = true;
        }

        if (hasError) return;

        setLoading(true);

        // Registration is strictly for CUSTOMER accounts with selected Customer City
        final RegisterRequest request = new RegisterRequest(firstName, lastName, email, password, rawPhone, selectedCity, "CUSTOMER");

        RetrofitClient.getApiService().registerUser(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                setLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(RegisterActivity.this,
                            "Welcome, " + firstName + "! Customer account created successfully.",
                            Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    intent.putExtra("registered_email", email);
                    startActivity(intent);
                    finish();
                } else {
                    String errorMsg = "Registration failed";
                    try {
                        if (response.errorBody() != null) {
                            String errStr = response.errorBody().string();
                            JSONObject json = new JSONObject(errStr);
                            if (json.has("message")) {
                                errorMsg = json.getString("message");
                            }
                        }
                    } catch (Exception ignored) {}

                    if (response.code() == 409) {
                        tilEmail.setError("This email is already registered in the system");
                    }
                    Toast.makeText(RegisterActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                setLoading(false);
                Toast.makeText(RegisterActivity.this, "Network Connection Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setLoading(boolean isLoading) {
        if (isLoading) {
            btnRegister.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            btnRegister.setEnabled(true);
            progressBar.setVisibility(View.GONE);
        }
    }
}
