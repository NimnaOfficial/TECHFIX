package com.mad.techfix.repository;

import android.os.Handler;
import android.os.Looper;

import com.mad.techfix.data.local.database.AdminDao;
import com.mad.techfix.data.local.database.AppDatabase;
import com.mad.techfix.data.local.database.BranchEntity;
import com.mad.techfix.data.local.database.TechnicianEntity;
import com.mad.techfix.models.ApiResponse;
import com.mad.techfix.models.Appointment;
import com.mad.techfix.models.admin.AssignTechnicianRequest;
import com.mad.techfix.models.admin.Branch;
import com.mad.techfix.models.admin.DashboardResponse;
import com.mad.techfix.models.admin.Service;
import com.mad.techfix.models.admin.Technician;
import com.mad.techfix.models.admin.UpdateTechServicesRequest;
import com.mad.techfix.network.AdminApiService;
import com.mad.techfix.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminRepository {

    private final AdminApiService apiService;
    private final AdminDao adminDao;
    private final ExecutorService executorService;
    private final Handler mainHandler;

    public interface AdminCallback<T> {
        void onSuccess(T data);
        void onError(String message);
    }

    public AdminRepository(AppDatabase database) {
        apiService = RetrofitClient.getClient().create(AdminApiService.class);
        adminDao = database.adminDao();
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
    }

    // --- Dashboard ---
        public void getDashboardData(String token, AdminCallback<DashboardResponse.DashboardData> callback) {
        // Try caching first
        executorService.execute(() -> {
            com.mad.techfix.data.local.database.DashboardMetricsEntity cached = adminDao.getDashboardMetrics();
            if (cached != null) {
                DashboardResponse.DashboardData data = new DashboardResponse.DashboardData();
                data.setTotalRevenue(cached.totalRevenue);
                data.setPendingRequests(cached.pendingRequests);
                data.setActiveRepairs(cached.activeRepairs);
                data.setAvailableTechnicians(cached.availableTechnicians);
                data.setTotalAppointments(cached.totalAppointments);
                data.setTotalTechnicians(cached.totalTechnicians);
                // We use a handler or postValue in ViewModel usually, but callback here runs on background if not careful.
                // Assuming ViewModel handles thread dispatching or we just run it. 
                // For safety, Android repository callbacks should ideally post to main thread, but Retrofit already does.
            }
        });

        apiService.getDashboard(token).enqueue(new Callback<ApiResponse<DashboardResponse.DashboardData>>() {
            @Override
            public void onResponse(Call<ApiResponse<DashboardResponse.DashboardData>> call, Response<ApiResponse<DashboardResponse.DashboardData>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    DashboardResponse.DashboardData data = response.body().getData();
                    executorService.execute(() -> {
                        adminDao.insertDashboardMetrics(new com.mad.techfix.data.local.database.DashboardMetricsEntity(
                                data.getTotalRevenue(), data.getPendingRequests(), data.getActiveRepairs(),
                                data.getAvailableTechnicians(), data.getTotalAppointments(), data.getTotalTechnicians()
                        ));
                    });
                    callback.onSuccess(data);
                } else {
                    callback.onError("Failed to fetch dashboard data");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<DashboardResponse.DashboardData>> call, Throwable t) {
                callback.onError(t.getMessage() != null ? t.getMessage() : "Network error");
            }
        });
    }

    // --- Branches ---
    public void getBranches(String token, AdminCallback<List<Branch>> callback) {
        apiService.getBranches(token).enqueue(new Callback<ApiResponse<List<Branch>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Branch>>> call, Response<ApiResponse<List<Branch>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Branch> branches = response.body().getData();
                    // Cache to Room
                    executorService.execute(() -> {
                        List<BranchEntity> entities = new ArrayList<>();
                        for (Branch b : branches) {
                            entities.add(new BranchEntity(
                                b.getId(), b.getName(), b.getAddress(), b.getCity(),
                                b.getLatitude(), b.getLongitude(), b.getPhone(),
                                b.getEmail(), b.getOpeningTime(), b.getClosingTime()
                            ));
                        }
                        adminDao.deleteAllBranches();
                        adminDao.insertBranches(entities);
                    });
                    callback.onSuccess(branches);
                } else {
                    callback.onError("Failed to fetch branches");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Branch>>> call, Throwable t) {
                callback.onError(t.getMessage() != null ? t.getMessage() : "Network error");
            }
        });
    }

    // --- Branch Details ---
    public void getBranchDetails(String token, String branchId, AdminCallback<Branch> callback) {
        apiService.getBranchDetails(token, branchId).enqueue(new Callback<ApiResponse<Branch>>() {
            @Override
            public void onResponse(Call<ApiResponse<Branch>> call, Response<ApiResponse<Branch>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onError("Failed to fetch branch details");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Branch>> call, Throwable t) {
                callback.onError(t.getMessage() != null ? t.getMessage() : "Network error");
            }
        });
    }

    // --- Technicians ---
    public void getTechnicians(String token, AdminCallback<List<Technician>> callback) {
        apiService.getTechnicians(token).enqueue(new Callback<ApiResponse<List<Technician>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Technician>>> call, Response<ApiResponse<List<Technician>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Technician> technicians = response.body().getData();
                    // Cache to Room
                    executorService.execute(() -> {
                        List<TechnicianEntity> entities = new ArrayList<>();
                        for (Technician t : technicians) {
                            entities.add(new TechnicianEntity(
                                t.getId(), t.getEmployeeCode(),
                                t.getFirstName(), t.getLastName(),
                                t.getSpecialization(),
                                t.getAvailabilityStatus(),
                                t.getBranchId(), ""
                            ));
                        }
                        adminDao.deleteAllTechnicians();
                        adminDao.insertTechnicians(entities);
                    });
                    callback.onSuccess(technicians);
                } else {
                    callback.onError("Failed to fetch technicians");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Technician>>> call, Throwable t) {
                callback.onError(t.getMessage() != null ? t.getMessage() : "Network error");
            }
        });
    }

    // --- Technician Services ---
    public void getTechnicianServices(String token, String techId, AdminCallback<List<Service>> callback) {
        apiService.getTechnicianServices(token, techId).enqueue(new Callback<ApiResponse<List<Service>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Service>>> call, Response<ApiResponse<List<Service>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onError("Failed to fetch technician services");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Service>>> call, Throwable t) {
                callback.onError(t.getMessage() != null ? t.getMessage() : "Network error");
            }
        });
    }

    // --- Update Technician Services ---
    public void updateTechnicianServices(String token, String techId, List<String> serviceIds, AdminCallback<Void> callback) {
        UpdateTechServicesRequest request = new UpdateTechServicesRequest(serviceIds);
        apiService.updateTechnicianServices(token, techId, request).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError("Failed to update technician services");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                callback.onError(t.getMessage() != null ? t.getMessage() : "Network error");
            }
        });
    }

    // --- Assign Technician ---
    public void assignTechnician(String token, String appointmentId, String technicianId, AdminCallback<Void> callback) {
        AssignTechnicianRequest request = new AssignTechnicianRequest(technicianId);
        apiService.assignTechnician(token, appointmentId, request).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError("Failed to assign technician");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                callback.onError(t.getMessage() != null ? t.getMessage() : "Network error");
            }
        });
    }

    // --- Smart Assignment: Eligible Technicians ---
    public void getEligibleTechnicians(String token, String appointmentId, AdminCallback<com.mad.techfix.models.admin.EligibleTechniciansResponse> callback) {
        apiService.getEligibleTechnicians(token, appointmentId).enqueue(new Callback<ApiResponse<com.mad.techfix.models.admin.EligibleTechniciansResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<com.mad.techfix.models.admin.EligibleTechniciansResponse>> call, Response<ApiResponse<com.mad.techfix.models.admin.EligibleTechniciansResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onError("Failed to fetch eligible technicians");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<com.mad.techfix.models.admin.EligibleTechniciansResponse>> call, Throwable t) {
                callback.onError(t.getMessage() != null ? t.getMessage() : "Network error");
            }
        });
    }

    // --- All Appointments ---
        public void getAllAppointments(String token, AdminCallback<List<Appointment>> callback) {
        apiService.getAllAppointments(token).enqueue(new Callback<ApiResponse<List<Appointment>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Appointment>>> call, Response<ApiResponse<List<Appointment>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Appointment> appointments = response.body().getData();
                    executorService.execute(() -> {
                        List<com.mad.techfix.data.local.database.AppointmentEntity> entities = new java.util.ArrayList<>();
                        for (Appointment a : appointments) {
                            entities.add(new com.mad.techfix.data.local.database.AppointmentEntity(
                                    a.getId(), a.getAppointment_number(), a.getCustomer_id(), a.getDevice_id(),
                                    a.getService_id(), a.getBranch_id(), a.getTechnician_id(), a.getStatus(),
                                    a.getRequested_date(), a.getRequested_time(), a.getEstimated_price(),
                                    a.getFinal_price() != null ? a.getFinal_price() : 0.0, a.getCreated_at()
                            ));
                        }
                        adminDao.deleteAllAppointments();
                        adminDao.insertAppointments(entities);
                    });
                    callback.onSuccess(appointments);
                } else {
                    callback.onError("Failed to fetch appointments");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Appointment>>> call, Throwable t) {
                callback.onError(t.getMessage() != null ? t.getMessage() : "Network error");
            }
        });
    }

    // --- All Services ---
    public void getAllServices(String token, AdminCallback<List<Service>> callback) {
        apiService.getAllServices(token).enqueue(new Callback<ApiResponse<List<Service>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Service>>> call, Response<ApiResponse<List<Service>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onError("Failed to fetch services");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Service>>> call, Throwable t) {
                callback.onError(t.getMessage() != null ? t.getMessage() : "Network error");
            }
        });
    }

    // --- Branch CRUD ---
    public void createBranch(String token, Branch branch, AdminCallback<Branch> callback) {
        apiService.createBranch(token, branch).enqueue(new Callback<ApiResponse<Branch>>() {
            @Override
            public void onResponse(Call<ApiResponse<Branch>> call, Response<ApiResponse<Branch>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) callback.onSuccess(response.body().getData());
                else callback.onError("Failed to create branch");
            }
            @Override
            public void onFailure(Call<ApiResponse<Branch>> call, Throwable t) { callback.onError(t.getMessage()); }
        });
    }

    public void updateBranch(String token, String branchId, Branch branch, AdminCallback<Branch> callback) {
        apiService.updateBranch(token, branchId, branch).enqueue(new Callback<ApiResponse<Branch>>() {
            @Override
            public void onResponse(Call<ApiResponse<Branch>> call, Response<ApiResponse<Branch>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) callback.onSuccess(response.body().getData());
                else callback.onError("Failed to update branch");
            }
            @Override
            public void onFailure(Call<ApiResponse<Branch>> call, Throwable t) { callback.onError(t.getMessage()); }
        });
    }

    public void deleteBranch(String token, String branchId, AdminCallback<Void> callback) {
        apiService.deleteBranch(token, branchId).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) callback.onSuccess(null);
                else callback.onError("Failed to delete branch");
            }
            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) { callback.onError(t.getMessage()); }
        });
    }

    // --- Technician CRUD ---
    public void createTechnician(String token, Technician technician, AdminCallback<Technician> callback) {
        apiService.createTechnician(token, technician).enqueue(new Callback<ApiResponse<Technician>>() {
            @Override
            public void onResponse(Call<ApiResponse<Technician>> call, Response<ApiResponse<Technician>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) callback.onSuccess(response.body().getData());
                else callback.onError("Failed to create technician");
            }
            @Override
            public void onFailure(Call<ApiResponse<Technician>> call, Throwable t) { callback.onError(t.getMessage()); }
        });
    }

    public void updateTechnician(String token, String technicianId, Technician technician, AdminCallback<Technician> callback) {
        apiService.updateTechnician(token, technicianId, technician).enqueue(new Callback<ApiResponse<Technician>>() {
            @Override
            public void onResponse(Call<ApiResponse<Technician>> call, Response<ApiResponse<Technician>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) callback.onSuccess(response.body().getData());
                else callback.onError("Failed to update technician");
            }
            @Override
            public void onFailure(Call<ApiResponse<Technician>> call, Throwable t) { callback.onError(t.getMessage()); }
        });
    }

    public void deleteTechnician(String token, String technicianId, AdminCallback<Void> callback) {
        apiService.deleteTechnician(token, technicianId).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    executorService.execute(() -> {
                        adminDao.deleteTechnicianById(technicianId);
                    });
                    callback.onSuccess(null);
                } else callback.onError("Failed to delete technician");
            }
            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) { callback.onError(t.getMessage()); }
        });
    }

        // --- System Admin ---
    public void getSystemLogs(String token, AdminCallback<java.util.List<com.mad.techfix.models.admin.LogEntry>> callback) {
        apiService.getSystemLogs(token).enqueue(new retrofit2.Callback<com.mad.techfix.models.ApiResponse<java.util.List<com.mad.techfix.models.admin.LogEntry>>>() {
            @Override
            public void onResponse(retrofit2.Call<com.mad.techfix.models.ApiResponse<java.util.List<com.mad.techfix.models.admin.LogEntry>>> call, retrofit2.Response<com.mad.techfix.models.ApiResponse<java.util.List<com.mad.techfix.models.admin.LogEntry>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) callback.onSuccess(response.body().getData());
                else callback.onError("Failed to fetch logs");
            }
            @Override public void onFailure(retrofit2.Call<com.mad.techfix.models.ApiResponse<java.util.List<com.mad.techfix.models.admin.LogEntry>>> call, Throwable t) { callback.onError(t.getMessage()); }
        });
    }

            public void getSystemSettings(String token, AdminCallback<java.util.Map<String, String>> callback) {
        apiService.getSystemSettings(token).enqueue(new retrofit2.Callback<com.mad.techfix.models.ApiResponse<java.util.Map<String, String>>>() {
            @Override
            public void onResponse(retrofit2.Call<com.mad.techfix.models.ApiResponse<java.util.Map<String, String>>> call, retrofit2.Response<com.mad.techfix.models.ApiResponse<java.util.Map<String, String>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) callback.onSuccess(response.body().getData());
                else callback.onError("Failed to fetch settings");
            }
            @Override public void onFailure(retrofit2.Call<com.mad.techfix.models.ApiResponse<java.util.Map<String, String>>> call, Throwable t) { callback.onError(t.getMessage()); }
        });
    }

        public void getSystemBackup(String token, AdminCallback<okhttp3.ResponseBody> callback) {
        apiService.getSystemBackup(token).enqueue(new retrofit2.Callback<okhttp3.ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<okhttp3.ResponseBody> call, retrofit2.Response<okhttp3.ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) callback.onSuccess(response.body());
                else { try { callback.onError("Failed: " + response.errorBody().string()); } catch (Exception e) { callback.onError("Failed to fetch backup: " + response.code()); } }
            }
            @Override public void onFailure(retrofit2.Call<okhttp3.ResponseBody> call, Throwable t) { callback.onError(t.getMessage()); }
        });
    }

    public void updateSystemSetting(String token, String key, String value, AdminCallback<Void> callback) {
        java.util.Map<String, String> payload = new java.util.HashMap<>();
        payload.put("setting_key", key);
        payload.put("setting_value", value);
        apiService.updateSystemSetting(token, payload).enqueue(new retrofit2.Callback<com.mad.techfix.models.ApiResponse<Void>>() {
            @Override
            public void onResponse(retrofit2.Call<com.mad.techfix.models.ApiResponse<Void>> call, retrofit2.Response<com.mad.techfix.models.ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) callback.onSuccess(null);
                else callback.onError("Failed to save setting");
            }
            @Override public void onFailure(retrofit2.Call<com.mad.techfix.models.ApiResponse<Void>> call, Throwable t) { callback.onError(t.getMessage()); }
        });
    }

    public void getUserMonitor(String token, String userId, AdminCallback<com.mad.techfix.models.admin.UserMonitorResponse> callback) {
        apiService.getUserMonitor(token, userId).enqueue(new retrofit2.Callback<com.mad.techfix.models.ApiResponse<com.mad.techfix.models.admin.UserMonitorResponse>>() {
            @Override
            public void onResponse(retrofit2.Call<com.mad.techfix.models.ApiResponse<com.mad.techfix.models.admin.UserMonitorResponse>> call, retrofit2.Response<com.mad.techfix.models.ApiResponse<com.mad.techfix.models.admin.UserMonitorResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) callback.onSuccess(response.body().getData());
                else callback.onError("Failed to fetch monitor data");
            }
            @Override public void onFailure(retrofit2.Call<com.mad.techfix.models.ApiResponse<com.mad.techfix.models.admin.UserMonitorResponse>> call, Throwable t) { callback.onError(t.getMessage()); }
        });
    }

    public void clearSystemLogs(String token, AdminCallback<Void> callback) {
        apiService.clearSystemLogs(token).enqueue(new retrofit2.Callback<com.mad.techfix.models.ApiResponse<Void>>() {
            @Override
            public void onResponse(retrofit2.Call<com.mad.techfix.models.ApiResponse<Void>> call, retrofit2.Response<com.mad.techfix.models.ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) callback.onSuccess(null);
                else callback.onError("Failed to clear logs");
            }
            @Override public void onFailure(retrofit2.Call<com.mad.techfix.models.ApiResponse<Void>> call, Throwable t) { callback.onError(t.getMessage()); }
        });
    }
    public void getManagers(String token, AdminCallback<java.util.List<com.mad.techfix.models.admin.Manager>> callback) {
        apiService.getManagers(token).enqueue(new retrofit2.Callback<com.mad.techfix.models.ApiResponse<java.util.List<com.mad.techfix.models.admin.Manager>>>() {
            @Override
            public void onResponse(retrofit2.Call<com.mad.techfix.models.ApiResponse<java.util.List<com.mad.techfix.models.admin.Manager>>> call, retrofit2.Response<com.mad.techfix.models.ApiResponse<java.util.List<com.mad.techfix.models.admin.Manager>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) callback.onSuccess(response.body().getData());
                else callback.onError("Failed to fetch managers");
            }
            @Override public void onFailure(retrofit2.Call<com.mad.techfix.models.ApiResponse<java.util.List<com.mad.techfix.models.admin.Manager>>> call, Throwable t) { callback.onError(t.getMessage()); }
        });
    }

    public void createManager(String token, com.mad.techfix.models.admin.Manager manager, AdminCallback<Void> callback) {
        apiService.createManager(token, manager).enqueue(new retrofit2.Callback<com.mad.techfix.models.ApiResponse<Void>>() {
            @Override
            public void onResponse(retrofit2.Call<com.mad.techfix.models.ApiResponse<Void>> call, retrofit2.Response<com.mad.techfix.models.ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) callback.onSuccess(null);
                else callback.onError("Failed to create manager");
            }
            @Override public void onFailure(retrofit2.Call<com.mad.techfix.models.ApiResponse<Void>> call, Throwable t) { callback.onError(t.getMessage()); }
        });
    }

    public void updateManager(String token, String id, com.mad.techfix.models.admin.Manager manager, AdminCallback<Void> callback) {
        apiService.updateManager(token, id, manager).enqueue(new retrofit2.Callback<com.mad.techfix.models.ApiResponse<Void>>() {
            @Override
            public void onResponse(retrofit2.Call<com.mad.techfix.models.ApiResponse<Void>> call, retrofit2.Response<com.mad.techfix.models.ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) callback.onSuccess(null);
                else callback.onError("Failed to update manager");
            }
            @Override public void onFailure(retrofit2.Call<com.mad.techfix.models.ApiResponse<Void>> call, Throwable t) { callback.onError(t.getMessage()); }
        });
    }

    public void deleteManager(String token, String id, AdminCallback<Void> callback) {
        apiService.deleteManager(token, id).enqueue(new retrofit2.Callback<com.mad.techfix.models.ApiResponse<Void>>() {
            @Override
            public void onResponse(retrofit2.Call<com.mad.techfix.models.ApiResponse<Void>> call, retrofit2.Response<com.mad.techfix.models.ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) callback.onSuccess(null);
                else callback.onError("Failed to delete manager");
            }
            @Override public void onFailure(retrofit2.Call<com.mad.techfix.models.ApiResponse<Void>> call, Throwable t) { callback.onError(t.getMessage()); }
        });
    }
    public void getSystemOverview(String token, AdminCallback<com.mad.techfix.models.admin.SysAdminOverviewResponse> callback) {
        apiService.getSystemOverview(token).enqueue(new retrofit2.Callback<com.mad.techfix.models.ApiResponse<com.mad.techfix.models.admin.SysAdminOverviewResponse>>() {
            @Override
            public void onResponse(retrofit2.Call<com.mad.techfix.models.ApiResponse<com.mad.techfix.models.admin.SysAdminOverviewResponse>> call, retrofit2.Response<com.mad.techfix.models.ApiResponse<com.mad.techfix.models.admin.SysAdminOverviewResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onSuccess(response.body().getData());
                } else {
                    callback.onError("Failed to fetch system overview. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.mad.techfix.models.ApiResponse<com.mad.techfix.models.admin.SysAdminOverviewResponse>> call, Throwable t) {
                callback.onError(t.getMessage() != null ? t.getMessage() : "Network error");
            }
        });
    }

    // --- Cached Data (Room) ---
    public List<TechnicianEntity> getCachedTechnicians() {
        return adminDao.getAllTechnicians();
    }

    public List<BranchEntity> getCachedBranches() {
        return adminDao.getAllBranches();
    }

    public List<TechnicianEntity> getCachedAvailableTechniciansByBranch(String branchId) {
        return adminDao.getAvailableTechniciansByBranch(branchId);
    }
}










