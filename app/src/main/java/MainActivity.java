package com.mad.techfix;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.mad.techfix.models.LoginRequest;
import com.mad.techfix.models.AuthResponse;
import com.mad.techfix.network.ApiService;
import com.mad.techfix.network.RetrofitClient;
import com.mad.techfix.ui.camera.CameraFragment;
import com.mad.techfix.utils.TokenManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private TokenManager tokenManager;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tokenManager = new TokenManager(this);
        apiService = RetrofitClient.getClient().create(ApiService.class);

        if (tokenManager.getToken() != null) {
            loadCameraFragment();
            return;
        }

        String testEmail = "admin@techfix.test";
        String testPassword = "Admin123!";

        LoginRequest loginRequest = new LoginRequest(testEmail, testPassword);

        Toast.makeText(this, "🔐 Logging in as ADMIN...", Toast.LENGTH_SHORT).show();

        apiService.loginUser(loginRequest).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    String token = response.body().getToken();
                    tokenManager.saveToken(token);
                    if (response.body().getUser() != null) {
                        tokenManager.saveUserId(response.body().getUser().getId());
                    }

                    Toast.makeText(MainActivity.this, "✅ Login successful! Loading Camera...", Toast.LENGTH_SHORT).show();
                    loadCameraFragment();

                } else {
                    String errorMsg = "Login failed! Check credentials.";
                    try {
                        if (response.errorBody() != null) {
                            errorMsg = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(MainActivity.this, "❌ " + errorMsg, Toast.LENGTH_LONG).show();
                    loadCameraFragment();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "⚠️ Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                loadCameraFragment();
            }
        });
    }

    private void loadCameraFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new CameraFragment())
                .commit();
    }
}