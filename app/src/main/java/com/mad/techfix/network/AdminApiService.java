package com.mad.techfix.network;

import com.mad.techfix.models.ApiResponse;
import com.mad.techfix.models.Appointment;
import com.mad.techfix.models.admin.AssignTechnicianRequest;
import com.mad.techfix.models.admin.Branch;
import com.mad.techfix.models.admin.DashboardResponse;
import com.mad.techfix.models.admin.EligibleTechniciansResponse;
import com.mad.techfix.models.admin.Service;
import com.mad.techfix.models.admin.Technician;
import com.mad.techfix.models.admin.UpdateTechServicesRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.POST;
import retrofit2.http.DELETE;
import retrofit2.http.Path;
import com.mad.techfix.models.admin.SysAdminOverviewResponse;

public interface AdminApiService {
    @GET("api/admin/system/logs")
    Call<ApiResponse<java.util.List<com.mad.techfix.models.admin.LogEntry>>> getSystemLogs(@Header("Authorization") String auth);

    @DELETE("api/admin/system/logs")
    Call<ApiResponse<Void>> clearSystemLogs(@Header("Authorization") String auth);
    @GET("api/admin/users/{id}/monitor")
    Call<ApiResponse<com.mad.techfix.models.admin.UserMonitorResponse>> getUserMonitor(@Header("Authorization") String auth, @Path("id") String userId);
    @GET("api/admin/settings")
    Call<ApiResponse<java.util.Map<String, String>>> getSystemSettings(@Header("Authorization") String auth);

    @POST("api/admin/settings")
    Call<ApiResponse<Void>> updateSystemSetting(@Header("Authorization") String auth, @Body java.util.Map<String, String> payload);
    @GET("api/admin/system/backup")
    Call<okhttp3.ResponseBody> getSystemBackup(@Header("Authorization") String auth);
    @GET("api/admin/users")
    Call<ApiResponse<java.util.List<com.mad.techfix.models.admin.Manager>>> getManagers(@Header("Authorization") String auth);

    @POST("api/admin/users")
    Call<ApiResponse<Void>> createManager(@Header("Authorization") String auth, @Body com.mad.techfix.models.admin.Manager manager);

    @PUT("api/admin/managers/{id}")
    Call<ApiResponse<Void>> updateManager(@Header("Authorization") String auth, @Path("id") String id, @Body com.mad.techfix.models.admin.Manager manager);

    @DELETE("api/admin/managers/{id}")
    Call<ApiResponse<Void>> deleteManager(@Header("Authorization") String auth, @Path("id") String id);
    @GET("api/admin/system/overview")
    Call<ApiResponse<SysAdminOverviewResponse>> getSystemOverview(@Header("Authorization") String auth);
    @GET("api/admin/dashboard")
    Call<ApiResponse<DashboardResponse.DashboardData>> getDashboard(@Header("Authorization") String auth);

    @GET("api/branches")
    Call<ApiResponse<List<Branch>>> getBranches(@Header("Authorization") String auth);

    @GET("api/branches/{id}")
    Call<ApiResponse<Branch>> getBranchDetails(@Header("Authorization") String auth, @Path("id") String branchId);

    @GET("api/technicians")
    Call<ApiResponse<List<Technician>>> getTechnicians(@Header("Authorization") String auth);

    @GET("api/technicians/{id}/services")
    Call<ApiResponse<List<Service>>> getTechnicianServices(@Header("Authorization") String auth, @Path("id") String technicianId);

    @PUT("api/technicians/{id}/services")
    Call<ApiResponse<Object>> updateTechnicianServices(@Header("Authorization") String auth, @Path("id") String technicianId, @Body UpdateTechServicesRequest request);

    @PUT("api/appointments/{id}/assign")
    Call<ApiResponse<Object>> assignTechnician(@Header("Authorization") String auth, @Path("id") String appointmentId, @Body AssignTechnicianRequest request);

    @GET("api/appointments/{id}/eligible-technicians")
    Call<ApiResponse<EligibleTechniciansResponse>> getEligibleTechnicians(@Header("Authorization") String auth, @Path("id") String appointmentId);

    @GET("api/appointments")
    Call<ApiResponse<List<Appointment>>> getAllAppointments(@Header("Authorization") String auth);

    @GET("api/services")
    Call<ApiResponse<List<Service>>> getAllServices(@Header("Authorization") String auth);

    // --- Branch CRUD ---
    @retrofit2.http.POST("api/branches")
    Call<ApiResponse<Branch>> createBranch(@Header("Authorization") String auth, @Body Branch branch);

    @PUT("api/branches/{id}")
    Call<ApiResponse<Branch>> updateBranch(@Header("Authorization") String auth, @Path("id") String branchId, @Body Branch branch);

    @retrofit2.http.DELETE("api/branches/{id}")
    Call<ApiResponse<Object>> deleteBranch(@Header("Authorization") String auth, @Path("id") String branchId);

    // --- Technician CRUD ---
    @retrofit2.http.POST("api/technicians")
    Call<ApiResponse<Technician>> createTechnician(@Header("Authorization") String auth, @Body Technician technician);

    @PUT("api/technicians/{id}")
    Call<ApiResponse<Technician>> updateTechnician(@Header("Authorization") String auth, @Path("id") String technicianId, @Body Technician technician);

    @retrofit2.http.DELETE("api/technicians/{id}")
    Call<ApiResponse<Object>> deleteTechnician(@Header("Authorization") String auth, @Path("id") String technicianId);
}








