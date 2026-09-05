package com.mad.techfix.models.admin;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Response model for GET /api/appointments/{id}/eligible-technicians
 * Contains recommended technicians (skill-matched) and other available technicians (fallback).
 */
public class EligibleTechniciansResponse {

    @SerializedName("recommended")
    private List<Technician> recommended;

    @SerializedName("other_available")
    private List<Technician> otherAvailable;

    @SerializedName("appointment_branch_id")
    private String appointmentBranchId;

    @SerializedName("appointment_service_id")
    private String appointmentServiceId;

    public List<Technician> getRecommended() {
        return recommended;
    }

    public void setRecommended(List<Technician> recommended) {
        this.recommended = recommended;
    }

    public List<Technician> getOtherAvailable() {
        return otherAvailable;
    }

    public void setOtherAvailable(List<Technician> otherAvailable) {
        this.otherAvailable = otherAvailable;
    }

    public String getAppointmentBranchId() {
        return appointmentBranchId;
    }

    public void setAppointmentBranchId(String appointmentBranchId) {
        this.appointmentBranchId = appointmentBranchId;
    }

    public String getAppointmentServiceId() {
        return appointmentServiceId;
    }

    public void setAppointmentServiceId(String appointmentServiceId) {
        this.appointmentServiceId = appointmentServiceId;
    }
}
