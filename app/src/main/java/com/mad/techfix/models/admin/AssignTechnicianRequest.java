package com.mad.techfix.models.admin;

import com.google.gson.annotations.SerializedName;

public class AssignTechnicianRequest {
    @SerializedName("technician_id")
    private String technicianId;

    public AssignTechnicianRequest(String technicianId) {
        this.technicianId = technicianId;
    }

    public String getTechnicianId() {
        return technicianId;
    }

    public void setTechnicianId(String technicianId) {
        this.technicianId = technicianId;
    }
}
