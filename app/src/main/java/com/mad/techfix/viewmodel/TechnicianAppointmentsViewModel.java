package com.mad.techfix.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mad.techfix.data.SessionManager;
import com.mad.techfix.data.local.database.AppDatabase;
import com.mad.techfix.models.Appointment;
import com.mad.techfix.repository.TechnicianRepository;

import java.util.ArrayList;
import java.util.List;

public class TechnicianAppointmentsViewModel
        extends AndroidViewModel {

    private final TechnicianRepository repository;
    private final SessionManager sessionManager;


    private final MutableLiveData<List<Appointment>>
            appointments =
            new MutableLiveData<>(
                    new ArrayList<>()
            );


    private final MutableLiveData<Boolean>
            isLoading =
            new MutableLiveData<>(false);


    private final MutableLiveData<String>
            errorMessage =
            new MutableLiveData<>();


    public TechnicianAppointmentsViewModel(
            @NonNull Application application
    ) {

        super(application);


        repository =
                new TechnicianRepository(
                        application,
                        AppDatabase.getInstance(
                                application
                        )
                );


        sessionManager =
                new SessionManager(
                        application
                );
    }


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


    public String getTechnicianName() {

        String name =
                sessionManager
                        .getUserName();


        if (name == null
                || name.trim().isEmpty()
                || name.equalsIgnoreCase(
                "User"
        )) {

            return "TECHFIX Technician";
        }


        return name.trim();
    }


    public void loadAppointments() {

        isLoading.setValue(
                true
        );


        String token =
                sessionManager
                        .getBearerToken();


        String userId =
                sessionManager
                        .getUserId();


        repository
                .getTechnicianAppointments(
                        token,
                        userId,

                        new TechnicianRepository
                                .RepositoryCallback<
                                List<Appointment>
                                >() {

                            @Override
                            public void onSuccess(
                                    List<Appointment> data
                            ) {

                                isLoading.setValue(
                                        false
                                );


                                appointments.setValue(
                                        data == null
                                                ? new ArrayList<>()
                                                : data
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


    public void refreshAppointments() {

        loadAppointments();
    }


    public void clearError() {

        errorMessage.setValue(
                null
        );
    }
}