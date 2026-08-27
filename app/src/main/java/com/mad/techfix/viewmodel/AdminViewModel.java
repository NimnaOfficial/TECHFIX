package com.mad.techfix.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.mad.techfix.data.local.database.AppDatabase;
import com.mad.techfix.models.Appointment;
import com.mad.techfix.models.Branch;
import com.mad.techfix.models.DashboardResponse;
import com.mad.techfix.models.Service;
import com.mad.techfix.models.Technician;
import com.mad.techfix.repository.AdminRepository;
import com.mad.techfix.utils.TokenManager;

import java.util.List;

public class AdminViewModel extends AndroidViewModel {

    private final AdminRepository repository;
    private final TokenManager tokenManager;

    private final MutableLiveData<DashboardResponse.DashboardData> dashboardData = new MutableLiveData<>();
    private final MutableLiveData<List<Branch>> branches = new MutableLiveData<>();
    private final MutableLiveData<List<Technician>> technicians = new MutableLiveData<>();
    private final MutableLiveData<List<Service>> technicianServices = new MutableLiveData<>();
    private final MutableLiveData<List<Appointment>> allAppointments = new MutableLiveData<>();
    private final MutableLiveData<List<Service>> allServices = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> assignmentSuccess = new MutableLiveData<>();
    private final MutableLiveData<Boolean> skillUpdateSuccess = new MutableLiveData<>();

    public AdminViewModel(@NonNull Application application) {
        super(application);
        // Assuming AppDatabase has a getInstance(Context) method
        AppDatabase database = AppDatabase.getInstance(application);
        repository = new AdminRepository(database);
        // TokenManager is usually instantiated with context
        tokenManager = new TokenManager(application);
    }

    public MutableLiveData<DashboardResponse.DashboardData> getDashboardData() { return dashboardData; }
    public MutableLiveData<List<Branch>> getBranches() { return branches; }
    public MutableLiveData<List<Technician>> getTechnicians() { return technicians; }
    public MutableLiveData<List<Service>> getTechnicianServices() { return technicianServices; }
    public MutableLiveData<List<Appointment>> getAllAppointments() { return allAppointments; }
    public MutableLiveData<List<Service>> getAllServices() { return allServices; }
    public MutableLiveData<Boolean> getIsLoading() { return isLoading; }
    public MutableLiveData<String> getErrorMessage() { return errorMessage; }
    public MutableLiveData<Boolean> getAssignmentSuccess() { return assignmentSuccess; }
    public MutableLiveData<Boolean> getSkillUpdateSuccess() { return skillUpdateSuccess; }

    public void loadDashboard() {
        String token = tokenManager.getToken();
        if (token == null) {
            errorMessage.setValue("Authentication error");
            return;
        }

        isLoading.setValue(true);
        repository.fetchDashboard(token, new AdminRepository.AdminCallback<DashboardResponse.DashboardData>() {
            @Override
            public void onSuccess(DashboardResponse.DashboardData data) {
                isLoading.setValue(false);
                dashboardData.setValue(data);
            }

            @Override
            public void onError(String message) {
                isLoading.setValue(false);
                errorMessage.setValue(message);
            }
        });
    }

    public void loadBranches() {
        String token = tokenManager.getToken();
        if (token == null) return;

        isLoading.setValue(true);
        repository.fetchBranches(token, new AdminRepository.AdminCallback<List<Branch>>() {
            @Override
            public void onSuccess(List<Branch> data) {
                isLoading.setValue(false);
                branches.setValue(data);
            }

            @Override
            public void onError(String message) {
                isLoading.setValue(false);
                errorMessage.setValue(message);
            }
        });
    }

    public void loadTechnicians() {
        String token = tokenManager.getToken();
        if (token == null) return;

        isLoading.setValue(true);
        repository.fetchTechnicians(token, new AdminRepository.AdminCallback<List<Technician>>() {
            @Override
            public void onSuccess(List<Technician> data) {
                isLoading.setValue(false);
                technicians.setValue(data);
            }

            @Override
            public void onError(String message) {
                isLoading.setValue(false);
                errorMessage.setValue(message);
            }
        });
    }

    public void loadTechnicianServices(String techId) {
        String token = tokenManager.getToken();
        if (token == null) return;

        isLoading.setValue(true);
        repository.fetchTechnicianServices(token, techId, new AdminRepository.AdminCallback<List<Service>>() {
            @Override
            public void onSuccess(List<Service> data) {
                isLoading.setValue(false);
                technicianServices.setValue(data);
            }

            @Override
            public void onError(String message) {
                isLoading.setValue(false);
                errorMessage.setValue(message);
            }
        });
    }

    public void loadAllAppointments() {
        String token = tokenManager.getToken();
        if (token == null) return;

        isLoading.setValue(true);
        repository.fetchAllAppointments(token, new AdminRepository.AdminCallback<List<Appointment>>() {
            @Override
            public void onSuccess(List<Appointment> data) {
                isLoading.setValue(false);
                allAppointments.setValue(data);
            }

            @Override
            public void onError(String message) {
                isLoading.setValue(false);
                errorMessage.setValue(message);
            }
        });
    }

    public void loadAllServices() {
        String token = tokenManager.getToken();
        if (token == null) return;

        isLoading.setValue(true);
        repository.fetchAllServices(token, new AdminRepository.AdminCallback<List<Service>>() {
            @Override
            public void onSuccess(List<Service> data) {
                isLoading.setValue(false);
                allServices.setValue(data);
            }

            @Override
            public void onError(String message) {
                isLoading.setValue(false);
                errorMessage.setValue(message);
            }
        });
    }

    public void assignTechnician(String appointmentId, String technicianId) {
        String token = tokenManager.getToken();
        if (token == null) return;

        isLoading.setValue(true);
        repository.assignTechnician(token, appointmentId, technicianId, new AdminRepository.AdminCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                isLoading.setValue(false);
                assignmentSuccess.setValue(true);
            }

            @Override
            public void onError(String message) {
                isLoading.setValue(false);
                errorMessage.setValue(message);
                assignmentSuccess.setValue(false);
            }
        });
    }

    public void updateTechnicianServices(String techId, List<String> serviceIds) {
        String token = tokenManager.getToken();
        if (token == null) return;

        isLoading.setValue(true);
        repository.updateTechnicianServices(token, techId, serviceIds, new AdminRepository.AdminCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                isLoading.setValue(false);
                skillUpdateSuccess.setValue(true);
            }

            @Override
            public void onError(String message) {
                isLoading.setValue(false);
                errorMessage.setValue(message);
                skillUpdateSuccess.setValue(false);
            }
        });
    }
}
