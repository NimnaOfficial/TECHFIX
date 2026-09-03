package com.mad.techfix.repository;

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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerAppointmentDetailRepository {

    private final ApiService apiService;
    private final TechFixDao techFixDao;

    private final ExecutorService executorService =
            Executors.newSingleThreadExecutor();

    private final Handler mainHandler =
            new Handler(Looper.getMainLooper());


    public interface DetailCallback {

        void onSuccess(AppointmentDetail detail);

        void onError(String message);
    }


    public CustomerAppointmentDetailRepository(
            AppDatabase database
    ) {

        apiService =
                RetrofitClient.getApiService();

        techFixDao =
                database.techFixDao();
    }


    // ==========================================
    // NETWORK FIRST -> ROOM FALLBACK
    // ==========================================

    public void getAppointmentDetail(
            String token,
            String appointmentId,
            DetailCallback callback
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

            loadCachedAppointment(
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
                                            response.body().getData()
                                    );

                                    return;
                                }


                                loadCachedAppointment(
                                        appointmentId,
                                        callback,
                                        getResponseMessage(
                                                response,
                                                "Unable to load appointment details"
                                        )
                                );
                            }


                            @Override
                            public void onFailure(
                                    Call<ApiResponse<AppointmentDetail>> call,
                                    Throwable throwable
                            ) {

                                String message =
                                        "Network error";

                                if (throwable != null
                                        && throwable.getMessage() != null
                                        && !throwable.getMessage()
                                        .trim()
                                        .isEmpty()) {

                                    message =
                                            throwable.getMessage();
                                }


                                loadCachedAppointment(
                                        appointmentId,
                                        callback,
                                        message
                                );
                            }
                        }
                );
    }


    // ==========================================
    // ROOM FALLBACK
    // ==========================================

    private void loadCachedAppointment(
            String appointmentId,
            DetailCallback callback,
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


                        Appointment appointment =
                                entity.toAppointment();


                        AppointmentDetail detail =
                                convertToDetail(
                                        appointment
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
    // CACHED APPOINTMENT -> DETAIL MODEL
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


        // Device
        detail.setDevice_brand(
                appointment.getDevice_brand()
        );

        detail.setDevice_model(
                appointment.getDevice_model()
        );

        detail.setSerial_number(
                appointment.getDevice_serial_number()
        );


        // Service
        detail.setService_name(
                appointment.getService_name()
        );


        // Branch
        detail.setBranch_name(
                appointment.getBranch_name()
        );

        detail.setBranch_city(
                appointment.getBranch_city()
        );


        // Customer
        detail.setCustomer_first_name(
                appointment.getCustomer_first_name()
        );

        detail.setCustomer_last_name(
                appointment.getCustomer_last_name()
        );


        // Technician
        detail.setTechnician_first_name(
                appointment.getTechnician_first_name()
        );

        detail.setTechnician_last_name(
                appointment.getTechnician_last_name()
        );


        return detail;
    }


    // ==========================================
    // ERROR MESSAGE
    // ==========================================

    private String getResponseMessage(
            Response<ApiResponse<AppointmentDetail>> response,
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
}