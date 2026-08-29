package com.mad.techfix.data.local.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "local_appointments")
public class AppointmentEntity {
    @PrimaryKey
    @NonNull
    public String id;
    public String appointment_number;
    public String customer_id;
    public String device_id;
    public String service_id;
    public String branch_id;
    public String technician_id;
    public String status;
    public String requested_date;
    public String requested_time;
    public double estimated_price;
    public double final_price;
    public String created_at;

    public AppointmentEntity(@NonNull String id, String appointment_number, String customer_id, String device_id, String service_id, String branch_id, String technician_id, String status, String requested_date, String requested_time, double estimated_price, double final_price, String created_at) {
        this.id = id;
        this.appointment_number = appointment_number;
        this.customer_id = customer_id;
        this.device_id = device_id;
        this.service_id = service_id;
        this.branch_id = branch_id;
        this.technician_id = technician_id;
        this.status = status;
        this.requested_date = requested_date;
        this.requested_time = requested_time;
        this.estimated_price = estimated_price;
        this.final_price = final_price;
        this.created_at = created_at;
    }
}
