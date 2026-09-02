package com.mad.techfix.models;

import com.google.gson.annotations.SerializedName;

public class Device {

    private String id;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("category_id")
    private String categoryId;

    private String brand;
    private String model;

    @SerializedName("serial_number")
    private String serialNumber;

    @SerializedName("purchase_year")
    private Integer purchaseYear;

    private String notes;

    @SerializedName("category_name")
    private String categoryName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Integer getPurchaseYear() {
        return purchaseYear;
    }

    public void setPurchaseYear(Integer purchaseYear) {
        this.purchaseYear = purchaseYear;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDisplayName() {
        String safeBrand = brand == null ? "" : brand;
        String safeModel = model == null ? "" : model;
        return (safeBrand + " " + safeModel).trim();
    }
}