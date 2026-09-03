package com.mad.techfix.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mad.techfix.data.SessionManager;
import com.mad.techfix.models.Device;
import com.mad.techfix.models.admin.Branch;
import com.mad.techfix.models.admin.Service;
import com.mad.techfix.repository.BookingRepository;

import java.util.ArrayList;
import java.util.List;

public class RepairBookingViewModel
        extends AndroidViewModel {

    private final BookingRepository repository;
    private final SessionManager sessionManager;


    private final MutableLiveData<List<Device>> devices =
            new MutableLiveData<>(
                    new ArrayList<>()
            );

    private final MutableLiveData<List<Service>> services =
            new MutableLiveData<>(
                    new ArrayList<>()
            );

    private final MutableLiveData<List<Branch>> branches =
            new MutableLiveData<>(
                    new ArrayList<>()
            );


    private final MutableLiveData<Boolean> isLoading =
            new MutableLiveData<>(
                    false
            );

    private final MutableLiveData<String> errorMessage =
            new MutableLiveData<>();


    private int pendingRequests = 0;


    public RepairBookingViewModel(
            @NonNull Application application
    ) {

        super(application);

        repository =
                new BookingRepository();

        sessionManager =
                new SessionManager(
                        application
                );
    }


    // ==========================================
    // LIVE DATA
    // ==========================================

    public LiveData<List<Device>>
    getDevices() {

        return devices;
    }


    public LiveData<List<Service>>
    getServices() {

        return services;
    }


    public LiveData<List<Branch>>
    getBranches() {

        return branches;
    }


    public LiveData<Boolean>
    getIsLoading() {

        return isLoading;
    }


    public LiveData<String>
    getErrorMessage() {

        return errorMessage;
    }


    // ==========================================
    // LOAD ALL BOOKING DATA
    // ==========================================

    public void loadBookingData() {

        pendingRequests = 3;

        isLoading.setValue(
                true
        );

        loadDevices();

        loadServices();

        loadBranches();
    }


    // ==========================================
    // DEVICES
    // ==========================================

    private void loadDevices() {

        String token =
                sessionManager
                        .getBearerToken();

        if (token == null
                || token.trim().isEmpty()) {

            devices.setValue(
                    new ArrayList<>()
            );

            postError(
                    "Please sign in again"
            );

            finishRequest();

            return;
        }


        repository
                .getDevices(
                        token,
                        new BookingRepository
                                .BookingCallback<
                                List<Device>
                                >() {

                            @Override
                            public void onSuccess(
                                    List<Device> data
                            ) {

                                if (data == null) {

                                    devices.setValue(
                                            new ArrayList<>()
                                    );

                                } else {

                                    devices.setValue(
                                            data
                                    );
                                }

                                finishRequest();
                            }


                            @Override
                            public void onError(
                                    String message
                            ) {

                                devices.setValue(
                                        new ArrayList<>()
                                );

                                postError(
                                        message
                                );

                                finishRequest();
                            }
                        }
                );
    }


    // ==========================================
    // SERVICES
    // ==========================================

    private void loadServices() {

        repository
                .getServices(
                        new BookingRepository
                                .BookingCallback<
                                List<Service>
                                >() {

                            @Override
                            public void onSuccess(
                                    List<Service> data
                            ) {

                                if (data == null) {

                                    services.setValue(
                                            new ArrayList<>()
                                    );

                                } else {

                                    services.setValue(
                                            data
                                    );
                                }

                                finishRequest();
                            }


                            @Override
                            public void onError(
                                    String message
                            ) {

                                services.setValue(
                                        new ArrayList<>()
                                );

                                postError(
                                        message
                                );

                                finishRequest();
                            }
                        }
                );
    }


    // ==========================================
    // BRANCHES
    // ==========================================

    private void loadBranches() {

        repository
                .getBranches(
                        new BookingRepository
                                .BookingCallback<
                                List<Branch>
                                >() {

                            @Override
                            public void onSuccess(
                                    List<Branch> data
                            ) {

                                if (data == null) {

                                    branches.setValue(
                                            new ArrayList<>()
                                    );

                                } else {

                                    branches.setValue(
                                            data
                                    );
                                }

                                finishRequest();
                            }


                            @Override
                            public void onError(
                                    String message
                            ) {

                                branches.setValue(
                                        new ArrayList<>()
                                );

                                postError(
                                        message
                                );

                                finishRequest();
                            }
                        }
                );
    }


    // ==========================================
    // REFRESH
    // ==========================================

    public void refreshBookingData() {

        loadBookingData();
    }


    // ==========================================
    // REQUEST TRACKING
    // ==========================================

    private void finishRequest() {

        pendingRequests--;

        if (pendingRequests <= 0) {

            pendingRequests = 0;

            isLoading.setValue(
                    false
            );
        }
    }


    // ==========================================
    // ERROR HANDLING
    // ==========================================

    private void postError(
            String message
    ) {

        if (message == null
                || message.trim().isEmpty()) {

            message =
                    "Unable to load booking information";
        }

        errorMessage.setValue(
                message
        );
    }


    public void clearError() {

        errorMessage.setValue(
                null
        );
    }
}