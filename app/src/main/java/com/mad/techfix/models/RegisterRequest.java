package com.mad.techfix.models;

public class RegisterRequest {
    private String first_name;
    private String last_name;
    private String email;
    private String password;
    private String phone;
    private String city;
    private String role;

    public RegisterRequest(String first_name, String last_name, String email, String password, String phone) {
        this(first_name, last_name, email, password, phone, null, "CUSTOMER");
    }

    public RegisterRequest(String first_name, String last_name, String email, String password, String phone, String city) {
        this(first_name, last_name, email, password, phone, city, "CUSTOMER");
    }

    public RegisterRequest(String first_name, String last_name, String email, String password, String phone, String city, String role) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.city = city;
        this.role = role != null ? role : "CUSTOMER";
    }

    public String getFirst_name() { return first_name; }
    public String getLast_name() { return last_name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getPhone() { return phone; }
    public String getCity() { return city; }
    public String getRole() { return role; }
}
