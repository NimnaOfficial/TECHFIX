package com.mad.techfix.models;

import com.google.gson.annotations.SerializedName;

public class CreateAppointmentRequest {

    @SerializedName("device_id")
    private final String deviceId;

    @SerializedName("service_id")
    private final String serviceId;

    @SerializedName("branch_id")
    private final String branchId;

    @SerializedName("requested_date")
    private final String requestedDate;

    @SerializedName("requested_time")
    private final String requestedTime;

    @SerializedName("problem_description")
    private final String problemDescription;

    @SerializedName("customer_latitude")
    private final double customerLatitude;

    @SerializedName("customer_longitude")
    private final double customerLongitude;


    public CreateAppointmentRequest(
            String deviceId,
            String serviceId,
            String branchId,
            String requestedDate,
            String requestedTime,
            String problemDescription,
            double customerLatitude,
            double customerLongitude
    ) {

        this.deviceId = deviceId;
        this.serviceId = serviceId;
        this.branchId = branchId;
        this.requestedDate = requestedDate;
        this.requestedTime = requestedTime;
        this.problemDescription = problemDescription;
        this.customerLatitude = customerLatitude;
        this.customerLongitude = customerLongitude;
    }


    public String getDeviceId() {
        return deviceId;
    }


    public String getServiceId() {
        return serviceId;
    }


    public String getBranchId() {
        return branchId;
    }


    public String getRequestedDate() {
        return requestedDate;
    }


    public String getRequestedTime() {
        return requestedTime;
    }


    public String getProblemDescription() {
        return problemDescription;
    }


    public double getCustomerLatitude() {
        return customerLatitude;
    }


    public double getCustomerLongitude() {
        return customerLongitude;
    }
}