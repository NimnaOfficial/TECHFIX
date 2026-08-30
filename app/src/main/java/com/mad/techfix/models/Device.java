package com.mad.techfix.models;

public class Device {
    private String id;
    private String user_id;
    private String category_id;
    private String brand;
    private String model;
    private String serial_number;
    private Integer purchase_year;
    private String notes;

    // Default constructor
    public Device() {}

    // Add getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getUserId() { return user_id; }
    public void setUserId(String user_id) { this.user_id = user_id; }

    public String getCategoryId() { return category_id; }
    public void setCategoryId(String category_id) { this.category_id = category_id; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getSerialNumber() { return serial_number; }
    public void setSerialNumber(String serial_number) { this.serial_number = serial_number; }

    public Integer getPurchaseYear() { return purchase_year; }
    public void setPurchaseYear(Integer purchase_year) { this.purchase_year = purchase_year; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
