package com.mad.techfix.network;

import com.mad.techfix.models.ApiResponse;
import com.mad.techfix.models.AuthResponse;
import com.mad.techfix.models.LoginRequest;
import com.mad.techfix.models.RegisterRequest;
import com.mad.techfix.models.SparePart;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiService {

    // POST https://techfix-api.codse251f-003.workers.dev/api/auth/login
    @POST("api/auth/login")
    Call<AuthResponse> loginUser(@Body LoginRequest loginRequest);

    // ==========================================
    // 2. PARTS MANAGER (YOUR MODULE)
    // ==========================================
    @GET("api/spare-parts")
    Call<ApiResponse<List<SparePart>>> getSpareParts(@Header("Authorization") String auth);

    @POST("api/spare-parts")
    Call<ApiResponse<SparePart>> createSparePart(@Header("Authorization") String auth, @Body SparePart part);

    @PUT("api/spare-parts/{id}")
    Call<ApiResponse<SparePart>> updateSparePart(@Header("Authorization") String auth, @Path("id") String partId, @Body SparePart part);

    @retrofit2.http.DELETE("api/spare-parts/{id}")
    Call<ApiResponse<Object>> deleteSparePart(@Header("Authorization") String auth, @Path("id") String partId);

    // ==========================================
    // 3. REPAIR HISTORY (YOUR MODULE)
    // ==========================================
    @GET("api/appointments")
    Call<ApiResponse<List<Appointment>>> getAppointments(@Header("Authorization") String auth);

    @GET("api/appointments/{id}/history")
    Call<ApiResponse<List<Object>>> getAppointmentHistory(
            @Header("Authorization") String auth,
            @Path("id") String appointmentId
    );

    // ==========================================
    // 4. PAYMENTS (YOUR MODULE)
    // ==========================================
    @POST("api/payments")
    Call<ApiResponse<Payment>> createPayment(
            @Header("Authorization") String auth,
            @Body Payment payment
    );
    // POST https://techfix-api.codse251f-003.workers.dev/api/auth/register
    @POST("api/auth/register")
    Call<AuthResponse> registerUser(@Body RegisterRequest registerRequest);

    // GET https://techfix-api.codse251f-003.workers.dev/api/spare-parts
    @GET("api/spare-parts")
    Call<ApiResponse<List<SparePart>>> getSpareParts(@Header("Authorization") String token);
}