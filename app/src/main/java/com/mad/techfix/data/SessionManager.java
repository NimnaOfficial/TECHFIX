package com.mad.techfix.data;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "TechFixSession";
    private static final String KEY_USER_ID = "USER_ID";
    private static final String KEY_USER_EMAIL = "USER_EMAIL";
    private static final String KEY_USER_ROLE = "USER_ROLE";
    private static final String KEY_IS_LOGGED_IN = "IS_LOGGED_IN";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveUserSession(String userId, String email, String role) {
        editor = prefs.edit();
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_ROLE, role);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getUserRole() {
        return prefs.getString(KEY_USER_ROLE, "CUSTOMER");
    }

    public void clearSession() {
        editor = prefs.edit();
        editor.clear();
        editor.apply();
    }
}