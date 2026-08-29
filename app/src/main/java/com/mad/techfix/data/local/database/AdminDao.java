package com.mad.techfix.data.local.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AdminDao {
    @Query("SELECT * FROM local_technicians")
    List<TechnicianEntity> getAllTechnicians();

    @Query("SELECT * FROM local_technicians WHERE branchId = :branchId")
    List<TechnicianEntity> getTechniciansByBranch(String branchId);

    @Query("SELECT * FROM local_technicians WHERE branchId = :branchId AND status = 'AVAILABLE'")
    List<TechnicianEntity> getAvailableTechniciansByBranch(String branchId);

    @Query("SELECT * FROM local_technicians WHERE status = :status")
    List<TechnicianEntity> getTechniciansByStatus(String status);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTechnicians(List<TechnicianEntity> technicians);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTechnician(TechnicianEntity technician);

    @Query("DELETE FROM local_technicians")
    void deleteAllTechnicians();

    @Query("DELETE FROM local_technicians WHERE id = :id")
    void deleteTechnicianById(String id);

    @Query("SELECT * FROM local_branches")
    List<BranchEntity> getAllBranches();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBranches(List<BranchEntity> branches);

    @Query("DELETE FROM local_branches")
    void deleteAllBranches();

    @Query("SELECT COUNT(*) FROM local_technicians WHERE status = 'AVAILABLE'")
    int getAvailableTechnicianCount();
    // --- Appointments ---
    @Query("SELECT * FROM local_appointments ORDER BY created_at DESC")
    List<AppointmentEntity> getAllAppointments();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAppointments(List<AppointmentEntity> appointments);

    @Query("DELETE FROM local_appointments")
    void deleteAllAppointments();

    // --- Dashboard Metrics ---
    @Query("SELECT * FROM local_dashboard_metrics WHERE id = 1")
    DashboardMetricsEntity getDashboardMetrics();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDashboardMetrics(DashboardMetricsEntity metrics);
}
