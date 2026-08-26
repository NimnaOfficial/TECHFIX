package com.mad.techfix.models;

public class User {
    private String id;
    private String first_name;
    private String last_name;
    private String email;
    private String phone;
    private String role;
    private String profile_image_url;
    private int is_active;

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFirst_name() { return first_name; }
    public void setFirst_name(String first_name) { this.first_name = first_name; }

    public String getLast_name() { return last_name; }
    public void setLast_name(String last_name) { this.last_name = last_name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getProfile_image_url() { return profile_image_url; }
    public void setProfile_image_url(String profile_image_url) { this.profile_image_url = profile_image_url; }

    public int getIs_active() { return is_active; }
    public void setIs_active(int is_active) { this.is_active = is_active; }

    // Helper method to get full name
    public String getFullName() {
        return first_name + " " + last_name;
    }
}