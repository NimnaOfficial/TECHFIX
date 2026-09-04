package com.mad.techfix.data;

import android.content.Context;
import android.content.SharedPreferences;
import com.mad.techfix.models.User;

public class SessionManager {
    private static final String PREF_NAME = "TechFixSession";
    private static final String KEY_USER_ID = "USER_ID";
    private static final String KEY_USER_NAME = "USER_NAME";
    private static final String KEY_USER_EMAIL = "USER_EMAIL";
    private static final String KEY_USER_PHONE = "USER_PHONE";
    private static final String KEY_USER_ROLE = "USER_ROLE";
    private static final String KEY_AUTH_TOKEN = "AUTH_TOKEN";
    private static final String KEY_IS_LOGGED_IN = "IS_LOGGED_IN";
    private static final String KEY_THEME_DARK = "THEME_DARK";

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void setDarkMode(boolean isDark) {
        prefs.edit().putBoolean(KEY_THEME_DARK, isDark).apply();
    }

    public boolean isDarkMode() {
        return prefs.getBoolean(KEY_THEME_DARK, false);
    }

    public void saveAuthSession(String token, User user) {
        SharedPreferences.Editor editor = prefs.edit();
        if (token != null) {
            editor.putString(KEY_AUTH_TOKEN, token);
        }
        if (user != null) {
            editor.putString(KEY_USER_ID, user.getId());
            editor.putString(KEY_USER_NAME, user.getFullName());
            editor.putString(KEY_USER_EMAIL, user.getEmail());
            editor.putString(KEY_USER_PHONE, user.getPhone());
            editor.putString(KEY_USER_ROLE, user.getRole());
        }
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    public void saveUserSession(String userId, String email, String role) {
        saveUserSession(userId, email, role, null, null, null);
    }

    public void saveUserSession(String userId, String email, String role, String token, String fullName, String phone) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_ROLE, role);
        if (token != null) {
            editor.putString(KEY_AUTH_TOKEN, token);
        }
        if (fullName != null) {
            editor.putString(KEY_USER_NAME, fullName);
        }
        if (phone != null) {
            editor.putString(KEY_USER_PHONE, phone);
        }
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false) && getAuthToken() != null && !getAuthToken().isEmpty();
    }

    public String getAuthToken() {
        return prefs.getString(KEY_AUTH_TOKEN, "");
    }

    public String getBearerToken() {
        String token = getAuthToken();
        if (token == null || token.isEmpty()) return "";
        return token.startsWith("Bearer ") ? token : "Bearer " + token;
    }

    public String getUserId() {
        return prefs.getString(KEY_USER_ID, "");
    }

    public String getUserName() {
        return prefs.getString(KEY_USER_NAME, "User");
    }

    public String getUserEmail() {
        return prefs.getString(KEY_USER_EMAIL, "");
    }

    public String getUserPhone() {
        return prefs.getString(KEY_USER_PHONE, "");
    }

    public String getUserRole() {
        return prefs.getString(KEY_USER_ROLE, "CUSTOMER");
    }

    public void clearSession() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }
}
