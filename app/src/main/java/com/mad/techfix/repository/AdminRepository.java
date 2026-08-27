package com.mad.techfix.repository;

import android.os.Handler;
import android.os.Looper;

import com.mad.techfix.data.local.database.AdminDao;
import com.mad.techfix.data.local.database.AppDatabase;
import com.mad.techfix.data.local.database.BranchEntity;
import com.mad.techfix.data.local.database.TechnicianEntity;
import com.mad.techfix.models.Appointment;
import com.mad.techfix.models.Branch;
import com.mad.techfix.models.DashboardResponse;
import com.mad.techfix.models.Service;
import com.mad.techfix.models.Technician;
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

    public void fetchDashboard(String token, AdminCallback<DashboardResponse.DashboardData> callback) {
        apiService.getDashboard("Bearer " + token).enqueue(new Callback<DashboardResponse.DashboardData>() {
            @Override
            public void onResponse(Call<DashboardResponse.DashboardData> call, Response<DashboardResponse.DashboardData> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to fetch dashboard data");
                }
            }

            @Override
            public void onFailure(Call<DashboardResponse.DashboardData> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void fetchBranches(String token, AdminCallback<List<Branch>> callback) {
        apiService.getBranches("Bearer " + token).enqueue(new Callback<List<Branch>>() {
            @Override
            public void onResponse(Call<List<Branch>> call, Response<List<Branch>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Branch> branches = response.body();
                    
                    executorService.execute(() -> {
                        List<BranchEntity> entities = new ArrayList<>();
                        for (Branch b : branches) {
                            // Assuming Branch has required getters. Fallbacks used to match entity constructor
                            entities.add(new BranchEntity(
                                b.getId(),
                                b.getName(),
                                b.getAddress(),
                                b.getCity(),
                                b.getLatitude(),
                                b.getLongitude(),
                                b.getPhone(),
                                b.getEmail(),
                                b.getOpeningTime(),
                                b.getClosingTime()
                            ));
                        }
                        adminDao.insertBranches(entities);
                    });
                    
                    callback.onSuccess(branches);
                } else {
                    callback.onError("Failed to fetch branches");
                }
            }

            @Override
            public void onFailure(Call<List<Branch>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void fetchBranchDetails(String token, String branchId, AdminCallback<Branch> callback) {
        apiService.getBranchDetails("Bearer " + token, branchId).enqueue(new Callback<Branch>() {
            @Override
            public void onResponse(Call<Branch> call, Response<Branch> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to fetch branch details");
                }
            }

            @Override
            public void onFailure(Call<Branch> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void fetchTechnicians(String token, AdminCallback<List<Technician>> callback) {
        apiService.getTechnicians("Bearer " + token).enqueue(new Callback<List<Technician>>() {
            @Override
            public void onResponse(Call<List<Technician>> call, Response<List<Technician>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Technician> technicians = response.body();
                    
                    executorService.execute(() -> {
                        List<TechnicianEntity> entities = new ArrayList<>();
                        for (Technician t : technicians) {
                            entities.add(new TechnicianEntity(
                                t.getId(),
                                t.getEmployeeCode(),
                                t.getFirstName(),
                                t.getLastName(),
                                t.getSpecialization(),
                                t.getStatus(),
                                t.getBranchId(),
                                t.getBranchName()
                            ));
                        }
                        adminDao.insertTechnicians(entities);
                    });
                    
                    callback.onSuccess(technicians);
                } else {
                    callback.onError("Failed to fetch technicians");
                }
            }

            @Override
            public void onFailure(Call<List<Technician>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void fetchTechnicianServices(String token, String techId, AdminCallback<List<Service>> callback) {
        apiService.getTechnicianServices("Bearer " + token, techId).enqueue(new Callback<List<Service>>() {
            @Override
            public void onResponse(Call<List<Service>> call, Response<List<Service>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to fetch technician services");
                }
            }

            @Override
            public void onFailure(Call<List<Service>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void updateTechnicianServices(String token, String techId, List<String> serviceIds, AdminCallback<Void> callback) {
        apiService.updateTechnicianServices("Bearer " + token, techId, serviceIds).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError("Failed to update technician services");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void assignTechnician(String token, String appointmentId, String technicianId, AdminCallback<Void> callback) {
        apiService.assignTechnician("Bearer " + token, appointmentId, technicianId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError("Failed to assign technician");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void fetchAllAppointments(String token, AdminCallback<List<Appointment>> callback) {
        apiService.getAllAppointments("Bearer " + token).enqueue(new Callback<List<Appointment>>() {
            @Override
            public void onResponse(Call<List<Appointment>> call, Response<List<Appointment>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to fetch all appointments");
                }
            }

            @Override
            public void onFailure(Call<List<Appointment>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void fetchAllServices(String token, AdminCallback<List<Service>> callback) {
        apiService.getAllServices("Bearer " + token).enqueue(new Callback<List<Service>>() {
            @Override
            public void onResponse(Call<List<Service>> call, Response<List<Service>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to fetch all services");
                }
            }

            @Override
            public void onFailure(Call<List<Service>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

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
