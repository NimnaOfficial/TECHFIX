package com.mad.techfix.models;

public class LoginRequest {
    private String email;
    private String password;

    // Constructor (this is used to create the object)
    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getters and Setters (so Retrofit can read the data)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}