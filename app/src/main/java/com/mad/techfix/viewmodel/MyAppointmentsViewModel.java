package com.mad.techfix.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mad.techfix.data.SessionManager;
import com.mad.techfix.data.local.database.AppDatabase;
import com.mad.techfix.models.Appointment;
import com.mad.techfix.repository.AppointmentRepository;

import java.util.ArrayList;
import java.util.List;

public class MyAppointmentsViewModel
        extends AndroidViewModel {

    private final AppointmentRepository repository;
    private final SessionManager sessionManager;

    private final MutableLiveData<List<Appointment>> appointments =
            new MutableLiveData<>(new ArrayList<>());

    private final MutableLiveData<Boolean> isLoading =
            new MutableLiveData<>(false);

    private final MutableLiveData<String> errorMessage =
            new MutableLiveData<>();


    public MyAppointmentsViewModel(
            @NonNull Application application
    ) {

        super(application);

        AppDatabase database =
                AppDatabase.getInstance(
                        application
                );

        repository =
                new AppointmentRepository(
                        database
                );

        sessionManager =
                new SessionManager(
                        application
                );
    }


    // ==========================================
    // LIVE DATA
    // ==========================================

    public LiveData<List<Appointment>>
    getAppointments() {

        return appointments;
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
    // LOAD CUSTOMER APPOINTMENTS
    // ==========================================

    public void loadAppointments() {

        String token =
                sessionManager
                        .getBearerToken();

        String customerId =
                sessionManager
                        .getUserId();


        if (customerId == null
                || customerId.trim().isEmpty()) {

            isLoading.setValue(false);

            errorMessage.setValue(
                    "Customer account information is unavailable"
            );

            return;
        }


        isLoading.setValue(true);


        repository
                .getCustomerAppointments(
                        token,
                        customerId,
                        new AppointmentRepository
                                .AppointmentCallback<
                                List<Appointment>
                                >() {

                            @Override
                            public void onSuccess(
                                    List<Appointment> data
                            ) {

                                isLoading.setValue(
                                        false
                                );

                                if (data == null) {

                                    appointments.setValue(
                                            new ArrayList<>()
                                    );

                                } else {

                                    appointments.setValue(
                                            data
                                    );
                                }
                            }


                            @Override
                            public void onError(
                                    String message
                            ) {

                                isLoading.setValue(
                                        false
                                );

                                appointments.setValue(
                                        new ArrayList<>()
                                );

                                if (message == null
                                        || message.trim()
                                        .isEmpty()) {

                                    message =
                                            "Unable to load appointments";
                                }

                                errorMessage.setValue(
                                        message
                                );
                            }
                        }
                );
    }


    // ==========================================
    // REFRESH
    // ==========================================

    public void refreshAppointments() {

        loadAppointments();
    }


    // ==========================================
    // CLEAR CUSTOMER CACHE
    // ==========================================

    public void clearAppointmentCache() {

        String customerId =
                sessionManager
                        .getUserId();

        if (customerId == null
                || customerId.trim().isEmpty()) {

            return;
        }

        repository
                .clearCustomerCache(
                        customerId
                );
    }


    // ==========================================
    // CLEAR CONSUMED ERROR
    // ==========================================

    public void clearError() {

        errorMessage.setValue(
                null
        );
    }
}