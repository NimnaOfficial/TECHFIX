package com.mad.techfix.models;

public class AuthResponse {
    private boolean success;
    private String token;
    private String token_type;
    private long expires_in;
    private User user;
    private String message;

    public boolean isSuccess() { return success; }
    public String getToken() { return token; }
    public String getToken_type() { return token_type; }
    public long getExpires_in() { return expires_in; }
    public User getUser() { return user; }
    public String getMessage() { return message; }

    public void setSuccess(boolean success) { this.success = success; }
    public void setToken(String token) { this.token = token; }
    public void setUser(User user) { this.user = user; }
    public void setMessage(String message) { this.message = message; }
}
