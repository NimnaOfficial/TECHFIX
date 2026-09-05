package com.mad.techfix.models.admin;

import com.google.gson.annotations.SerializedName;

public class DashboardResponse {
    
    public static class DashboardData {
        @SerializedName("total_revenue")
        private double totalRevenue;

        @SerializedName("pending_requests")
        private int pendingRequests;

        @SerializedName("active_repairs")
        private int activeRepairs;

        @SerializedName("available_technicians")
        private int availableTechnicians;

        @SerializedName("total_appointments")
        private int totalAppointments;

        @SerializedName("total_technicians")
        private int totalTechnicians;

        @SerializedName("branch_name")
        private String branchName;

        public String getBranchName() { return branchName; }
        public void setBranchName(String branchName) { this.branchName = branchName; }

        public double getTotalRevenue() { return totalRevenue; }
        public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }

        public int getPendingRequests() { return pendingRequests; }
        public void setPendingRequests(int pendingRequests) { this.pendingRequests = pendingRequests; }

        public int getActiveRepairs() { return activeRepairs; }
        public void setActiveRepairs(int activeRepairs) { this.activeRepairs = activeRepairs; }

        public int getAvailableTechnicians() { return availableTechnicians; }
        public void setAvailableTechnicians(int availableTechnicians) { this.availableTechnicians = availableTechnicians; }

        public int getTotalAppointments() { return totalAppointments; }
        public void setTotalAppointments(int totalAppointments) { this.totalAppointments = totalAppointments; }

        public int getTotalTechnicians() { return totalTechnicians; }
        public void setTotalTechnicians(int totalTechnicians) { this.totalTechnicians = totalTechnicians; }
    }
}
