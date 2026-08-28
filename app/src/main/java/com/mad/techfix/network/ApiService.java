package com.mad.techfix.network;

import com.mad.techfix.models.ApiResponse;
import com.mad.techfix.models.AuthResponse;
import com.mad.techfix.models.LoginRequest;
import com.mad.techfix.models.RegisterRequest;
import com.mad.techfix.models.SparePart;
import com.mad.techfix.models.Appointment;
import com.mad.techfix.models.Payment;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.DELETE;
import retrofit2.http.Path;

public interface ApiService {

    @POST("api/auth/login")
    Call<AuthResponse> loginUser(@Body LoginRequest loginRequest);

    @GET("api/spare-parts")
    Call<ApiResponse<List<SparePart>>> getSpareParts(@Header("Authorization") String auth);

    @POST("api/spare-parts")
    Call<ApiResponse<SparePart>> createSparePart(@Header("Authorization") String auth, @Body SparePart part);

    @PUT("api/spare-parts/{id}")
    Call<ApiResponse<SparePart>> updateSparePart(@Header("Authorization") String auth, @Path("id") String partId, @Body SparePart part);

    @DELETE("api/spare-parts/{id}")
    Call<ApiResponse<Object>> deleteSparePart(@Header("Authorization") String auth, @Path("id") String partId);

    @GET("api/appointments")
    Call<ApiResponse<List<Appointment>>> getAppointments(@Header("Authorization") String auth);

    @GET("api/appointments/{id}/history")
    Call<ApiResponse<List<Object>>> getAppointmentHistory(@Header("Authorization") String auth, @Path("id") String appointmentId);

    @POST("api/payments")
    Call<ApiResponse<Payment>> createPayment(@Header("Authorization") String auth, @Body Payment payment);

    @POST("api/auth/register")
    Call<AuthResponse> registerUser(@Body RegisterRequest registerRequest);
}
