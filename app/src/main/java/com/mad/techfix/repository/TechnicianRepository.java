package com.mad.techfix.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import com.mad.techfix.data.local.database.AppDatabase;
import com.mad.techfix.data.local.database.AppointmentEntity;
import com.mad.techfix.data.local.database.TechFixDao;
import com.mad.techfix.models.ApiResponse;
import com.mad.techfix.models.Appointment;
import com.mad.techfix.models.AppointmentDetail;
import com.mad.techfix.network.ApiService;
import com.mad.techfix.network.RetrofitClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TechnicianRepository {

    private static final String PREFS_NAME =
            "technician_room_cache";

    private static final String TECH_ID_PREFIX =
            "technician_id_";

    private final ApiService apiService;
    private final TechFixDao techFixDao;

    private final SharedPreferences preferences;

    private final ExecutorService executorService =
            Executors.newSingleThreadExecutor();

    private final Handler mainHandler =
            new Handler(Looper.getMainLooper());


    public interface RepositoryCallback<T> {

        void onSuccess(T data);

        void onError(String message);
    }


    public TechnicianRepository(
            Context context,
            AppDatabase database
    ) {

        apiService =
                RetrofitClient.getApiService();

        techFixDao =
                database.techFixDao();

        preferences =
                context.getApplicationContext()
                        .getSharedPreferences(
                                PREFS_NAME,
                                Context.MODE_PRIVATE
                        );
    }


    // ==========================================
    // TECHNICIAN APPOINTMENTS
    // NETWORK FIRST -> ROOM FALLBACK
    // ==========================================

    public void getTechnicianAppointments(
            String token,
            String userId,
            RepositoryCallback<List<Appointment>> callback
    ) {

        if (token == null
                || token.trim().isEmpty()) {

            loadCachedAppointments(
                    userId,
                    callback,
                    "Authentication token is missing"
            );

            return;
        }


        apiService
                .getTechnicianAppointments(token)
                .enqueue(
                        new Callback<
                                ApiResponse<List<Appointment>>
                                >() {

                            @Override
                            public void onResponse(
                                    Call<ApiResponse<List<Appointment>>> call,
                                    Response<ApiResponse<List<Appointment>>> response
                            ) {

                                if (response.isSuccessful()
                                        && response.body() != null
                                        && response.body().isSuccess()) {

                                    List<Appointment> appointments =
                                            response.body()
                                                    .getData();


                                    if (appointments == null) {

                                        appointments =
                                                new ArrayList<>();
                                    }


                                    cacheAppointments(
                                            userId,
                                            appointments
                                    );


                                    callback.onSuccess(
                                            appointments
                                    );

                                } else {

                                    loadCachedAppointments(
                                            userId,
                                            callback,
                                            getResponseMessage(
                                                    response,
                                                    "Unable to load assigned repairs"
                                            )
                                    );
                                }
                            }


                            @Override
                            public void onFailure(
                                    Call<ApiResponse<List<Appointment>>> call,
                                    Throwable throwable
                            ) {

                                loadCachedAppointments(
                                        userId,
                                        callback,
                                        getThrowableMessage(
                                                throwable,
                                                "Unable to load assigned repairs"
                                        )
                                );
                            }
                        }
                );
    }


    // ==========================================
    // CACHE APPOINTMENTS
    // ==========================================

    private void cacheAppointments(
            String userId,
            List<Appointment> appointments
    ) {

        executorService.execute(
                () -> {

                    try {

                        String technicianId =
                                findTechnicianId(
                                        appointments
                                );


                        if (technicianId != null
                                && userId != null
                                && !userId.trim().isEmpty()) {

                            preferences
                                    .edit()
                                    .putString(
                                            TECH_ID_PREFIX + userId,
                                            technicianId
                                    )
                                    .apply();
                        }


                        if (technicianId != null) {

                            techFixDao
                                    .clearAppointmentsForTechnician(
                                            technicianId
                                    );
                        }


                        List<AppointmentEntity> entities =
                                new ArrayList<>();


                        for (Appointment appointment :
                                appointments) {

                            if (appointment == null) {
                                continue;
                            }


                            entities.add(
                                    AppointmentEntity
                                            .fromAppointment(
                                                    appointment
                                            )
                            );
                        }


                        if (!entities.isEmpty()) {

                            techFixDao
                                    .insertAppointments(
                                            entities
                                    );
                        }

                    } catch (Exception ignored) {
                    }
                }
        );
    }


    // ==========================================
    // ROOM FALLBACK
    // ==========================================

    private void loadCachedAppointments(
            String userId,
            RepositoryCallback<List<Appointment>> callback,
            String networkError
    ) {

        executorService.execute(
                () -> {

                    try {

                        String technicianId =
                                getSavedTechnicianId(
                                        userId
                                );


                        if (technicianId == null) {

                            mainHandler.post(
                                    () -> callback.onError(
                                            networkError
                                    )
                            );

                            return;
                        }


                        List<AppointmentEntity> entities =
                                techFixDao
                                        .getAppointmentsByTechnician(
                                                technicianId
                                        );


                        List<Appointment> appointments =
                                new ArrayList<>();


                        if (entities != null) {

                            for (AppointmentEntity entity :
                                    entities) {

                                appointments.add(
                                        entity.toAppointment()
                                );
                            }
                        }


                        mainHandler.post(
                                () -> callback.onSuccess(
                                        appointments
                                )
                        );

                    } catch (Exception exception) {

                        mainHandler.post(
                                () -> callback.onError(
                                        networkError
                                )
                        );
                    }
                }
        );
    }


    private String findTechnicianId(
            List<Appointment> appointments
    ) {

        if (appointments == null) {

            return null;
        }


        for (Appointment appointment :
                appointments) {

            if (appointment == null) {
                continue;
            }


            String technicianId =
                    appointment
                            .getTechnician_id();


            if (technicianId != null
                    && !technicianId.trim().isEmpty()) {

                return technicianId.trim();
            }
        }


        return null;
    }


    private String getSavedTechnicianId(
            String userId
    ) {

        if (userId == null
                || userId.trim().isEmpty()) {

            return null;
        }


        return preferences.getString(
                TECH_ID_PREFIX + userId,
                null
        );
    }


    // ==========================================
    // APPOINTMENT DETAIL
    // ==========================================

    public void getAppointmentDetail(
            String token,
            String appointmentId,
            RepositoryCallback<AppointmentDetail> callback
    ) {

        if (appointmentId == null
                || appointmentId.trim().isEmpty()) {

            callback.onError(
                    "Appointment ID is missing"
            );

            return;
        }


        if (token == null
                || token.trim().isEmpty()) {

            loadCachedDetail(
                    appointmentId,
                    callback,
                    "Authentication token is missing"
            );

            return;
        }


        apiService
                .getAppointmentDetail(
                        token,
                        appointmentId
                )
                .enqueue(
                        new Callback<
                                ApiResponse<AppointmentDetail>
                                >() {

                            @Override
                            public void onResponse(
                                    Call<ApiResponse<AppointmentDetail>> call,
                                    Response<ApiResponse<AppointmentDetail>> response
                            ) {

                                if (response.isSuccessful()
                                        && response.body() != null
                                        && response.body().isSuccess()
                                        && response.body().getData() != null) {

                                    callback.onSuccess(
                                            response.body()
                                                    .getData()
                                    );

                                } else {

                                    loadCachedDetail(
                                            appointmentId,
                                            callback,
                                            getResponseMessage(
                                                    response,
                                                    "Unable to load repair details"
                                            )
                                    );
                                }
                            }


                            @Override
                            public void onFailure(
                                    Call<ApiResponse<AppointmentDetail>> call,
                                    Throwable throwable
                            ) {

                                loadCachedDetail(
                                        appointmentId,
                                        callback,
                                        getThrowableMessage(
                                                throwable,
                                                "Unable to load repair details"
                                        )
                                );
                            }
                        }
                );
    }


    private void loadCachedDetail(
            String appointmentId,
            RepositoryCallback<AppointmentDetail> callback,
            String fallbackError
    ) {

        executorService.execute(
                () -> {

                    try {

                        AppointmentEntity entity =
                                techFixDao
                                        .getAppointmentById(
                                                appointmentId
                                        );


                        if (entity == null) {

                            mainHandler.post(
                                    () -> callback.onError(
                                            fallbackError
                                    )
                            );

                            return;
                        }


                        AppointmentDetail detail =
                                convertToDetail(
                                        entity.toAppointment()
                                );


                        mainHandler.post(
                                () -> callback.onSuccess(
                                        detail
                                )
                        );

                    } catch (Exception exception) {

                        mainHandler.post(
                                () -> callback.onError(
                                        fallbackError
                                )
                        );
                    }
                }
        );
    }


    // ==========================================
    // REPAIR HISTORY / NOTES
    // ==========================================

    public void getRepairHistory(
            String token,
            String appointmentId,
            RepositoryCallback<List<Object>> callback
    ) {

        apiService
                .getAppointmentHistory(
                        token,
                        appointmentId
                )
                .enqueue(
                        new Callback<
                                ApiResponse<List<Object>>
                                >() {

                            @Override
                            public void onResponse(
                                    Call<ApiResponse<List<Object>>> call,
                                    Response<ApiResponse<List<Object>>> response
                            ) {

                                if (response.isSuccessful()
                                        && response.body() != null
                                        && response.body().isSuccess()) {

                                    List<Object> history =
                                            response.body()
                                                    .getData();


                                    if (history == null) {

                                        history =
                                                new ArrayList<>();
                                    }


                                    callback.onSuccess(
                                            history
                                    );

                                } else {

                                    callback.onError(
                                            "Unable to load repair notes"
                                    );
                                }
                            }


                            @Override
                            public void onFailure(
                                    Call<ApiResponse<List<Object>>> call,
                                    Throwable throwable
                            ) {

                                callback.onError(
                                        getThrowableMessage(
                                                throwable,
                                                "Unable to load repair notes"
                                        )
                                );
                            }
                        }
                );
    }


    // ==========================================
    // UPDATE STATUS / SAVE NOTE
    // ==========================================

    public void updateRepairStatus(
            String token,
            String appointmentId,
            String status,
            String note,
            RepositoryCallback<Object> callback
    ) {

        Map<String, Object> body =
                new HashMap<>();


        body.put(
                "status",
                status
        );


        body.put(
                "note",
                note == null
                        ? ""
                        : note
        );


        apiService
                .updateAppointmentStatus(
                        token,
                        appointmentId,
                        body
                )
                .enqueue(
                        new Callback<
                                ApiResponse<Object>
                                >() {

                            @Override
                            public void onResponse(
                                    Call<ApiResponse<Object>> call,
                                    Response<ApiResponse<Object>> response
                            ) {

                                if (response.isSuccessful()
                                        && response.body() != null
                                        && response.body().isSuccess()) {

                                    updateCachedStatus(
                                            appointmentId,
                                            status
                                    );


                                    callback.onSuccess(
                                            response.body()
                                                    .getData()
                                    );

                                } else {

                                    callback.onError(
                                            getResponseMessage(
                                                    response,
                                                    "Unable to update repair status"
                                            )
                                    );
                                }
                            }


                            @Override
                            public void onFailure(
                                    Call<ApiResponse<Object>> call,
                                    Throwable throwable
                            ) {

                                callback.onError(
                                        getThrowableMessage(
                                                throwable,
                                                "Unable to update repair status"
                                        )
                                );
                            }
                        }
                );
    }


    private void updateCachedStatus(
            String appointmentId,
            String status
    ) {

        executorService.execute(
                () -> {

                    try {

                        AppointmentEntity entity =
                                techFixDao
                                        .getAppointmentById(
                                                appointmentId
                                        );


                        if (entity == null) {

                            return;
                        }


                        Appointment appointment =
                                entity.toAppointment();


                        appointment.setStatus(
                                status
                        );


                        techFixDao
                                .insertAppointment(
                                        AppointmentEntity
                                                .fromAppointment(
                                                        appointment
                                                )
                                );

                    } catch (Exception ignored) {
                    }
                }
        );
    }


    // ==========================================
    // REPAIR IMAGES
    // ==========================================

    public void getRepairImages(
            String token,
            String appointmentId,
            RepositoryCallback<List<com.mad.techfix.models.RepairImage>> callback
    ) {

        apiService
                .getAppointmentImages(
                        token,
                        appointmentId
                )
                .enqueue(
                        new Callback<
                                ApiResponse<List<com.mad.techfix.models.RepairImage>>
                                >() {

                            @Override
                            public void onResponse(
                                    Call<ApiResponse<List<com.mad.techfix.models.RepairImage>>> call,
                                    Response<ApiResponse<List<com.mad.techfix.models.RepairImage>>> response
                            ) {

                                if (response.isSuccessful()
                                        && response.body() != null
                                        && response.body().isSuccess()) {

                                    List<com.mad.techfix.models.RepairImage> images =
                                            response.body()
                                                    .getData();


                                    if (images == null) {

                                        images =
                                                new ArrayList<>();
                                    }


                                    callback.onSuccess(
                                            images
                                    );

                                } else {

                                    callback.onError(
                                            "Unable to load repair images"
                                    );
                                }
                            }


                            @Override
                            public void onFailure(
                                    Call<ApiResponse<List<com.mad.techfix.models.RepairImage>>> call,
                                    Throwable throwable
                            ) {

                                callback.onError(
                                        getThrowableMessage(
                                                throwable,
                                                "Unable to load repair images"
                                        )
                                );
                            }
                        }
                );
    }


    public void addRepairImage(
            String token,
            String appointmentId,
            String imageUrl,
            RepositoryCallback<Object> callback
    ) {

        Map<String, Object> body =
                new HashMap<>();


        body.put(
                "image_url",
                imageUrl
        );


        body.put(
                "image_type",
                "REPAIR"
        );


        apiService
                .addAppointmentImage(
                        token,
                        appointmentId,
                        body
                )
                .enqueue(
                        new Callback<
                                ApiResponse<Object>
                                >() {

                            @Override
                            public void onResponse(
                                    Call<ApiResponse<Object>> call,
                                    Response<ApiResponse<Object>> response
                            ) {

                                if (response.isSuccessful()
                                        && response.body() != null
                                        && response.body().isSuccess()) {

                                    callback.onSuccess(
                                            response.body()
                                                    .getData()
                                    );

                                } else {

                                    callback.onError(
                                            getResponseMessage(
                                                    response,
                                                    "Unable to add repair image"
                                            )
                                    );
                                }
                            }


                            @Override
                            public void onFailure(
                                    Call<ApiResponse<Object>> call,
                                    Throwable throwable
                            ) {

                                callback.onError(
                                        getThrowableMessage(
                                                throwable,
                                                "Unable to add repair image"
                                        )
                                );
                            }
                        }
                );
    }


    // ==========================================
    // APPOINTMENT -> DETAIL
    // ==========================================

    private AppointmentDetail convertToDetail(
            Appointment appointment
    ) {

        AppointmentDetail detail =
                new AppointmentDetail();


        detail.setId(
                appointment.getId()
        );

        detail.setAppointment_number(
                appointment.getAppointment_number()
        );

        detail.setCustomer_id(
                appointment.getCustomer_id()
        );

        detail.setDevice_id(
                appointment.getDevice_id()
        );

        detail.setService_id(
                appointment.getService_id()
        );

        detail.setBranch_id(
                appointment.getBranch_id()
        );

        detail.setTechnician_id(
                appointment.getTechnician_id()
        );


        detail.setStatus(
                appointment.getStatus()
        );

        detail.setRequested_date(
                appointment.getRequested_date()
        );

        detail.setRequested_time(
                appointment.getRequested_time()
        );

        detail.setProblem_description(
                appointment.getProblem_description()
        );

        detail.setEstimated_price(
                appointment.getEstimated_price()
        );

        detail.setFinal_price(
                appointment.getFinal_price()
        );

        detail.setCreated_at(
                appointment.getCreated_at()
        );

        detail.setUpdated_at(
                appointment.getUpdated_at()
        );


        detail.setDevice_brand(
                appointment.getDevice_brand()
        );

        detail.setDevice_model(
                appointment.getDevice_model()
        );

        detail.setSerial_number(
                appointment.getDevice_serial_number()
        );


        detail.setService_name(
                appointment.getService_name()
        );


        detail.setBranch_name(
                appointment.getBranch_name()
        );

        detail.setBranch_city(
                appointment.getBranch_city()
        );


        detail.setCustomer_first_name(
                appointment.getCustomer_first_name()
        );

        detail.setCustomer_last_name(
                appointment.getCustomer_last_name()
        );


        detail.setTechnician_first_name(
                appointment.getTechnician_first_name()
        );

        detail.setTechnician_last_name(
                appointment.getTechnician_last_name()
        );


        return detail;
    }


    // ==========================================
    // ERRORS
    // ==========================================

    private <T> String getResponseMessage(
            Response<ApiResponse<T>> response,
            String fallback
    ) {

        if (response != null
                && response.body() != null
                && response.body().getMessage() != null
                && !response.body()
                .getMessage()
                .trim()
                .isEmpty()) {

            return response.body()
                    .getMessage();
        }


        return fallback;
    }


    private String getThrowableMessage(
            Throwable throwable,
            String fallback
    ) {

        if (throwable != null
                && throwable.getMessage() != null
                && !throwable.getMessage()
                .trim()
                .isEmpty()) {

            return throwable.getMessage();
        }


        return fallback;
    }
}