package com.mad.techfix.models;

public class Appointment {

    private String id;
    private String appointment_number;

    private String customer_id;
    private String device_id;
    private String service_id;
    private String branch_id;
    private String technician_id;

    private String status;

    private String requested_date;
    private String requested_time;

    private String problem_description;

    private double estimated_price;
    private Double final_price;

    private Double customer_latitude;
    private Double customer_longitude;

    private String created_at;
    private String updated_at;


    // Joined device fields
    private String device_brand;
    private String device_model;
    private String device_serial_number;
    private String device_name;


    // Joined service fields
    private String service_name;
    private String service_description;
    private Double service_base_price;


    // Joined branch fields
    private String branch_name;
    private String branch_city;
    private String branch_address;


    // Joined customer fields
    private String customer_first_name;
    private String customer_last_name;
    private String customer_name;


    // Joined technician fields
    private String technician_first_name;
    private String technician_last_name;
    private String technician_name;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getAppointment_number() {
        return appointment_number;
    }

    public void setAppointment_number(
            String appointment_number
    ) {
        this.appointment_number =
                appointment_number;
    }


    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(
            String customer_id
    ) {
        this.customer_id =
                customer_id;
    }


    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(
            String device_id
    ) {
        this.device_id =
                device_id;
    }


    public String getService_id() {
        return service_id;
    }

    public void setService_id(
            String service_id
    ) {
        this.service_id =
                service_id;
    }


    public String getBranch_id() {
        return branch_id;
    }

    public void setBranch_id(
            String branch_id
    ) {
        this.branch_id =
                branch_id;
    }


    public String getTechnician_id() {
        return technician_id;
    }

    public void setTechnician_id(
            String technician_id
    ) {
        this.technician_id =
                technician_id;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(
            String status
    ) {
        this.status =
                status;
    }


    public String getRequested_date() {
        return requested_date;
    }

    public void setRequested_date(
            String requested_date
    ) {
        this.requested_date =
                requested_date;
    }


    public String getRequested_time() {
        return requested_time;
    }

    public void setRequested_time(
            String requested_time
    ) {
        this.requested_time =
                requested_time;
    }


    public String getProblem_description() {
        return problem_description;
    }

    public void setProblem_description(
            String problem_description
    ) {
        this.problem_description =
                problem_description;
    }


    public double getEstimated_price() {
        return estimated_price;
    }

    public void setEstimated_price(
            double estimated_price
    ) {
        this.estimated_price =
                estimated_price;
    }


    public Double getFinal_price() {
        return final_price;
    }

    public void setFinal_price(
            Double final_price
    ) {
        this.final_price =
                final_price;
    }


    public Double getCustomer_latitude() {
        return customer_latitude;
    }

    public void setCustomer_latitude(
            Double customer_latitude
    ) {
        this.customer_latitude =
                customer_latitude;
    }


    public Double getCustomer_longitude() {
        return customer_longitude;
    }

    public void setCustomer_longitude(
            Double customer_longitude
    ) {
        this.customer_longitude =
                customer_longitude;
    }


    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(
            String created_at
    ) {
        this.created_at =
                created_at;
    }


    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(
            String updated_at
    ) {
        this.updated_at =
                updated_at;
    }


    public String getDevice_brand() {
        return device_brand;
    }

    public void setDevice_brand(
            String device_brand
    ) {
        this.device_brand =
                device_brand;
    }


    public String getDevice_model() {
        return device_model;
    }

    public void setDevice_model(
            String device_model
    ) {
        this.device_model =
                device_model;
    }


    public String getDevice_serial_number() {
        return device_serial_number;
    }

    public void setDevice_serial_number(
            String device_serial_number
    ) {
        this.device_serial_number =
                device_serial_number;
    }


    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(
            String device_name
    ) {
        this.device_name =
                device_name;
    }


    public String getService_name() {
        return service_name;
    }

    public void setService_name(
            String service_name
    ) {
        this.service_name =
                service_name;
    }


    public String getService_description() {
        return service_description;
    }

    public void setService_description(
            String service_description
    ) {
        this.service_description =
                service_description;
    }


    public Double getService_base_price() {
        return service_base_price;
    }

    public void setService_base_price(
            Double service_base_price
    ) {
        this.service_base_price =
                service_base_price;
    }


    public String getBranch_name() {
        return branch_name;
    }

    public void setBranch_name(
            String branch_name
    ) {
        this.branch_name =
                branch_name;
    }


    public String getBranch_city() {
        return branch_city;
    }

    public void setBranch_city(
            String branch_city
    ) {
        this.branch_city =
                branch_city;
    }


    public String getBranch_address() {
        return branch_address;
    }

    public void setBranch_address(
            String branch_address
    ) {
        this.branch_address =
                branch_address;
    }


    public String getCustomer_first_name() {
        return customer_first_name;
    }

    public void setCustomer_first_name(
            String customer_first_name
    ) {
        this.customer_first_name =
                customer_first_name;
    }


    public String getCustomer_last_name() {
        return customer_last_name;
    }

    public void setCustomer_last_name(
            String customer_last_name
    ) {
        this.customer_last_name =
                customer_last_name;
    }


    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(
            String customer_name
    ) {
        this.customer_name =
                customer_name;
    }


    public String getTechnician_first_name() {
        return technician_first_name;
    }

    public void setTechnician_first_name(
            String technician_first_name
    ) {
        this.technician_first_name =
                technician_first_name;
    }


    public String getTechnician_last_name() {
        return technician_last_name;
    }

    public void setTechnician_last_name(
            String technician_last_name
    ) {
        this.technician_last_name =
                technician_last_name;
    }


    public String getTechnician_name() {
        return technician_name;
    }

    public void setTechnician_name(
            String technician_name
    ) {
        this.technician_name =
                technician_name;
    }
}