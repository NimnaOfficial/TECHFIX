package com.mad.techfix.worker;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.mad.techfix.data.SessionManager;
import com.mad.techfix.data.local.database.AppDatabase;
import com.mad.techfix.data.local.database.PendingStatusUpdateEntity;
import com.mad.techfix.models.ApiResponse;
import com.mad.techfix.network.RetrofitClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

public class SyncStatusWorker extends Worker {

    private static final String TAG = "SyncStatusWorker";

    public SyncStatusWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();
        SessionManager sessionManager = new SessionManager(context);
        String token = sessionManager.getBearerToken();

        if (token == null || token.isEmpty()) {
            return Result.failure();
        }

        AppDatabase db = AppDatabase.getInstance(context);
        List<PendingStatusUpdateEntity> pendingUpdates = db.techFixDao().getAllPendingStatusUpdates();

        if (pendingUpdates == null || pendingUpdates.isEmpty()) {
            return Result.success();
        }

        boolean allSuccess = true;

        for (PendingStatusUpdateEntity update : pendingUpdates) {
            try {
                Map<String, Object> body = new HashMap<>();
                body.put("status", update.getStatus());
                body.put("note", update.getNote());

                // Synchronous call for Worker
                Call<ApiResponse<Object>> call = RetrofitClient.getApiService().updateAppointmentStatus(token, update.getAppointmentId(), body);
                Response<ApiResponse<Object>> response = call.execute();

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    db.techFixDao().deletePendingStatusUpdate(update.getId());
                    Log.d(TAG, "Successfully synced status update for appointment: " + update.getAppointmentId());
                } else {
                    allSuccess = false;
                    Log.e(TAG, "Failed to sync status update for appointment: " + update.getAppointmentId());
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception during sync: " + e.getMessage());
                allSuccess = false;
            }
        }

        return allSuccess ? Result.success() : Result.retry();
    }
}
