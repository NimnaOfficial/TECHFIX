package com.mad.techfix.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mad.techfix.data.SessionManager;
import com.mad.techfix.data.local.database.AppDatabase;
import com.mad.techfix.models.AppointmentDetail;
import com.mad.techfix.repository.CustomerAppointmentDetailRepository;

public class CustomerAppointmentDetailViewModel
        extends AndroidViewModel {

    private final CustomerAppointmentDetailRepository repository;
    private final SessionManager sessionManager;


    private final MutableLiveData<AppointmentDetail>
            appointmentDetail =
            new MutableLiveData<>();


    private final MutableLiveData<Boolean>
            isLoading =
            new MutableLiveData<>(false);


    private final MutableLiveData<String>
            errorMessage =
            new MutableLiveData<>();


    public CustomerAppointmentDetailViewModel(
            @NonNull Application application
    ) {

        super(application);


        repository =
                new CustomerAppointmentDetailRepository(
                        AppDatabase.getInstance(
                                application
                        )
                );


        sessionManager =
                new SessionManager(
                        application
                );
    }


    // ==========================================
    // LIVE DATA
    // ==========================================

    public LiveData<AppointmentDetail>
    getAppointmentDetail() {

        return appointmentDetail;
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
    // LOAD DETAIL
    // ==========================================

    public void loadAppointmentDetail(
            String appointmentId
    ) {

        if (appointmentId == null
                || appointmentId.trim().isEmpty()) {

            errorMessage.setValue(
                    "Appointment ID is missing"
            );

            return;
        }


        isLoading.setValue(
                true
        );


        String token =
                sessionManager
                        .getBearerToken();


        repository
                .getAppointmentDetail(
                        token,
                        appointmentId,

                        new CustomerAppointmentDetailRepository
                                .DetailCallback() {

                            @Override
                            public void onSuccess(
                                    AppointmentDetail detail
                            ) {

                                isLoading.setValue(
                                        false
                                );

                                appointmentDetail.setValue(
                                        detail
                                );
                            }


                            @Override
                            public void onError(
                                    String message
                            ) {

                                isLoading.setValue(
                                        false
                                );

                                errorMessage.setValue(
                                        message
                                );
                            }
                        }
                );
    }


    // ==========================================
    // RESET
    // ==========================================

    public void clearError() {

        errorMessage.setValue(
                null
        );
    }
}