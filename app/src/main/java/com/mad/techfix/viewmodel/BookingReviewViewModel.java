package com.mad.techfix.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mad.techfix.data.SessionManager;
import com.mad.techfix.models.CreateAppointmentRequest;
import com.mad.techfix.repository.BookingRepository;

import java.util.Map;

public class BookingReviewViewModel
        extends AndroidViewModel {

    private final BookingRepository repository;
    private final SessionManager sessionManager;

    private final MutableLiveData<Boolean> isLoading =
            new MutableLiveData<>(false);

    private final MutableLiveData<Map<String, Object>> bookingResult =
            new MutableLiveData<>();

    private final MutableLiveData<String> errorMessage =
            new MutableLiveData<>();


    public BookingReviewViewModel(
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

    public LiveData<Boolean> getIsLoading() {

        return isLoading;
    }


    public LiveData<Map<String, Object>>
    getBookingResult() {

        return bookingResult;
    }


    public LiveData<String>
    getErrorMessage() {

        return errorMessage;
    }


    // ==========================================
    // CREATE APPOINTMENT
    // ==========================================

    public void createAppointment(
            String deviceId,
            String serviceId,
            String branchId,
            String requestedDate,
            String requestedTime,
            String problemDescription,
            double customerLatitude,
            double customerLongitude
    ) {

        if (isLoading.getValue() != null
                && isLoading.getValue()) {

            return;
        }


        if (deviceId == null
                || deviceId.trim().isEmpty()) {

            postError(
                    "Device information is missing"
            );

            return;
        }


        if (serviceId == null
                || serviceId.trim().isEmpty()) {

            postError(
                    "Service information is missing"
            );

            return;
        }


        if (branchId == null
                || branchId.trim().isEmpty()) {

            postError(
                    "Branch information is missing"
            );

            return;
        }


        if (requestedDate == null
                || requestedDate.trim().isEmpty()) {

            postError(
                    "Appointment date is missing"
            );

            return;
        }


        if (requestedTime == null
                || requestedTime.trim().isEmpty()) {

            postError(
                    "Appointment time is missing"
            );

            return;
        }


        if (problemDescription == null
                || problemDescription.trim().isEmpty()) {

            postError(
                    "Please describe the device problem"
            );

            return;
        }


        String token =
                sessionManager
                        .getBearerToken();


        if (token == null
                || token.trim().isEmpty()) {

            postError(
                    "Please sign in again"
            );

            return;
        }


        CreateAppointmentRequest request =
                new CreateAppointmentRequest(
                        deviceId,
                        serviceId,
                        branchId,
                        requestedDate,
                        requestedTime,
                        problemDescription.trim(),
                        customerLatitude,
                        customerLongitude
                );


        isLoading.setValue(
                true
        );


        repository
                .createAppointment(
                        token,
                        request,
                        new BookingRepository
                                .BookingCallback<
                                Map<String, Object>
                                >() {

                            @Override
                            public void onSuccess(
                                    Map<String, Object> data
                            ) {

                                isLoading.setValue(
                                        false
                                );

                                bookingResult.setValue(
                                        data
                                );
                            }


                            @Override
                            public void onError(
                                    String message
                            ) {

                                isLoading.setValue(
                                        false
                                );

                                postError(
                                        message
                                );
                            }
                        }
                );
    }


    // ==========================================
    // RESULT / ERROR RESET
    // ==========================================

    public void clearBookingResult() {

        bookingResult.setValue(
                null
        );
    }


    public void clearError() {

        errorMessage.setValue(
                null
        );
    }


    private void postError(
            String message
    ) {

        if (message == null
                || message.trim().isEmpty()) {

            message =
                    "Unable to create appointment";
        }

        errorMessage.setValue(
                message
        );
    }
}