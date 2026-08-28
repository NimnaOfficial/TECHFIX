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
        apiService.getDashboard(token).enqueue(new Callback<ApiResponse<DashboardResponse.DashboardData>>() {
            @Override
            public void onResponse(Call<ApiResponse<DashboardResponse.DashboardData>> call, Response<ApiResponse<DashboardResponse.DashboardData>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onSuccess(response.body().getData());
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

    // --- All Appointments ---
    public void getAllAppointments(String token, AdminCallback<List<Appointment>> callback) {
        apiService.getAllAppointments(token).enqueue(new Callback<ApiResponse<List<Appointment>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Appointment>>> call, Response<ApiResponse<List<Appointment>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    callback.onSuccess(response.body().getData());
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
