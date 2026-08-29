package com.mad.techfix.data.local.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "local_branches")
public class BranchEntity {
    @PrimaryKey
    @NonNull
    public String id;
    
    public String name;
    public String address;
    public String city;
    public double latitude;
    public double longitude;
    public String phone;
    public String email;
    public String openingTime;
    public String closingTime;

    public BranchEntity(@NonNull String id, String name, String address, String city, double latitude, double longitude, String phone, String email, String openingTime, String closingTime) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
        this.phone = phone;
        this.email = email;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
    }
}
