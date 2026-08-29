package com.mad.techfix.data.local.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "payments")
public class PaymentEntity {
    @PrimaryKey
    @NonNull
    public String paymentId;
    public String repairId;
    public double amount;
    public String paymentMethod;
    public String paymentStatus;
    public String transactionDate;

    public PaymentEntity(String paymentId, String repairId, double amount,
                         String paymentMethod, String paymentStatus, String transactionDate) {
        this.paymentId = paymentId;
        this.repairId = repairId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.transactionDate = transactionDate;
    }

    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
    public String getRepairId() { return repairId; }
    public void setRepairId(String repairId) { this.repairId = repairId; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    public String getTransactionDate() { return transactionDate; }
    public void setTransactionDate(String transactionDate) { this.transactionDate = transactionDate; }
}