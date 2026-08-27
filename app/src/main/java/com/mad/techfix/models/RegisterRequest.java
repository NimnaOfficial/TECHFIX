package com.mad.techfix.models;

public class RegisterRequest {
    private String first_name;
    private String last_name;
    private String email;
    private String password;
    private String phone;

    public RegisterRequest(String first_name, String last_name, String email, String password, String phone) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.password = password;
        this.phone = phone;
    }

    public String getFirst_name() { return first_name; }
    public String getLast_name() { return last_name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getPhone() { return phone; }
}