package com.mad.techfix.models;

public class SparePart {
    private String id;
    private String name;
    private String part_number;
    private String description;
    private double unit_price;
    private int minimum_stock;
    private int is_active;  // CHANGED from boolean to int (0 or 1)

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPart_number() { return part_number; }
    public void setPart_number(String part_number) { this.part_number = part_number; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getUnit_price() { return unit_price; }
    public void setUnit_price(double unit_price) { this.unit_price = unit_price; }

    public int getMinimum_stock() { return minimum_stock; }
    public void setMinimum_stock(int minimum_stock) { this.minimum_stock = minimum_stock; }

    public int getIs_active() { return is_active; }
    public void setIs_active(int is_active) { this.is_active = is_active; }
}