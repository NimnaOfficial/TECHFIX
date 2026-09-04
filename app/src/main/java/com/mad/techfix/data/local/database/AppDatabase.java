package com.mad.techfix.data.local.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(
        entities = {
                SparePartEntity.class,
                TechnicianEntity.class,
                BranchEntity.class,
                AppointmentEntity.class,
                DashboardMetricsEntity.class,
                RepairHistoryEntity.class,
                PaymentEntity.class
        },
        version = 4,
        exportSchema = false
)
public abstract class AppDatabase
        extends RoomDatabase {

    public abstract TechFixDao techFixDao();

    public abstract AdminDao adminDao();


    private static volatile AppDatabase INSTANCE;


    public static AppDatabase getInstance(
            Context context
    ) {

        if (INSTANCE == null) {

            synchronized (AppDatabase.class) {

                if (INSTANCE == null) {

                    INSTANCE =
                            Room.databaseBuilder(
                                            context.getApplicationContext(),
                                            AppDatabase.class,
                                            "techfix_database"
                                    )
                                    .fallbackToDestructiveMigration()
                                    .build();
                }
            }
        }

        return INSTANCE;
    }
}