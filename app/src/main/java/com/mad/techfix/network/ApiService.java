package com.mad.techfix.network;

import com.mad.techfix.models.ApiResponse;
import com.mad.techfix.models.Appointment;
import com.mad.techfix.models.LoginRequest;
import com.mad.techfix.models.LoginResponse;
import com.mad.techfix.models.Payment;
import com.mad.techfix.models.SparePart;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {

    // ==========================================
    // 1. AUTHENTICATION (For logging in to get the token)
    // ==========================================
    @POST("api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    // ==========================================
    // 2. PARTS MANAGER (YOUR MODULE)
    // ==========================================
    @GET("api/spare-parts")
    Call<ApiResponse<List<SparePart>>> getSpareParts(@Header("Authorization") String auth);

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

    @GET("api/appointments/{id}/payments")
    Call<ApiResponse<List<Payment>>> getAppointmentPayments(
            @Header("Authorization") String auth,
            @Path("id") String appointmentId
    );

    // ==========================================
    // 5. CAMERA / IMAGES (YOUR MODULE)
    // ==========================================
    @Multipart
    @POST("api/appointments/{id}/images")
    Call<ApiResponse<Object>> uploadImage(
            @Header("Authorization") String auth,
            @Path("id") String appointmentId,
            @Part MultipartBody.Part image,
            @Part("image_type") RequestBody imageType
    );

    @GET("api/appointments/{id}/images")
    Call<ApiResponse<List<Object>>> getAppointmentImages(
            @Header("Authorization") String auth,
            @Path("id") String appointmentId
    );
}