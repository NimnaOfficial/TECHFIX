package com.mad.techfix.repository;

import com.mad.techfix.models.ApiResponse;
import com.mad.techfix.models.CreateAppointmentRequest;
import com.mad.techfix.models.Device;
import com.mad.techfix.models.admin.Branch;
import com.mad.techfix.models.admin.Service;
import com.mad.techfix.network.ApiService;
import com.mad.techfix.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingRepository {

    private final ApiService apiService;


    public interface BookingCallback<T> {

        void onSuccess(T data);

        void onError(String message);
    }


    public BookingRepository() {

        apiService =
                RetrofitClient.getApiService();
    }


    // ==========================================
    // LOAD CUSTOMER DEVICES
    // ==========================================

    public void getDevices(
            String token,
            BookingCallback<List<Device>> callback
    ) {

        if (token == null
                || token.trim().isEmpty()) {

            callback.onError(
                    "Authentication token is missing"
            );

            return;
        }


        apiService
                .getMyDevices(token)
                .enqueue(
                        new Callback<
                                ApiResponse<
                                        List<Device>
                                        >
                                >() {

                            @Override
                            public void onResponse(
                                    Call<
                                            ApiResponse<
                                                    List<Device>
                                                    >
                                            > call,

                                    Response<
                                            ApiResponse<
                                                    List<Device>
                                                    >
                                            > response
                            ) {

                                if (response.isSuccessful()
                                        && response.body() != null
                                        && response.body().isSuccess()) {

                                    List<Device> devices =
                                            response.body()
                                                    .getData();

                                    if (devices == null) {

                                        devices =
                                                new ArrayList<>();
                                    }

                                    callback.onSuccess(
                                            devices
                                    );

                                } else {

                                    callback.onError(
                                            getApiMessage(
                                                    response,
                                                    "Unable to load devices"
                                            )
                                    );
                                }
                            }


                            @Override
                            public void onFailure(
                                    Call<
                                            ApiResponse<
                                                    List<Device>
                                                    >
                                            > call,

                                    Throwable t
                            ) {

                                callback.onError(
                                        getThrowableMessage(
                                                t,
                                                "Unable to load devices"
                                        )
                                );
                            }
                        }
                );
    }


    // ==========================================
    // LOAD SERVICES
    // ==========================================

    public void getServices(
            BookingCallback<List<Service>> callback
    ) {

        apiService
                .getServices()
                .enqueue(
                        new Callback<
                                ApiResponse<
                                        List<Service>
                                        >
                                >() {

                            @Override
                            public void onResponse(
                                    Call<
                                            ApiResponse<
                                                    List<Service>
                                                    >
                                            > call,

                                    Response<
                                            ApiResponse<
                                                    List<Service>
                                                    >
                                            > response
                            ) {

                                if (response.isSuccessful()
                                        && response.body() != null
                                        && response.body().isSuccess()) {

                                    List<Service> services =
                                            response.body()
                                                    .getData();

                                    if (services == null) {

                                        services =
                                                new ArrayList<>();
                                    }

                                    callback.onSuccess(
                                            services
                                    );

                                } else {

                                    callback.onError(
                                            getApiMessage(
                                                    response,
                                                    "Unable to load services"
                                            )
                                    );
                                }
                            }


                            @Override
                            public void onFailure(
                                    Call<
                                            ApiResponse<
                                                    List<Service>
                                                    >
                                            > call,

                                    Throwable t
                            ) {

                                callback.onError(
                                        getThrowableMessage(
                                                t,
                                                "Unable to load services"
                                        )
                                );
                            }
                        }
                );
    }


    // ==========================================
    // LOAD BRANCHES
    // ==========================================

    public void getBranches(
            BookingCallback<List<Branch>> callback
    ) {

        apiService
                .getBranches()
                .enqueue(
                        new Callback<
                                ApiResponse<
                                        List<Branch>
                                        >
                                >() {

                            @Override
                            public void onResponse(
                                    Call<
                                            ApiResponse<
                                                    List<Branch>
                                                    >
                                            > call,

                                    Response<
                                            ApiResponse<
                                                    List<Branch>
                                                    >
                                            > response
                            ) {

                                if (response.isSuccessful()
                                        && response.body() != null
                                        && response.body().isSuccess()) {

                                    List<Branch> branches =
                                            response.body()
                                                    .getData();

                                    if (branches == null) {

                                        branches =
                                                new ArrayList<>();
                                    }

                                    callback.onSuccess(
                                            branches
                                    );

                                } else {

                                    callback.onError(
                                            getApiMessage(
                                                    response,
                                                    "Unable to load branches"
                                            )
                                    );
                                }
                            }


                            @Override
                            public void onFailure(
                                    Call<
                                            ApiResponse<
                                                    List<Branch>
                                                    >
                                            > call,

                                    Throwable t
                            ) {

                                callback.onError(
                                        getThrowableMessage(
                                                t,
                                                "Unable to load branches"
                                        )
                                );
                            }
                        }
                );
    }


    // ==========================================
    // CREATE APPOINTMENT
    // ==========================================

    public void createAppointment(
            String token,
            CreateAppointmentRequest request,
            BookingCallback<Map<String, Object>> callback
    ) {

        if (token == null
                || token.trim().isEmpty()) {

            callback.onError(
                    "Authentication token is missing"
            );

            return;
        }


        if (request == null) {

            callback.onError(
                    "Booking information is missing"
            );

            return;
        }


        apiService
                .createAppointmentRequest(
                        token,
                        request
                )
                .enqueue(
                        new Callback<
                                ApiResponse<
                                        Map<String, Object>
                                        >
                                >() {

                            @Override
                            public void onResponse(
                                    Call<
                                            ApiResponse<
                                                    Map<String, Object>
                                                    >
                                            > call,

                                    Response<
                                            ApiResponse<
                                                    Map<String, Object>
                                                    >
                                            > response
                            ) {

                                if (response.isSuccessful()
                                        && response.body() != null
                                        && response.body().isSuccess()) {

                                    Map<String, Object> data =
                                            response.body()
                                                    .getData();

                                    callback.onSuccess(
                                            data
                                    );

                                } else {

                                    callback.onError(
                                            getApiMessage(
                                                    response,
                                                    "Unable to create appointment"
                                            )
                                    );
                                }
                            }


                            @Override
                            public void onFailure(
                                    Call<
                                            ApiResponse<
                                                    Map<String, Object>
                                                    >
                                            > call,

                                    Throwable t
                            ) {

                                callback.onError(
                                        getThrowableMessage(
                                                t,
                                                "Unable to create appointment"
                                        )
                                );
                            }
                        }
                );
    }


    // ==========================================
    // ERROR HELPERS
    // ==========================================

    private <T> String getApiMessage(
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