package com.mad.techfix;

import android.app.Application;
import androidx.appcompat.app.AppCompatDelegate;
import com.mad.techfix.data.SessionManager;

public class TechFixApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        
        SessionManager sessionManager = new SessionManager(this);
        if (sessionManager.isDarkMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}
