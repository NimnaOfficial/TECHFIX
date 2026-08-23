package com.mad.techfix.data.local.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "spare_parts")
public class SparePartEntity {
    @PrimaryKey
    @NonNull
    public String partId;
    public String branchId;
    public String partName;
    public String category;
    public int quantity;
    public double unitPrice;

    public SparePartEntity(String partId, String branchId, String partName, String category, int quantity, double unitPrice) {
        this.partId = partId;
        this.branchId = branchId;
        this.partName = partName;
        this.category = category;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }
}