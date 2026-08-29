package com.mad.techfix.data.local.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "local_dashboard_metrics")
public class DashboardMetricsEntity {
    @PrimaryKey
    public int id = 1;
    public double totalRevenue;
    public int pendingRequests;
    public int activeRepairs;
    public int availableTechnicians;
    public int totalAppointments;
    public int totalTechnicians;

    public DashboardMetricsEntity(double totalRevenue, int pendingRequests, int activeRepairs, int availableTechnicians, int totalAppointments, int totalTechnicians) {
        this.totalRevenue = totalRevenue;
        this.pendingRequests = pendingRequests;
        this.activeRepairs = activeRepairs;
        this.availableTechnicians = availableTechnicians;
        this.totalAppointments = totalAppointments;
        this.totalTechnicians = totalTechnicians;
    }
}
