package com.mad.techfix.models;

public class LoginResponse {
    private boolean success;
    private String message;
    private String token;
    private String token_type;
    private int expires_in;
    private User user;

    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getToken_type() { return token_type; }
    public void setToken_type(String token_type) { this.token_type = token_type; }

    public int getExpires_in() { return expires_in; }
    public void setExpires_in(int expires_in) { this.expires_in = expires_in; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}