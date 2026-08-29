package com.mad.techfix.models;

import java.util.List;

public class AppointmentDetail {
    // ========== APPOINTMENT ==========
    private String id;
    private String appointment_number;
    private String requested_date;
    private String requested_time;
    private String status;
    private String problem_description;
    private double estimated_price;
    private Double final_price;
    private String created_at;
    private String updated_at;

    // ========== CUSTOMER ==========
    private String customer_id;
    private String customer_first_name;
    private String customer_last_name;

    // ========== DEVICE ==========
    private String device_id;
    private String device_brand;
    private String device_model;
    private String serial_number;
    private String purchase_year;

    // ========== SERVICE ==========
    private String service_id;
    private String service_name;

    // ========== BRANCH ==========
    private String branch_id;
    private String branch_name;
    private String branch_city;

    // ========== TECHNICIAN ==========
    private String technician_id;
    private String technician_first_name;
    private String technician_last_name;
    private String technician_employee_code;

    // ========== RELATED DATA ==========
    private List<StatusHistory> status_history;
    private List<RepairImage> images;
    private List<PartUsed> parts_used;
    private Payment payment;

    // ===== Getters and Setters =====
    // Appointment
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getAppointment_number() { return appointment_number; }
    public void setAppointment_number(String appointment_number) { this.appointment_number = appointment_number; }

    public String getRequested_date() { return requested_date; }
    public void setRequested_date(String requested_date) { this.requested_date = requested_date; }

    public String getRequested_time() { return requested_time; }
    public void setRequested_time(String requested_time) { this.requested_time = requested_time; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getProblem_description() { return problem_description; }
    public void setProblem_description(String problem_description) { this.problem_description = problem_description; }

    public double getEstimated_price() { return estimated_price; }
    public void setEstimated_price(double estimated_price) { this.estimated_price = estimated_price; }

    public Double getFinal_price() { return final_price; }
    public void setFinal_price(Double final_price) { this.final_price = final_price; }

    public String getCreated_at() { return created_at; }
    public void setCreated_at(String created_at) { this.created_at = created_at; }

    public String getUpdated_at() { return updated_at; }
    public void setUpdated_at(String updated_at) { this.updated_at = updated_at; }

    // Customer
    public String getCustomer_id() { return customer_id; }
    public void setCustomer_id(String customer_id) { this.customer_id = customer_id; }

    public String getCustomer_first_name() { return customer_first_name; }
    public void setCustomer_first_name(String customer_first_name) { this.customer_first_name = customer_first_name; }

    public String getCustomer_last_name() { return customer_last_name; }
    public void setCustomer_last_name(String customer_last_name) { this.customer_last_name = customer_last_name; }

    public String getCustomer_full_name() {
        return (customer_first_name != null ? customer_first_name : "") + " " + (customer_last_name != null ? customer_last_name : "");
    }

    // Device
    public String getDevice_id() { return device_id; }
    public void setDevice_id(String device_id) { this.device_id = device_id; }

    public String getDevice_brand() { return device_brand; }
    public void setDevice_brand(String device_brand) { this.device_brand = device_brand; }

    public String getDevice_model() { return device_model; }
    public void setDevice_model(String device_model) { this.device_model = device_model; }

    public String getSerial_number() { return serial_number; }
    public void setSerial_number(String serial_number) { this.serial_number = serial_number; }

    public String getPurchase_year() { return purchase_year; }
    public void setPurchase_year(String purchase_year) { this.purchase_year = purchase_year; }

    public String getDevice_full_name() {
        return (device_brand != null ? device_brand : "") + " " + (device_model != null ? device_model : "");
    }

    // Service
    public String getService_id() { return service_id; }
    public void setService_id(String service_id) { this.service_id = service_id; }

    public String getService_name() { return service_name; }
    public void setService_name(String service_name) { this.service_name = service_name; }

    // Branch
    public String getBranch_id() { return branch_id; }
    public void setBranch_id(String branch_id) { this.branch_id = branch_id; }

    public String getBranch_name() { return branch_name; }
    public void setBranch_name(String branch_name) { this.branch_name = branch_name; }

    public String getBranch_city() { return branch_city; }
    public void setBranch_city(String branch_city) { this.branch_city = branch_city; }

    // Technician
    public String getTechnician_id() { return technician_id; }
    public void setTechnician_id(String technician_id) { this.technician_id = technician_id; }

    public String getTechnician_first_name() { return technician_first_name; }
    public void setTechnician_first_name(String technician_first_name) { this.technician_first_name = technician_first_name; }

    public String getTechnician_last_name() { return technician_last_name; }
    public void setTechnician_last_name(String technician_last_name) { this.technician_last_name = technician_last_name; }

    public String getTechnician_employee_code() { return technician_employee_code; }
    public void setTechnician_employee_code(String technician_employee_code) { this.technician_employee_code = technician_employee_code; }

    public String getTechnician_full_name() {
        return (technician_first_name != null ? technician_first_name : "") + " " + (technician_last_name != null ? technician_last_name : "");
    }

    // Related Data
    public List<StatusHistory> getStatus_history() { return status_history; }
    public void setStatus_history(List<StatusHistory> status_history) { this.status_history = status_history; }

    public List<RepairImage> getImages() { return images; }
    public void setImages(List<RepairImage> images) { this.images = images; }

    public List<PartUsed> getParts_used() { return parts_used; }
    public void setParts_used(List<PartUsed> parts_used) { this.parts_used = parts_used; }

    public Payment getPayment() { return payment; }
    public void setPayment(Payment payment) { this.payment = payment; }

    // ==========================================
    // INNER CLASS: StatusHistory
    // ==========================================
    public static class StatusHistory {
        private String id;
        private String appointment_id;
        private String status;
        private String note;
        private String changed_by;
        private String created_at;
        private String changed_by_first_name;
        private String changed_by_last_name;
        private String changed_by_role;

        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getAppointment_id() { return appointment_id; }
        public void setAppointment_id(String appointment_id) { this.appointment_id = appointment_id; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public String getNote() { return note; }
        public void setNote(String note) { this.note = note; }

        public String getChanged_by() { return changed_by; }
        public void setChanged_by(String changed_by) { this.changed_by = changed_by; }

        public String getCreated_at() { return created_at; }
        public void setCreated_at(String created_at) { this.created_at = created_at; }

        public String getChanged_by_first_name() { return changed_by_first_name; }
        public void setChanged_by_first_name(String changed_by_first_name) { this.changed_by_first_name = changed_by_first_name; }

        public String getChanged_by_last_name() { return changed_by_last_name; }
        public void setChanged_by_last_name(String changed_by_last_name) { this.changed_by_last_name = changed_by_last_name; }

        public String getChanged_by_role() { return changed_by_role; }
        public void setChanged_by_role(String changed_by_role) { this.changed_by_role = changed_by_role; }

        public String getChanged_by_full_name() {
            return (changed_by_first_name != null ? changed_by_first_name : "") + " " + (changed_by_last_name != null ? changed_by_last_name : "");
        }
    }

    // ==========================================
    // INNER CLASS: RepairImage
    // ==========================================
    public static class RepairImage {
        private String id;
        private String appointment_id;
        private String image_url;
        private String image_type;
        private String uploaded_by;
        private String created_at;

        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getAppointment_id() { return appointment_id; }
        public void setAppointment_id(String appointment_id) { this.appointment_id = appointment_id; }

        public String getImage_url() { return image_url; }
        public void setImage_url(String image_url) { this.image_url = image_url; }

        public String getImage_type() { return image_type; }
        public void setImage_type(String image_type) { this.image_type = image_type; }

        public String getUploaded_by() { return uploaded_by; }
        public void setUploaded_by(String uploaded_by) { this.uploaded_by = uploaded_by; }

        public String getCreated_at() { return created_at; }
        public void setCreated_at(String created_at) { this.created_at = created_at; }
    }

    // ==========================================
    // INNER CLASS: PartUsed
    // ==========================================
    public static class PartUsed {
        private String id;
        private String appointment_id;
        private String part_id;
        private String part_name;
        private String part_number;
        private int quantity;
        private double unit_price;
        private double total_price;

        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getAppointment_id() { return appointment_id; }
        public void setAppointment_id(String appointment_id) { this.appointment_id = appointment_id; }

        public String getPart_id() { return part_id; }
        public void setPart_id(String part_id) { this.part_id = part_id; }

        public String getPart_name() { return part_name; }
        public void setPart_name(String part_name) { this.part_name = part_name; }

        public String getPart_number() { return part_number; }
        public void setPart_number(String part_number) { this.part_number = part_number; }

        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }

        public double getUnit_price() { return unit_price; }
        public void setUnit_price(double unit_price) { this.unit_price = unit_price; }

        public double getTotal_price() { return total_price; }
        public void setTotal_price(double total_price) { this.total_price = total_price; }
    }
}