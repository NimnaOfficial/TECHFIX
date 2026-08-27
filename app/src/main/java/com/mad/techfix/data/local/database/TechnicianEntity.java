package com.mad.techfix.data.local.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "local_technicians")
public class TechnicianEntity {
    @PrimaryKey
    @NonNull
    public String id;
    
    public String employeeCode;
    public String firstName;
    public String lastName;
    public String specialization;
    public String status;
    public String branchId;
    public String branchName;

    public TechnicianEntity(@NonNull String id, String employeeCode, String firstName, String lastName, String specialization, String status, String branchId, String branchName) {
        this.id = id;
        this.employeeCode = employeeCode;
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialization = specialization;
        this.status = status;
        this.branchId = branchId;
        this.branchName = branchName;
    }
}
