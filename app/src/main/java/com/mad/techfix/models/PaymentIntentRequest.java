package com.mad.techfix.models;

public class PaymentIntentRequest {
    private String repairId;
    private long amount; // in cents

    public PaymentIntentRequest(String repairId, long amount) {
        this.repairId = repairId;
        this.amount = amount;
    }

    public String getRepairId() { return repairId; }
    public void setRepairId(String repairId) { this.repairId = repairId; }

    public long getAmount() { return amount; }
    public void setAmount(long amount) { this.amount = amount; }
}