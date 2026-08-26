package com.mad.techfix.models;

public class Payment {
    private String id;
    private String appointment_id;
    private double amount;
    private String payment_method; // CASH, CARD, ONLINE
    private String payment_status; // PENDING, PAID, FAILED
    private String paid_at;
    private String created_at;

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getAppointment_id() { return appointment_id; }
    public void setAppointment_id(String appointment_id) { this.appointment_id = appointment_id; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getPayment_method() { return payment_method; }
    public void setPayment_method(String payment_method) { this.payment_method = payment_method; }

    public String getPayment_status() { return payment_status; }
    public void setPayment_status(String payment_status) { this.payment_status = payment_status; }

    public String getPaid_at() { return paid_at; }
    public void setPaid_at(String paid_at) { this.paid_at = paid_at; }

    public String getCreated_at() { return created_at; }
    public void setCreated_at(String created_at) { this.created_at = created_at; }
}