package com.mad.techfix.data.local.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "repair_history")
public class RepairHistoryEntity {
    @PrimaryKey
    @NonNull
    public String repairId;
    public String appointmentNumber;
    public String device;
    public String service;
    public String status;
    public double price;
    public String date;
    public String branchName;

    // Constructor
    public RepairHistoryEntity(String repairId, String device, String service,
                               String status, double price, String date, String branchName) {
        this.repairId = repairId;
        this.device = device;
        this.service = service;
        this.status = status;
        this.price = price;
        this.date = date;
        this.branchName = branchName;
    }

    // Getters and Setters
    public String getRepairId() { return repairId; }
    public void setRepairId(String repairId) { this.repairId = repairId; }

    public String getAppointmentNumber() { return appointmentNumber; }
    public void setAppointmentNumber(String appointmentNumber) { this.appointmentNumber = appointmentNumber; }

    public String getDevice() { return device; }
    public void setDevice(String device) { this.device = device; }

    public String getService() { return service; }
    public void setService(String service) { this.service = service; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }
}