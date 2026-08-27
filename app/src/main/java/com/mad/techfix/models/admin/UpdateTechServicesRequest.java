package com.mad.techfix.models.admin;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class UpdateTechServicesRequest {
    @SerializedName("service_ids")
    private List<String> serviceIds;

    public UpdateTechServicesRequest(List<String> serviceIds) {
        this.serviceIds = serviceIds;
    }

    public List<String> getServiceIds() {
        return serviceIds;
    }

    public void setServiceIds(List<String> serviceIds) {
        this.serviceIds = serviceIds;
    }
}
