package com.mad.techfix.models;

public class Appointment {
    private String id;
    private String appointment_number;
    private String customer_id;
    private String device_id;
    private String service_id;
    private String branch_id;
    private String technician_id;
    private String status;
    private String requested_date;
    private String requested_time;
    private double estimated_price;
    private Double final_price;
    private String created_at;
    private String updated_at;

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getAppointment_number() { return appointment_number; }
    public void setAppointment_number(String appointment_number) { this.appointment_number = appointment_number; }

    public String getCustomer_id() { return customer_id; }
    public void setCustomer_id(String customer_id) { this.customer_id = customer_id; }

    public String getDevice_id() { return device_id; }
    public void setDevice_id(String device_id) { this.device_id = device_id; }

    public String getService_id() { return service_id; }
    public void setService_id(String service_id) { this.service_id = service_id; }

    public String getBranch_id() { return branch_id; }
    public void setBranch_id(String branch_id) { this.branch_id = branch_id; }

    public String getTechnician_id() { return technician_id; }
    public void setTechnician_id(String technician_id) { this.technician_id = technician_id; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRequested_date() { return requested_date; }
    public void setRequested_date(String requested_date) { this.requested_date = requested_date; }

    public String getRequested_time() { return requested_time; }
    public void setRequested_time(String requested_time) { this.requested_time = requested_time; }

    public double getEstimated_price() { return estimated_price; }
    public void setEstimated_price(double estimated_price) { this.estimated_price = estimated_price; }

    public Double getFinal_price() { return final_price; }
    public void setFinal_price(Double final_price) { this.final_price = final_price; }

    public String getCreated_at() { return created_at; }
    public void setCreated_at(String created_at) { this.created_at = created_at; }

    public String getUpdated_at() { return updated_at; }
    public void setUpdated_at(String updated_at) { this.updated_at = updated_at; }
}