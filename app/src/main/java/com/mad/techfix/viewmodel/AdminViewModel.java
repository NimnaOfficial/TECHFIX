package com.mad.techfix.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.mad.techfix.models.Appointment;
import com.mad.techfix.models.admin.Branch;
import com.mad.techfix.models.admin.DashboardResponse;
import com.mad.techfix.models.admin.Service;
import com.mad.techfix.models.admin.Technician;
import com.mad.techfix.models.admin.SysAdminOverviewResponse;
import com.mad.techfix.models.admin.Manager;
import com.mad.techfix.repository.AdminRepository;
import com.mad.techfix.data.local.database.AppDatabase;
import com.mad.techfix.data.SessionManager;

import java.util.List;

public class AdminViewModel extends AndroidViewModel {
    private final AdminRepository repository;
    private final SessionManager sessionManager;

    private final MutableLiveData<DashboardResponse.DashboardData> dashboardData = new MutableLiveData<>();
    private final MutableLiveData<SysAdminOverviewResponse> systemOverview = new MutableLiveData<>();
    private final MutableLiveData<com.mad.techfix.models.admin.UserMonitorResponse> userMonitor = new MutableLiveData<>();
    public MutableLiveData<com.mad.techfix.models.admin.UserMonitorResponse> getUserMonitor() { return userMonitor; }
    private final MutableLiveData<List<com.mad.techfix.models.admin.LogEntry>> systemLogs = new MutableLiveData<>();
    public MutableLiveData<List<com.mad.techfix.models.admin.LogEntry>> getSystemLogs() { return systemLogs; }
    private final MutableLiveData<List<Manager>> managers = new MutableLiveData<>();
    private final MutableLiveData<List<Branch>> branches = new MutableLiveData<>();
    private final MutableLiveData<List<Technician>> technicians = new MutableLiveData<>();
    private final MutableLiveData<List<Service>> technicianServices = new MutableLiveData<>();
    private final MutableLiveData<List<Appointment>> allAppointments = new MutableLiveData<>();
    private final MutableLiveData<List<Service>> allServices = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> assignmentSuccess = new MutableLiveData<>();
    private final MutableLiveData<Boolean> skillUpdateSuccess = new MutableLiveData<>();
    private final MutableLiveData<Boolean> crudSuccess = new MutableLiveData<>();

    public AdminViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getInstance(application);
        repository = new AdminRepository(db);
        sessionManager = new SessionManager(application);
    }

    public MutableLiveData<DashboardResponse.DashboardData> getDashboardData() { return dashboardData; }
    public MutableLiveData<SysAdminOverviewResponse> getSystemOverview() { return systemOverview; }
    public MutableLiveData<List<Manager>> getManagers() { return managers; }
    public MutableLiveData<List<Branch>> getBranches() { return branches; }
    public MutableLiveData<List<Technician>> getTechnicians() { return technicians; }
    public MutableLiveData<List<Service>> getTechnicianServices() { return technicianServices; }
    public MutableLiveData<List<Appointment>> getAllAppointments() { return allAppointments; }
    public MutableLiveData<List<Service>> getAllServices() { return allServices; }
    public MutableLiveData<Boolean> getIsLoading() { return isLoading; }
    public MutableLiveData<String> getErrorMessage() { return errorMessage; }
    public MutableLiveData<Boolean> getAssignmentSuccess() { return assignmentSuccess; }
    public MutableLiveData<Boolean> getSkillUpdateSuccess() { return skillUpdateSuccess; }
    public MutableLiveData<Boolean> getCrudSuccess() { return crudSuccess; }

    private String getToken() {
        String token = sessionManager.getBearerToken();
        if (token == null || token.trim().isEmpty()) {
            errorMessage.setValue("Authentication token not found");
            return null;
        }
        return token;
    }

            public void loadUserMonitor(String userId) {
        String token = getToken();
        if (token == null) return;
        isLoading.setValue(true);
        repository.getUserMonitor(token, userId, new AdminRepository.AdminCallback<com.mad.techfix.models.admin.UserMonitorResponse>() {
            @Override
            public void onSuccess(com.mad.techfix.models.admin.UserMonitorResponse data) {
                isLoading.setValue(false);
                userMonitor.setValue(data);
            }
            @Override
            public void onError(String error) {
                isLoading.setValue(false);
                errorMessage.setValue(error);
            }
        });
    }

    public void loadSystemLogs() {
        String token = getToken();
        if (token == null) return;
        isLoading.setValue(true);
        repository.getSystemLogs(token, new AdminRepository.AdminCallback<List<com.mad.techfix.models.admin.LogEntry>>() {
            @Override
            public void onSuccess(List<com.mad.techfix.models.admin.LogEntry> data) {
                isLoading.setValue(false);
                systemLogs.setValue(data);
            }
            @Override
            public void onError(String error) {
                isLoading.setValue(false);
                errorMessage.setValue(error);
            }
        });
    }

    public void clearSystemLogs() {
        String token = getToken();
        if (token == null) return;
        isLoading.setValue(true);
        repository.clearSystemLogs(token, new AdminRepository.AdminCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                isLoading.setValue(false);
                crudSuccess.setValue(true);
                loadSystemLogs();
            }
            @Override
            public void onError(String error) {
                isLoading.setValue(false);
                errorMessage.setValue(error);
                crudSuccess.setValue(false);
            }
        });
    }

    public void loadSystemOverview() {
        String token = getToken();
        if (token == null) return;
        isLoading.setValue(true);
        repository.getSystemOverview(token, new AdminRepository.AdminCallback<SysAdminOverviewResponse>() {
            @Override
            public void onSuccess(SysAdminOverviewResponse data) {
                isLoading.setValue(false);
                systemOverview.setValue(data);
            }
            @Override
            public void onError(String error) {
                isLoading.setValue(false);
                errorMessage.setValue(error);
            }
        });
    }

    public void loadManagers() {
        String token = getToken();
        if (token == null) return;
        isLoading.setValue(true);
        repository.getManagers(token, new AdminRepository.AdminCallback<List<Manager>>() {
            @Override
            public void onSuccess(List<Manager> data) {
                isLoading.setValue(false);
                managers.setValue(data);
            }
            @Override
            public void onError(String error) {
                isLoading.setValue(false);
                errorMessage.setValue(error);
            }
        });
    }

    public void createManager(Manager manager) {
        String token = getToken();
        if (token == null) return;
        isLoading.setValue(true);
        repository.createManager(token, manager, new AdminRepository.AdminCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                isLoading.setValue(false);
                crudSuccess.setValue(true);
                loadManagers();
            }
            @Override
            public void onError(String error) {
                isLoading.setValue(false);
                errorMessage.setValue(error);
                crudSuccess.setValue(false);
            }
        });
    }

    public void updateManager(String id, Manager manager) {
        String token = getToken();
        if (token == null) return;
        isLoading.setValue(true);
        repository.updateManager(token, id, manager, new AdminRepository.AdminCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                isLoading.setValue(false);
                crudSuccess.setValue(true);
                loadManagers();
            }
            @Override
            public void onError(String error) {
                isLoading.setValue(false);
                errorMessage.setValue(error);
                crudSuccess.setValue(false);
            }
        });
    }

    public void deleteManager(String id) {
        String token = getToken();
        if (token == null) return;
        isLoading.setValue(true);
        repository.deleteManager(token, id, new AdminRepository.AdminCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                isLoading.setValue(false);
                crudSuccess.setValue(true);
                loadManagers();
            }
            @Override
            public void onError(String error) {
                isLoading.setValue(false);
                errorMessage.setValue(error);
                crudSuccess.setValue(false);
            }
        });
    }

    public void loadDashboard() {
        String token = getToken();
        if (token == null) return;
        isLoading.setValue(true);
        repository.getDashboardData(token, new AdminRepository.AdminCallback<DashboardResponse.DashboardData>() {
            @Override
            public void onSuccess(DashboardResponse.DashboardData data) {
                isLoading.setValue(false);
                dashboardData.setValue(data);
            }
            @Override
            public void onError(String error) {
                isLoading.setValue(false);
                errorMessage.setValue(error);
            }
        });
    }

    public void loadBranches() {
        String token = getToken();
        if (token == null) return;
        isLoading.setValue(true);
        repository.getBranches(token, new AdminRepository.AdminCallback<List<Branch>>() {
            @Override
            public void onSuccess(List<Branch> data) {
                isLoading.setValue(false);
                branches.setValue(data);
            }
            @Override
            public void onError(String error) {
                isLoading.setValue(false);
                errorMessage.setValue(error);
            }
        });
    }

    public void loadTechnicians() {
        String token = getToken();
        if (token == null) return;
        isLoading.setValue(true);
        repository.getTechnicians(token, new AdminRepository.AdminCallback<List<Technician>>() {
            @Override
            public void onSuccess(List<Technician> data) {
                isLoading.setValue(false);
                technicians.setValue(data);
            }
            @Override
            public void onError(String error) {
                isLoading.setValue(false);
                errorMessage.setValue(error);
            }
        });
    }

    public void loadTechnicianServices(String techId) {
        String token = getToken();
        if (token == null) return;
        isLoading.setValue(true);
        repository.getTechnicianServices(token, techId, new AdminRepository.AdminCallback<List<Service>>() {
            @Override
            public void onSuccess(List<Service> data) {
                isLoading.setValue(false);
                technicianServices.setValue(data);
            }
            @Override
            public void onError(String error) {
                isLoading.setValue(false);
                errorMessage.setValue(error);
            }
        });
    }

    public void loadAllAppointments() {
        String token = getToken();
        if (token == null) return;
        isLoading.setValue(true);
        repository.getAllAppointments(token, new AdminRepository.AdminCallback<List<Appointment>>() {
            @Override
            public void onSuccess(List<Appointment> data) {
                isLoading.setValue(false);
                allAppointments.setValue(data);
            }
            @Override
            public void onError(String error) {
                isLoading.setValue(false);
                errorMessage.setValue(error);
            }
        });
    }

    public void loadAllServices() {
        String token = getToken();
        if (token == null) return;
        isLoading.setValue(true);
        repository.getAllServices(token, new AdminRepository.AdminCallback<List<Service>>() {
            @Override
            public void onSuccess(List<Service> data) {
                isLoading.setValue(false);
                allServices.setValue(data);
            }
            @Override
            public void onError(String error) {
                isLoading.setValue(false);
                errorMessage.setValue(error);
            }
        });
    }

    public void assignTechnician(String appointmentId, String technicianId) {
        String token = getToken();
        if (token == null) return;
        isLoading.setValue(true);
        repository.assignTechnician(token, appointmentId, technicianId, new AdminRepository.AdminCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                isLoading.setValue(false);
                assignmentSuccess.setValue(true);
            }
            @Override
            public void onError(String error) {
                isLoading.setValue(false);
                errorMessage.setValue(error);
                assignmentSuccess.setValue(false);
            }
        });
    }

    public void updateTechnicianServices(String techId, List<String> serviceIds) {
        String token = getToken();
        if (token == null) return;
        isLoading.setValue(true);
        repository.updateTechnicianServices(token, techId, serviceIds, new AdminRepository.AdminCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                isLoading.setValue(false);
                skillUpdateSuccess.setValue(true);
            }
            @Override
            public void onError(String error) {
                isLoading.setValue(false);
                errorMessage.setValue(error);
                skillUpdateSuccess.setValue(false);
            }
        });
    }

    // --- Branch CRUD Methods ---
    public void createBranch(Branch branch) {
        String token = getToken();
        if (token == null) return;
        isLoading.setValue(true);
        repository.createBranch(token, branch, new AdminRepository.AdminCallback<Branch>() {
            @Override
            public void onSuccess(Branch result) {
                isLoading.setValue(false);
                crudSuccess.setValue(true);
                loadBranches();
            }
            @Override
            public void onError(String error) {
                isLoading.setValue(false);
                errorMessage.setValue(error);
                crudSuccess.setValue(false);
            }
        });
    }

    public void updateBranch(String branchId, Branch branch) {
        String token = getToken();
        if (token == null) return;
        isLoading.setValue(true);
        repository.updateBranch(token, branchId, branch, new AdminRepository.AdminCallback<Branch>() {
            @Override
            public void onSuccess(Branch result) {
                isLoading.setValue(false);
                crudSuccess.setValue(true);
                loadBranches();
            }
            @Override
            public void onError(String error) {
                isLoading.setValue(false);
                errorMessage.setValue(error);
                crudSuccess.setValue(false);
            }
        });
    }

    public void deleteBranch(String branchId) {
        String token = getToken();
        if (token == null) return;
        isLoading.setValue(true);
        repository.deleteBranch(token, branchId, new AdminRepository.AdminCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                isLoading.setValue(false);
                crudSuccess.setValue(true);
                loadBranches();
            }
            @Override
            public void onError(String error) {
                isLoading.setValue(false);
                errorMessage.setValue(error);
                crudSuccess.setValue(false);
            }
        });
    }

    // --- Technician CRUD Methods ---
    public void createTechnician(Technician technician) {
        String token = getToken();
        if (token == null) return;
        isLoading.setValue(true);
        repository.createTechnician(token, technician, new AdminRepository.AdminCallback<Technician>() {
            @Override
            public void onSuccess(Technician result) {
                isLoading.setValue(false);
                crudSuccess.setValue(true);
                loadTechnicians();
            }
            @Override
            public void onError(String error) {
                isLoading.setValue(false);
                errorMessage.setValue(error);
                crudSuccess.setValue(false);
            }
        });
    }

    public void updateTechnician(String technicianId, Technician technician) {
        String token = getToken();
        if (token == null) return;
        isLoading.setValue(true);
        repository.updateTechnician(token, technicianId, technician, new AdminRepository.AdminCallback<Technician>() {
            @Override
            public void onSuccess(Technician result) {
                isLoading.setValue(false);
                crudSuccess.setValue(true);
                loadTechnicians();
            }
            @Override
            public void onError(String error) {
                isLoading.setValue(false);
                errorMessage.setValue(error);
                crudSuccess.setValue(false);
            }
        });
    }

    public void deleteTechnician(String technicianId) {
        String token = getToken();
        if (token == null) return;
        isLoading.setValue(true);
        repository.deleteTechnician(token, technicianId, new AdminRepository.AdminCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                isLoading.setValue(false);
                crudSuccess.setValue(true);
                loadTechnicians();
            }
            @Override
            public void onError(String error) {
                isLoading.setValue(false);
                errorMessage.setValue(error);
                crudSuccess.setValue(false);
            }
        });
    }
}


