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

    // POST https://techfix-api.codse251f-003.workers.dev/api/auth/register
    @POST("api/auth/register")
    Call<AuthResponse> registerUser(@Body RegisterRequest registerRequest);

    // GET https://techfix-api.codse251f-003.workers.dev/api/spare-parts
    @GET("api/spare-parts")
    Call<ApiResponse<List<SparePart>>> getSpareParts(@Header("Authorization") String token);
}