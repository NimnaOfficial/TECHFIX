package com.mad.techfix.data.local.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "pending_status_updates")
public class PendingStatusUpdateEntity {
    
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private String appointmentId;
    private String status;
    private String note;
    private long timestamp;
    
    public PendingStatusUpdateEntity(String appointmentId, String status, String note, long timestamp) {
        this.appointmentId = appointmentId;
        this.status = status;
        this.note = note;
        this.timestamp = timestamp;
    }
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getAppointmentId() { return appointmentId; }
    public void setAppointmentId(String appointmentId) { this.appointmentId = appointmentId; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
