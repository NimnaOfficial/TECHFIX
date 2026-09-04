package com.mad.techfix.repository;

import android.os.Handler;
import android.os.Looper;

import com.mad.techfix.data.local.database.AppDatabase;
import com.mad.techfix.data.local.database.AppointmentEntity;
import com.mad.techfix.data.local.database.TechFixDao;
import com.mad.techfix.models.ApiResponse;
import com.mad.techfix.models.Appointment;
import com.mad.techfix.network.ApiService;
import com.mad.techfix.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppointmentRepository {

    private final ApiService apiService;
    private final TechFixDao techFixDao;

    private final ExecutorService executorService;
    private final Handler mainHandler;


    public interface AppointmentCallback<T> {

        void onSuccess(T data);

        void onError(String message);
    }


    public AppointmentRepository(
            AppDatabase database
    ) {

        apiService =
                RetrofitClient.getApiService();

        techFixDao =
                database.techFixDao();

        executorService =
                Executors.newSingleThreadExecutor();

        mainHandler =
                new Handler(
                        Looper.getMainLooper()
                );
    }


    // ==========================================
    // CUSTOMER APPOINTMENTS
    // NETWORK FIRST -> ROOM FALLBACK
    // ==========================================

    public void getCustomerAppointments(
            String token,
            String customerId,
            AppointmentCallback<List<Appointment>> callback
    ) {

        if (token == null
                || token.trim().isEmpty()) {

            loadCachedCustomerAppointments(
                    customerId,
                    callback,
                    "Authentication token is missing"
            );

            return;
        }

        apiService
                .getAppointments(token)
                .enqueue(
                        new Callback<
                                ApiResponse<
                                        List<Appointment>
                                        >
                                >() {

                            @Override
                            public void onResponse(
                                    Call<
                                            ApiResponse<
                                                    List<Appointment>
                                                    >
                                            > call,

                                    Response<
                                            ApiResponse<
                                                    List<Appointment>
                                                    >
                                            > response
                            ) {

                                if (response.isSuccessful()
                                        && response.body() != null
                                        && response.body().isSuccess()) {

                                    List<Appointment> appointments =
                                            response.body().getData();

                                    if (appointments == null) {

                                        appointments =
                                                new ArrayList<>();
                                    }

                                    List<Appointment> finalAppointments =
                                            appointments;

                                    cacheCustomerAppointments(
                                            customerId,
                                            finalAppointments
                                    );

                                    callback.onSuccess(
                                            finalAppointments
                                    );

                                } else {

                                    loadCachedCustomerAppointments(
                                            customerId,
                                            callback,
                                            "Unable to load appointments from server"
                                    );
                                }
                            }


                            @Override
                            public void onFailure(
                                    Call<
                                            ApiResponse<
                                                    List<Appointment>
                                                    >
                                            > call,

                                    Throwable t
                            ) {

                                String message =
                                        "Network error";

                                if (t != null
                                        && t.getMessage() != null
                                        && !t.getMessage()
                                        .trim()
                                        .isEmpty()) {

                                    message =
                                            t.getMessage();
                                }

                                loadCachedCustomerAppointments(
                                        customerId,
                                        callback,
                                        message
                                );
                            }
                        }
                );
    }


    // ==========================================
    // SAVE CUSTOMER APPOINTMENTS TO ROOM
    // ==========================================

    private void cacheCustomerAppointments(
            String customerId,
            List<Appointment> appointments
    ) {

        executorService.execute(
                () -> {

                    try {

                        /*
                         * Clear only this customer's cache.
                         *
                         * Do NOT clear all appointments because
                         * Admin/Technician modules may also use
                         * the same Room table.
                         */
                        if (customerId != null
                                && !customerId.trim().isEmpty()) {

                            techFixDao
                                    .clearAppointmentsForCustomer(
                                            customerId
                                    );
                        }

                        List<AppointmentEntity> entities =
                                new ArrayList<>();

                        if (appointments != null) {

                            for (Appointment appointment :
                                    appointments) {

                                AppointmentEntity entity =
                                        AppointmentEntity
                                                .fromAppointment(
                                                        appointment
                                                );

                                if (entity != null) {

                                    entities.add(
                                            entity
                                    );
                                }
                            }
                        }

                        if (!entities.isEmpty()) {

                            techFixDao
                                    .insertAppointments(
                                            entities
                                    );
                        }

                    } catch (Exception ignored) {

                        /*
                         * Cache failure must not stop
                         * successful online data from
                         * reaching the UI.
                         */
                    }
                }
        );
    }


    // ==========================================
    // LOAD CUSTOMER APPOINTMENTS FROM ROOM
    // ==========================================

    private void loadCachedCustomerAppointments(
            String customerId,
            AppointmentCallback<List<Appointment>> callback,
            String networkError
    ) {

        executorService.execute(
                () -> {

                    try {

                        List<AppointmentEntity> entities;

                        if (customerId != null
                                && !customerId.trim().isEmpty()) {

                            entities =
                                    techFixDao
                                            .getAppointmentsByCustomer(
                                                    customerId
                                            );

                        } else {

                            entities =
                                    new ArrayList<>();
                        }

                        List<Appointment> cachedAppointments =
                                new ArrayList<>();

                        if (entities != null) {

                            for (AppointmentEntity entity :
                                    entities) {

                                if (entity != null) {

                                    cachedAppointments.add(
                                            entity.toAppointment()
                                    );
                                }
                            }
                        }

                        mainHandler.post(
                                () -> {

                                    if (!cachedAppointments.isEmpty()) {

                                        callback.onSuccess(
                                                cachedAppointments
                                        );

                                    } else {

                                        callback.onError(
                                                networkError
                                        );
                                    }
                                }
                        );

                    } catch (Exception e) {

                        mainHandler.post(
                                () -> callback.onError(
                                        networkError
                                )
                        );
                    }
                }
        );
    }


    // ==========================================
    // GET ONE CACHED APPOINTMENT
    // ==========================================

    public void getCachedAppointment(
            String appointmentId,
            AppointmentCallback<Appointment> callback
    ) {

        executorService.execute(
                () -> {

                    try {

                        AppointmentEntity entity =
                                techFixDao
                                        .getAppointmentById(
                                                appointmentId
                                        );

                        mainHandler.post(
                                () -> {

                                    if (entity != null) {

                                        callback.onSuccess(
                                                entity.toAppointment()
                                        );

                                    } else {

                                        callback.onError(
                                                "Appointment is not available offline"
                                        );
                                    }
                                }
                        );

                    } catch (Exception e) {

                        mainHandler.post(
                                () -> callback.onError(
                                        "Unable to read cached appointment"
                                )
                        );
                    }
                }
        );
    }


    // ==========================================
    // MANUAL CACHE CLEAR
    // ==========================================

    public void clearCustomerCache(
            String customerId
    ) {

        if (customerId == null
                || customerId.trim().isEmpty()) {

            return;
        }

        executorService.execute(
                () -> techFixDao
                        .clearAppointmentsForCustomer(
                                customerId
                        )
        );
    }
}