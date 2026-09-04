package com.mad.techfix.data.local.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.mad.techfix.models.Appointment;

@Entity(tableName = "local_appointments")
public class AppointmentEntity {

    @PrimaryKey
    @NonNull
    public String id;

    public String appointment_number;

    public String customer_id;
    public String device_id;
    public String service_id;
    public String branch_id;
    public String technician_id;

    public String status;

    public String requested_date;
    public String requested_time;

    public String problem_description;

    public double estimated_price;
    public double final_price;

    public Double customer_latitude;
    public Double customer_longitude;

    public String created_at;
    public String updated_at;


    // ==========================================
    // JOINED DEVICE INFORMATION
    // ==========================================

    public String device_name;
    public String device_brand;
    public String device_model;
    public String device_serial_number;


    // ==========================================
    // JOINED SERVICE INFORMATION
    // ==========================================

    public String service_name;
    public String service_description;
    public Double service_base_price;


    // ==========================================
    // JOINED BRANCH INFORMATION
    // ==========================================

    public String branch_name;
    public String branch_city;
    public String branch_address;


    // ==========================================
    // JOINED CUSTOMER INFORMATION
    // ==========================================

    public String customer_first_name;
    public String customer_last_name;
    public String customer_name;


    // ==========================================
    // JOINED TECHNICIAN INFORMATION
    // ==========================================

    public String technician_first_name;
    public String technician_last_name;
    public String technician_name;


    /*
     * Keep this constructor because existing
     * project code may already use it.
     */
    public AppointmentEntity(
            @NonNull String id,
            String appointment_number,
            String customer_id,
            String device_id,
            String service_id,
            String branch_id,
            String technician_id,
            String status,
            String requested_date,
            String requested_time,
            double estimated_price,
            double final_price,
            String created_at
    ) {

        this.id = id;
        this.appointment_number = appointment_number;
        this.customer_id = customer_id;
        this.device_id = device_id;
        this.service_id = service_id;
        this.branch_id = branch_id;
        this.technician_id = technician_id;
        this.status = status;
        this.requested_date = requested_date;
        this.requested_time = requested_time;
        this.estimated_price = estimated_price;
        this.final_price = final_price;
        this.created_at = created_at;
    }


    // ==========================================
    // API MODEL -> ROOM ENTITY
    // ==========================================

    public static AppointmentEntity fromAppointment(
            Appointment appointment
    ) {

        if (appointment == null
                || appointment.getId() == null
                || appointment.getId().trim().isEmpty()) {

            return null;
        }

        double finalPrice = 0.0;

        if (appointment.getFinal_price() != null) {
            finalPrice = appointment.getFinal_price();
        }

        AppointmentEntity entity =
                new AppointmentEntity(
                        appointment.getId(),
                        appointment.getAppointment_number(),
                        appointment.getCustomer_id(),
                        appointment.getDevice_id(),
                        appointment.getService_id(),
                        appointment.getBranch_id(),
                        appointment.getTechnician_id(),
                        appointment.getStatus(),
                        appointment.getRequested_date(),
                        appointment.getRequested_time(),
                        appointment.getEstimated_price(),
                        finalPrice,
                        appointment.getCreated_at()
                );


        entity.problem_description =
                appointment.getProblem_description();

        entity.customer_latitude =
                appointment.getCustomer_latitude();

        entity.customer_longitude =
                appointment.getCustomer_longitude();

        entity.updated_at =
                appointment.getUpdated_at();


        // Device JOIN data
        entity.device_name =
                appointment.getDevice_name();

        entity.device_brand =
                appointment.getDevice_brand();

        entity.device_model =
                appointment.getDevice_model();

        entity.device_serial_number =
                appointment.getDevice_serial_number();


        // Service JOIN data
        entity.service_name =
                appointment.getService_name();

        entity.service_description =
                appointment.getService_description();

        entity.service_base_price =
                appointment.getService_base_price();


        // Branch JOIN data
        entity.branch_name =
                appointment.getBranch_name();

        entity.branch_city =
                appointment.getBranch_city();

        entity.branch_address =
                appointment.getBranch_address();


        // Customer JOIN data
        entity.customer_first_name =
                appointment.getCustomer_first_name();

        entity.customer_last_name =
                appointment.getCustomer_last_name();

        entity.customer_name =
                appointment.getCustomer_name();


        // Technician JOIN data
        entity.technician_first_name =
                appointment.getTechnician_first_name();

        entity.technician_last_name =
                appointment.getTechnician_last_name();

        entity.technician_name =
                appointment.getTechnician_name();


        return entity;
    }


    // ==========================================
    // ROOM ENTITY -> API/UI MODEL
    // ==========================================

    public Appointment toAppointment() {

        Appointment appointment =
                new Appointment();

        appointment.setId(id);

        appointment.setAppointment_number(
                appointment_number
        );

        appointment.setCustomer_id(
                customer_id
        );

        appointment.setDevice_id(
                device_id
        );

        appointment.setService_id(
                service_id
        );

        appointment.setBranch_id(
                branch_id
        );

        appointment.setTechnician_id(
                technician_id
        );

        appointment.setStatus(
                status
        );

        appointment.setRequested_date(
                requested_date
        );

        appointment.setRequested_time(
                requested_time
        );

        appointment.setProblem_description(
                problem_description
        );

        appointment.setEstimated_price(
                estimated_price
        );

        appointment.setFinal_price(
                final_price
        );

        appointment.setCustomer_latitude(
                customer_latitude
        );

        appointment.setCustomer_longitude(
                customer_longitude
        );

        appointment.setCreated_at(
                created_at
        );

        appointment.setUpdated_at(
                updated_at
        );


        // Device JOIN data
        appointment.setDevice_name(
                device_name
        );

        appointment.setDevice_brand(
                device_brand
        );

        appointment.setDevice_model(
                device_model
        );

        appointment.setDevice_serial_number(
                device_serial_number
        );


        // Service JOIN data
        appointment.setService_name(
                service_name
        );

        appointment.setService_description(
                service_description
        );

        appointment.setService_base_price(
                service_base_price
        );


        // Branch JOIN data
        appointment.setBranch_name(
                branch_name
        );

        appointment.setBranch_city(
                branch_city
        );

        appointment.setBranch_address(
                branch_address
        );


        // Customer JOIN data
        appointment.setCustomer_first_name(
                customer_first_name
        );

        appointment.setCustomer_last_name(
                customer_last_name
        );

        appointment.setCustomer_name(
                customer_name
        );


        // Technician JOIN data
        appointment.setTechnician_first_name(
                technician_first_name
        );

        appointment.setTechnician_last_name(
                technician_last_name
        );

        appointment.setTechnician_name(
                technician_name
        );


        return appointment;
    }
}