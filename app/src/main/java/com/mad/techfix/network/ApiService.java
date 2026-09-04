package com.mad.techfix.network;

import com.mad.techfix.models.ApiResponse;
import com.mad.techfix.models.Appointment;
import com.mad.techfix.models.AppointmentDetail;
import com.mad.techfix.models.AuthResponse;
import com.mad.techfix.models.CreateAppointmentRequest;
import com.mad.techfix.models.Device;
import com.mad.techfix.models.LoginRequest;
import com.mad.techfix.models.Payment;
import com.mad.techfix.models.PaymentIntentRequest;
import com.mad.techfix.models.RepairImage;
import com.mad.techfix.models.CloudinarySignatureResponse;
import com.mad.techfix.models.ImageUploadRequest;
import com.mad.techfix.models.PaymentIntentResponse;
import com.mad.techfix.models.RegisterRequest;
import com.mad.techfix.models.SparePart;
import com.mad.techfix.models.admin.Branch;
import com.mad.techfix.models.admin.Service;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import retrofit2.Call;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {

    // ==========================================
    // MEMBER 1: AUTHENTICATION & HEALTH
    // ==========================================

    @POST("api/auth/login")
    Call<AuthResponse> loginUser(
            @Body LoginRequest loginRequest
    );

    @POST("api/auth/register")
    Call<AuthResponse> registerUser(
            @Body RegisterRequest registerRequest
    );

    @GET("api/me")
    Call<AuthResponse> getMe(
            @Header("Authorization") String auth
    );

    @GET("api/health")
    Call<Map<String, Object>> getHealth();


    // ==========================================
    // MEMBER 4: SPARE PARTS
    // ==========================================

    @GET("api/spare-parts")
    Call<ApiResponse<List<SparePart>>> getSpareParts(
            @Header("Authorization") String auth
    );

    @POST("api/spare-parts")
    Call<ApiResponse<SparePart>> createSparePart(
            @Header("Authorization") String auth,
            @Body SparePart part
    );

    @PUT("api/spare-parts/{id}")
    Call<ApiResponse<SparePart>> updateSparePart(
            @Header("Authorization") String auth,
            @Path("id") String partId,
            @Body SparePart part
    );

    @DELETE("api/spare-parts/{id}")
    Call<ApiResponse<Object>> deleteSparePart(
            @Header("Authorization") String auth,
            @Path("id") String partId
    );


    // ==========================================
    // MEMBER 4: APPOINTMENTS / REPAIR HISTORY
    // ==========================================

    @GET("api/appointments")
    Call<ApiResponse<List<Appointment>>> getAppointments(
            @Header("Authorization") String auth
    );

    @GET("api/appointments/{id}/history")
    Call<ApiResponse<List<Object>>> getAppointmentHistory(
            @Header("Authorization") String auth,
            @Path("id") String appointmentId
    );

    @GET("api/appointments/{id}")
    Call<ApiResponse<AppointmentDetail>> getAppointmentDetail(
            @Header("Authorization") String auth,
            @Path("id") String appointmentId
    );


    // ==========================================
    // MEMBER 4: PAYMENTS
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

    @GET("api/payments/{id}")
    Call<ApiResponse<Payment>> getPayment(
            @Header("Authorization") String auth,
            @Path("id") String paymentId
    );

    @PUT("api/payments/{id}/status")
    Call<ApiResponse<Object>> updatePaymentStatus(
            @Header("Authorization") String auth,
            @Path("id") String paymentId,
            @Body Payment payment
    );


    // ==========================================
    // MEMBER 4: STRIPE
    // ==========================================

    @POST("api/create-payment-intent")
    Call<PaymentIntentResponse> createPaymentIntent(
            @Header("Authorization") String auth,
            @Body PaymentIntentRequest request
    );


    // ==========================================
    // MEMBER 4: CAMERA / IMAGES (CLOUDINARY)
    // ==========================================

    // 1. Get Cloudinary upload signature from the Worker
    @GET("api/cloudinary/signature")
    Call<CloudinarySignatureResponse> getCloudinarySignature(@Header("Authorization") String auth);

    // 2. Save image URL to the TechFix database (JSON body)
    @Multipart
    @POST("api/appointments/{id}/images")
    Call<ApiResponse<Object>> uploadImage(
            @Header("Authorization") String auth,
            @Path("id") String appointmentId,
            @Body ImageUploadRequest request
    );

    // 3. Fetch all images for an appointment
    @GET("api/appointments/{id}/images")
    Call<ApiResponse<List<RepairImage>>> getAppointmentImages(
            @Header("Authorization") String auth,
            @Path("id") String appointmentId
    );

    // 4. Delete an image
    @DELETE("api/appointments/{id}/images/{imageId}")
    Call<ApiResponse<Object>> deleteImage(
            @Header("Authorization") String auth,
            @Path("id") String appointmentId,
            @Path("imageId") String imageId
    );


    // ==========================================
    // MEMBER 2: REPAIR BOOKING
    // ==========================================

    @GET("api/devices")
    Call<ApiResponse<List<Device>>> getMyDevices(
            @Header("Authorization") String auth
    );

    @GET("api/services")
    Call<ApiResponse<List<Service>>> getServices();

    @GET("api/branches")
    Call<ApiResponse<List<Branch>>> getBranches();

    @POST("api/appointments")
    Call<ApiResponse<Map<String, Object>>> createAppointmentRequest(
            @Header("Authorization") String auth,
            @Body CreateAppointmentRequest request
    );


    // ==========================================
    // MEMBER 2: TECHNICIAN WORKFLOW
    // ==========================================

    @GET("api/technician/appointments")
    Call<ApiResponse<List<Appointment>>> getTechnicianAppointments(
            @Header("Authorization") String auth
    );

    @PUT("api/appointments/{id}/status")
    Call<ApiResponse<Object>> updateAppointmentStatus(
            @Header("Authorization") String auth,
            @Path("id") String appointmentId,
            @Body Map<String, Object> body
    );

    @POST("api/appointments/{id}/images")
    Call<ApiResponse<Object>> addAppointmentImage(
            @Header("Authorization") String auth,
            @Path("id") String appointmentId,
            @Body Map<String, Object> body
    );
}