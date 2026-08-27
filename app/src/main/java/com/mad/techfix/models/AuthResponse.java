package com.mad.techfix.models;

public class AuthResponse {
    private String token;
    private User user;
    private String message;

    public String getToken() { return token; }
    public User getUser() { return user; }
    public String getMessage() { return message; }
}
