package com.mad.techfix.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import com.mad.techfix.R;
import com.mad.techfix.data.SessionManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Wait 2 seconds, then check login state
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                SessionManager sessionManager = new SessionManager(SplashActivity.this);

                if (sessionManager.isLoggedIn()) {
                    // TODO: Open Dashboard when created
                } else {
                     Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(intent);
                }
                finish();
            }
        }, 2000);
    }
}