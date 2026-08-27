package com.mad.techfix.models.admin;

import com.google.gson.annotations.SerializedName;

public class Technician {
    private String id;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("employee_code")
    private String employeeCode;

    private String specialization;

    @SerializedName("availability_status")
    private String availabilityStatus;

    @SerializedName("branch_id")
    private String branchId;

    @SerializedName("hire_date")
    private String hireDate;

    @SerializedName("is_active")
    private int isActive;

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("last_name")
    private String lastName;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getEmployeeCode() { return employeeCode; }
    public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getAvailabilityStatus() { return availabilityStatus; }
    public void setAvailabilityStatus(String availabilityStatus) { this.availabilityStatus = availabilityStatus; }

    public String getBranchId() { return branchId; }
    public void setBranchId(String branchId) { this.branchId = branchId; }

    public String getHireDate() { return hireDate; }
    public void setHireDate(String hireDate) { this.hireDate = hireDate; }

    public int getIsActive() { return isActive; }
    public void setIsActive(int isActive) { this.isActive = isActive; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFullName() {
        String f = firstName != null ? firstName : "";
        String l = lastName != null ? lastName : "";
        return (f + " " + l).trim();
    }
}
