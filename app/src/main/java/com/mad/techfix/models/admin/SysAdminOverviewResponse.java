package com.mad.techfix.models.admin;

import com.google.gson.annotations.SerializedName;

public class SysAdminOverviewResponse {
    @SerializedName("system_health")
    private String systemHealth;

    @SerializedName("total_users")
    private int totalUsers;

    @SerializedName("total_managers")
    private int totalManagers;

    @SerializedName("total_customers")
    private int totalCustomers;

    @SerializedName("total_technicians")
    private int totalTechnicians;

    public String getSystemHealth() { return systemHealth; }
    public void setSystemHealth(String systemHealth) { this.systemHealth = systemHealth; }

    public int getTotalUsers() { return totalUsers; }
    public void setTotalUsers(int totalUsers) { this.totalUsers = totalUsers; }

    public int getTotalManagers() { return totalManagers; }
    public void setTotalManagers(int totalManagers) { this.totalManagers = totalManagers; }

    public int getTotalCustomers() { return totalCustomers; }
    public void setTotalCustomers(int totalCustomers) { this.totalCustomers = totalCustomers; }

    public int getTotalTechnicians() { return totalTechnicians; }
    public void setTotalTechnicians(int totalTechnicians) { this.totalTechnicians = totalTechnicians; }
}
