package com.mad.techfix.models.admin;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class UserMonitorResponse {
    private Manager user;
    private Connections connections;

    public Manager getUser() { return user; }
    public Connections getConnections() { return connections; }

    public static class Connections {
        private List<Device> devices;
        private List<AppointmentPreview> appointmentsAsCustomer;
        private List<AppointmentPreview> appointmentsAsTech;
        private TechProfile technicianProfile;
        private List<Skill> skills;
        private int historyActionsCount;
        private String managerBranch;

        public List<Device> getDevices() { return devices; }
        public List<AppointmentPreview> getAppointmentsAsCustomer() { return appointmentsAsCustomer; }
        public List<AppointmentPreview> getAppointmentsAsTech() { return appointmentsAsTech; }
        public TechProfile getTechnicianProfile() { return technicianProfile; }
        public List<Skill> getSkills() { return skills; }
        public int getHistoryActionsCount() { return historyActionsCount; }
        public String getManagerBranch() { return managerBranch; }
    }

    public static class Device {
        public String id;
        public String brand;
        public String model;
        @SerializedName("serial_number")
        public String serialNumber;
    }

    public static class AppointmentPreview {
        @SerializedName("appointment_number")
        public String appointmentNumber;
        public String status;
        @SerializedName("requested_date")
        public String requestedDate;
    }

    public static class TechProfile {
        public String id;
        @SerializedName("employee_code")
        public String employeeCode;
        public String specialization;
        @SerializedName("availability_status")
        public String availabilityStatus;
    }

    public static class Skill {
        public String name;
    }
}
