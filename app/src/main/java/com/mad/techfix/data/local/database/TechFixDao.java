package com.mad.techfix.data.local.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TechFixDao {

    // ==========================================
    // SPARE PARTS (Parts Manager)
    // ==========================================

    @Query("SELECT * FROM spare_parts WHERE branchId = :branchId")
    List<SparePartEntity> getPartsByBranch(String branchId);

    @Query("SELECT * FROM spare_parts WHERE partId = :partId AND branchId = :branchId")
    SparePartEntity getPartById(String partId, String branchId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPart(SparePartEntity part);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertParts(List<SparePartEntity> parts);

    @Update
    void updatePart(SparePartEntity part);

    @Delete
    void deletePart(SparePartEntity part);

    @Query("DELETE FROM spare_parts WHERE partId = :partId AND branchId = :branchId")
    void deletePartById(String partId, String branchId);

    @Query("SELECT * FROM spare_parts ORDER BY partName ASC")
    List<SparePartEntity> getAllParts();

    // ==========================================
    // REPAIR HISTORY (Repair History Module)
    // ==========================================

    @Query("SELECT * FROM repair_history ORDER BY date DESC")
    List<RepairHistoryEntity> getAllHistory();

    @Query("SELECT * FROM repair_history WHERE repairId = :repairId")
    RepairHistoryEntity getHistoryItem(String repairId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertHistory(RepairHistoryEntity history);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertHistoryList(List<RepairHistoryEntity> historyList);

    @Query("DELETE FROM repair_history")
    void clearAllHistory();

    @Query("SELECT * FROM repair_history WHERE status = :status ORDER BY date DESC")
    List<RepairHistoryEntity> getHistoryByStatus(String status);

    @Query("SELECT * FROM repair_history WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    List<RepairHistoryEntity> getHistoryBetweenDates(String startDate, String endDate);

    // ==========================================
    // PAYMENTS (Payment Module)
    // ==========================================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPayment(PaymentEntity payment);

    @Query("SELECT * FROM payments WHERE paymentId = :paymentId")
    PaymentEntity getPaymentById(String paymentId);

    @Query("SELECT * FROM payments WHERE repairId = :repairId ORDER BY transactionDate DESC")
    List<PaymentEntity> getPaymentsByRepair(String repairId);

    @Query("SELECT * FROM payments ORDER BY transactionDate DESC")
    List<PaymentEntity> getAllPayments();

    @Query("SELECT * FROM payments WHERE paymentStatus = :status ORDER BY transactionDate DESC")
    List<PaymentEntity> getPaymentsByStatus(String status);

    @Update
    void updatePayment(PaymentEntity payment);

    @Query("DELETE FROM payments WHERE paymentId = :paymentId")
    void deletePaymentById(String paymentId);
}