package com.mad.techfix.data.local.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface TechFixDao {
    @Query("SELECT * FROM spare_parts WHERE branchId = :branchId")
    List<SparePartEntity> getPartsByBranch(String branchId);

    @Update
    void updatePart(SparePartEntity part);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPart(SparePartEntity part);

    @androidx.room.Delete
    void deletePart(SparePartEntity part);
}