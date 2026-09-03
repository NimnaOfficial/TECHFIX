package com.mad.techfix.models;

public class RepairImage {
    private String id;
    private String appointment_id;
    private String image_url;
    private String image_type;
    private String uploaded_by;
    private String created_at;

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getAppointment_id() { return appointment_id; }
    public void setAppointment_id(String appointment_id) { this.appointment_id = appointment_id; }

    public String getImage_url() { return image_url; }
    public void setImage_url(String image_url) { this.image_url = image_url; }

    public String getImage_type() { return image_type; }
    public void setImage_type(String image_type) { this.image_type = image_type; }

    public String getUploaded_by() { return uploaded_by; }
    public void setUploaded_by(String uploaded_by) { this.uploaded_by = uploaded_by; }

    public String getCreated_at() { return created_at; }
    public void setCreated_at(String created_at) { this.created_at = created_at; }
}