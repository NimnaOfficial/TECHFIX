package com.mad.techfix.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mad.techfix.data.SessionManager;
import com.mad.techfix.data.local.database.AppDatabase;
import com.mad.techfix.models.AppointmentDetail;
import com.mad.techfix.repository.TechnicianRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TechnicianRepairDetailViewModel
        extends AndroidViewModel {

    private final TechnicianRepository repository;
    private final SessionManager sessionManager;


    private final MutableLiveData<AppointmentDetail>
            repairDetail =
            new MutableLiveData<>();


    private final MutableLiveData<String>
            repairNote =
            new MutableLiveData<>(
                    "No repair notes added yet."
            );


    private final MutableLiveData<List<String>>
            repairImages =
            new MutableLiveData<>(
                    new ArrayList<>()
            );


    private final MutableLiveData<Boolean>
            isLoading =
            new MutableLiveData<>(false);


    private final MutableLiveData<Boolean>
            actionLoading =
            new MutableLiveData<>(false);


    private final MutableLiveData<String>
            successMessage =
            new MutableLiveData<>();


    private final MutableLiveData<String>
            errorMessage =
            new MutableLiveData<>();


    public TechnicianRepairDetailViewModel(
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


    // ==========================================
    // LIVE DATA
    // ==========================================

    public LiveData<AppointmentDetail>
    getRepairDetail() {

        return repairDetail;
    }


    public LiveData<String>
    getRepairNote() {

        return repairNote;
    }


    public LiveData<List<String>>
    getRepairImages() {

        return repairImages;
    }


    public LiveData<Boolean>
    getIsLoading() {

        return isLoading;
    }


    public LiveData<Boolean>
    getActionLoading() {

        return actionLoading;
    }


    public LiveData<String>
    getSuccessMessage() {

        return successMessage;
    }


    public LiveData<String>
    getErrorMessage() {

        return errorMessage;
    }


    // ==========================================
    // LOAD EVERYTHING
    // ==========================================

    public void loadRepairData(
            String appointmentId
    ) {

        loadAppointmentDetail(
                appointmentId
        );

        loadRepairNotes(
                appointmentId
        );

        loadRepairImages(
                appointmentId
        );
    }


    // ==========================================
    // DETAIL
    // ==========================================

    public void loadAppointmentDetail(
            String appointmentId
    ) {

        if (!validAppointmentId(
                appointmentId
        )) {

            errorMessage.setValue(
                    "Repair ID is unavailable"
            );

            return;
        }


        String token =
                sessionManager
                        .getBearerToken();


        if (!validToken(token)) {

            errorMessage.setValue(
                    "Please sign in again"
            );

            return;
        }


        isLoading.setValue(
                true
        );


        repository
                .getAppointmentDetail(
                        token,
                        appointmentId,

                        new TechnicianRepository
                                .RepositoryCallback<
                                AppointmentDetail
                                >() {

                            @Override
                            public void onSuccess(
                                    AppointmentDetail data
                            ) {

                                isLoading.setValue(
                                        false
                                );

                                repairDetail.setValue(
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

                                errorMessage.setValue(
                                        message
                                );
                            }
                        }
                );
    }


    // ==========================================
    // NOTES
    // ==========================================

    public void loadRepairNotes(
            String appointmentId
    ) {

        String token =
                sessionManager
                        .getBearerToken();


        if (!validToken(token)
                || !validAppointmentId(
                appointmentId
        )) {

            return;
        }


        repository
                .getRepairHistory(
                        token,
                        appointmentId,

                        new TechnicianRepository
                                .RepositoryCallback<
                                List<Object>
                                >() {

                            @Override
                            public void onSuccess(
                                    List<Object> history
                            ) {

                                repairNote.setValue(
                                        findLatestRepairNote(
                                                history
                                        )
                                );
                            }


                            @Override
                            public void onError(
                                    String message
                            ) {

                                /*
                                 * Notes are secondary data.
                                 * Main repair detail can still
                                 * operate if history is unavailable.
                                 */
                            }
                        }
                );
    }


    private String findLatestRepairNote(
            List<Object> history
    ) {

        if (history == null
                || history.isEmpty()) {

            return "No repair notes added yet.";
        }


        for (int i =
             history.size() - 1;
             i >= 0;
             i--) {

            Object item =
                    history.get(i);


            if (!(item instanceof Map)) {

                continue;
            }


            Map<?, ?> map =
                    (Map<?, ?>) item;


            Object noteValue =
                    map.get(
                            "note"
                    );


            if (noteValue == null) {

                continue;
            }


            String note =
                    String.valueOf(
                            noteValue
                    ).trim();


            if (note.isEmpty()) {

                continue;
            }


            if (note.startsWith(
                    "System "
            )) {

                continue;
            }


            if (note.equalsIgnoreCase(
                    "Status updated"
            )) {

                continue;
            }


            return note;
        }


        return "No repair notes added yet.";
    }


    // ==========================================
    // IMAGES
    // ==========================================

    public void loadRepairImages(
            String appointmentId
    ) {

        String token =
                sessionManager
                        .getBearerToken();


        if (!validToken(token)
                || !validAppointmentId(
                appointmentId
        )) {

            return;
        }


        repository
                .getRepairImages(
                        token,
                        appointmentId,

                        new TechnicianRepository
                                .RepositoryCallback<
                                List<Object>
                                >() {

                            @Override
                            public void onSuccess(
                                    List<Object> data
                            ) {

                                repairImages.setValue(
                                        extractImageUrls(
                                                data
                                        )
                                );
                            }


                            @Override
                            public void onError(
                                    String message
                            ) {

                                repairImages.setValue(
                                        new ArrayList<>()
                                );
                            }
                        }
                );
    }


    private List<String> extractImageUrls(
            List<Object> images
    ) {

        List<String> urls =
                new ArrayList<>();


        if (images == null) {

            return urls;
        }


        for (Object item :
                images) {

            if (!(item instanceof Map)) {

                continue;
            }


            Map<?, ?> map =
                    (Map<?, ?>) item;


            Object url =
                    map.get(
                            "image_url"
                    );


            if (url == null) {

                continue;
            }


            String value =
                    String.valueOf(
                            url
                    ).trim();


            if (!value.isEmpty()) {

                urls.add(
                        value
                );
            }
        }


        return urls;
    }


    // ==========================================
    // STATUS UPDATE
    // ==========================================

    public void updateRepairStatus(
            String appointmentId,
            String newStatus,
            String note
    ) {

        performStatusUpdate(
                appointmentId,
                newStatus,
                note,
                "Repair status updated"
        );
    }


    // ==========================================
    // SAVE REPAIR NOTE
    // ==========================================

    public void saveRepairNote(
            String appointmentId,
            String currentStatus,
            String note
    ) {

        if (note == null
                || note.trim().isEmpty()) {

            errorMessage.setValue(
                    "Please enter a repair note"
            );

            return;
        }


        performStatusUpdate(
                appointmentId,
                currentStatus,
                note.trim(),
                "Repair note saved"
        );
    }


    private void performStatusUpdate(
            String appointmentId,
            String status,
            String note,
            String successText
    ) {

        String token =
                sessionManager
                        .getBearerToken();


        if (!validToken(token)) {

            errorMessage.setValue(
                    "Please sign in again"
            );

            return;
        }


        if (!validAppointmentId(
                appointmentId
        )) {

            errorMessage.setValue(
                    "Repair ID is unavailable"
            );

            return;
        }


        if (status == null
                || status.trim().isEmpty()) {

            errorMessage.setValue(
                    "Repair status is missing"
            );

            return;
        }


        actionLoading.setValue(
                true
        );


        repository
                .updateRepairStatus(
                        token,
                        appointmentId,
                        status.trim(),
                        note == null
                                ? ""
                                : note.trim(),

                        new TechnicianRepository
                                .RepositoryCallback<Object>() {

                            @Override
                            public void onSuccess(
                                    Object data
                            ) {

                                actionLoading.setValue(
                                        false
                                );


                                successMessage.setValue(
                                        successText
                                );


                                loadAppointmentDetail(
                                        appointmentId
                                );


                                loadRepairNotes(
                                        appointmentId
                                );
                            }


                            @Override
                            public void onError(
                                    String message
                            ) {

                                actionLoading.setValue(
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
    // ADD IMAGE
    // ==========================================

    public void addRepairImage(
            String appointmentId,
            String imageUrl
    ) {

        if (imageUrl == null
                || imageUrl.trim().isEmpty()) {

            errorMessage.setValue(
                    "Image URL is required"
            );

            return;
        }


        String token =
                sessionManager
                        .getBearerToken();


        if (!validToken(token)) {

            errorMessage.setValue(
                    "Please sign in again"
            );

            return;
        }


        actionLoading.setValue(
                true
        );


        repository
                .addRepairImage(
                        token,
                        appointmentId,
                        imageUrl.trim(),

                        new TechnicianRepository
                                .RepositoryCallback<Object>() {

                            @Override
                            public void onSuccess(
                                    Object data
                            ) {

                                actionLoading.setValue(
                                        false
                                );


                                successMessage.setValue(
                                        "Repair image added"
                                );


                                loadRepairImages(
                                        appointmentId
                                );
                            }


                            @Override
                            public void onError(
                                    String message
                            ) {

                                actionLoading.setValue(
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
    // RESET EVENTS
    // ==========================================

    public void clearSuccessMessage() {

        successMessage.setValue(
                null
        );
    }


    public void clearError() {

        errorMessage.setValue(
                null
        );
    }


    // ==========================================
    // VALIDATION
    // ==========================================

    private boolean validToken(
            String token
    ) {

        return token != null
                && !token.trim().isEmpty();
    }


    private boolean validAppointmentId(
            String appointmentId
    ) {

        return appointmentId != null
                && !appointmentId.trim().isEmpty();
    }
}