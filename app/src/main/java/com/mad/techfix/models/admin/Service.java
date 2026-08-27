package com.mad.techfix.models.admin;

import com.google.gson.annotations.SerializedName;

public class Service {
    private String id;

    @SerializedName("category_id")
    private String categoryId;

    private String name;
    private String description;

    @SerializedName("estimated_days")
    private int estimatedDays;

    @SerializedName("base_price")
    private double basePrice;

    @SerializedName("is_active")
    private int isActive;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getEstimatedDays() { return estimatedDays; }
    public void setEstimatedDays(int estimatedDays) { this.estimatedDays = estimatedDays; }

    public double getBasePrice() { return basePrice; }
    public void setBasePrice(double basePrice) { this.basePrice = basePrice; }

    public int getIsActive() { return isActive; }
    public void setIsActive(int isActive) { this.isActive = isActive; }
}
