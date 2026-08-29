package com.mad.techfix.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;

import com.mad.techfix.MainActivity;
import com.mad.techfix.R;
import com.mad.techfix.data.SessionManager;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION_MS = 1800;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            SessionManager sessionManager = new SessionManager(SplashActivity.this);

            Intent nextIntent;
            if (sessionManager.isLoggedIn()) {
                nextIntent = new Intent(SplashActivity.this, MainActivity.class);
            } else {
                nextIntent = new Intent(SplashActivity.this, LoginActivity.class);
            }

            startActivity(nextIntent);
            finish();
        }, SPLASH_DURATION_MS);
    }
}
