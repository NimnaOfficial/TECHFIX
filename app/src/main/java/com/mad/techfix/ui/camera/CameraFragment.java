package com.mad.techfix.ui.camera;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.mad.techfix.R;
import com.mad.techfix.models.ApiResponse;
import com.mad.techfix.models.CloudinarySignatureResponse;
import com.mad.techfix.models.ImageUploadRequest;
import com.mad.techfix.models.RepairImage;
import com.mad.techfix.network.ApiService;
import com.mad.techfix.network.RetrofitClient;
import com.mad.techfix.utils.TokenManager;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Callback;
import retrofit2.Callback;

import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;

import com.google.common.util.concurrent.ListenableFuture;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class CameraFragment extends Fragment {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final String TAG = "CameraFragment";

    private PreviewView previewView;
    private TextInputEditText etAppointmentId;
    private MaterialButton btnCapture, btnUpload;
    private ProgressBar progressBar;
    private RecyclerView rvImages;

    private ImageCapture imageCapture;
    private File capturedFile;
    private ImageAdapter imageAdapter;
    private ApiService apiService;
    private TokenManager tokenManager;
    private boolean isCameraReady = false;
    private OkHttpClient okHttpClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Init views
        previewView = view.findViewById(R.id.preview_view);
        etAppointmentId = view.findViewById(R.id.et_appointment_id);
        btnCapture = view.findViewById(R.id.btn_capture);
        btnUpload = view.findViewById(R.id.btn_upload);
        progressBar = view.findViewById(R.id.progress_bar);
        rvImages = view.findViewById(R.id.rv_images);

        btnCapture.setEnabled(false);

        // Setup RecyclerView
        rvImages.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        imageAdapter = new ImageAdapter(new ImageAdapter.OnImageClickListener() {
            @Override
            public void onImageClick(RepairImage image) {
                Toast.makeText(getContext(), "Image clicked", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeleteClick(RepairImage image) {
                deleteImage(image);
            }
        });
        rvImages.setAdapter(imageAdapter);

        // Init helpers
        apiService = RetrofitClient.getClient().create(ApiService.class);
        tokenManager = new TokenManager(requireContext());
        okHttpClient = new OkHttpClient();

        // Check permissions
        if (hasCameraPermission()) {
            startCamera();
        } else {
            requestPermissions();
        }

        // Capture button
        btnCapture.setOnClickListener(v -> capturePhoto());

        // Upload button
        btnUpload.setOnClickListener(v -> uploadImage());

        // Fetch images when appointment ID changes
        etAppointmentId.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                fetchImages();
            }
        });
    }

    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(requireActivity(),
                new String[]{Manifest.permission.CAMERA},
                PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(getContext(), "Camera permission required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build();

                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
                cameraProvider.bindToLifecycle(getViewLifecycleOwner(), cameraSelector, preview, imageCapture);

                isCameraReady = true;
                btnCapture.setEnabled(true);

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    private void capturePhoto() {
        if (!isCameraReady || imageCapture == null) {
            Toast.makeText(getContext(), "⏳ Camera is initializing... Please wait.", Toast.LENGTH_SHORT).show();
            btnCapture.postDelayed(this::capturePhoto, 1000);
            return;
        }

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File outputDirectory = requireContext().getCacheDir();
        capturedFile = new File(outputDirectory, "IMG_" + timestamp + ".jpg");

        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(capturedFile).build();

        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(requireContext()), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                Toast.makeText(getContext(), "📸 Photo captured", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "📸 Photo saved to: " + capturedFile.getAbsolutePath());
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Log.e(TAG, "❌ Capture error: " + exception.getMessage(), exception);
                Toast.makeText(getContext(), "Error: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ==========================================
    // UPLOAD IMAGE VIA CLOUDINARY
    // ==========================================
    private void uploadImage() {
        String appointmentId = etAppointmentId.getText() != null ? etAppointmentId.getText().toString().trim() : "";
        if (appointmentId.isEmpty()) {
            Toast.makeText(getContext(), "Please enter an Appointment ID", Toast.LENGTH_SHORT).show();
            return;
        }

        if (capturedFile == null || !capturedFile.exists()) {
            Toast.makeText(getContext(), "No photo to upload", Toast.LENGTH_SHORT).show();
            return;
        }

        String token = tokenManager.getToken();
        if (token == null) {
            Toast.makeText(getContext(), "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "📸 Starting upload for appointment: " + appointmentId);
        Log.d(TAG, "📸 Token: " + (token != null ? token.substring(0, Math.min(token.length(), 20)) + "..." : "NULL"));
        Log.d(TAG, "📸 File exists: " + capturedFile.exists() + ", size: " + capturedFile.length());

        progressBar.setVisibility(View.VISIBLE);
        btnUpload.setEnabled(false);

        apiService.getCloudinarySignature("Bearer " + token).enqueue(new retrofit2.Callback<CloudinarySignatureResponse>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<CloudinarySignatureResponse> call, @NonNull retrofit2.Response<CloudinarySignatureResponse> response) {
                Log.d(TAG, "📡 Signature response code: " + response.code());

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    CloudinarySignatureResponse.CloudinaryData data = response.body().getData();
                    Log.d(TAG, "✅ Signature received: cloudName=" + data.getCloudName() + ", apiKey=" + data.getApiKey());
                    uploadToCloudinary(data, appointmentId);
                } else {
                    String errorMsg = "Failed to get upload signature";
                    try {
                        if (response.errorBody() != null) {
                            errorMsg = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.e(TAG, "❌ Signature error: " + errorMsg);
                    Toast.makeText(getContext(), "❌ " + errorMsg, Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    btnUpload.setEnabled(true);
                }
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<CloudinarySignatureResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "❌ Network error getting signature: " + t.getMessage(), t);
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                btnUpload.setEnabled(true);
            }
        });
    }

    private void uploadToCloudinary(CloudinarySignatureResponse.CloudinaryData data, String appointmentId) {
        MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpeg");
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JPG, capturedFile);

        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", capturedFile.getName(), requestBody)
                .addFormDataPart("api_key", data.getApiKey())
                .addFormDataPart("timestamp", String.valueOf(data.getTimestamp()))
                .addFormDataPart("signature", data.getSignature())
                .addFormDataPart("folder", data.getFolder())
                .addFormDataPart("upload_preset", data.getUploadPreset());

        RequestBody cloudinaryBody = builder.build();
        String cloudinaryUrl = "https://api.cloudinary.com/v1_1/" + data.getCloudName() + "/image/upload";

        Log.d(TAG, "📡 Uploading to Cloudinary: " + cloudinaryUrl);

        Request cloudinaryRequest = new Request.Builder()
                .url(cloudinaryUrl)
                .post(cloudinaryBody)
                .build();

        okHttpClient.newCall(cloudinaryRequest).enqueue(new okhttp3.Callback() {
            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = response.body().string();
                    Log.d(TAG, "📡 Cloudinary response: " + jsonResponse);
                    try {
                        JSONObject jsonObject = new JSONObject(jsonResponse);
                        String secureUrl = jsonObject.getString("secure_url");
                        Log.d(TAG, "✅ Uploaded to Cloudinary, secure_url: " + secureUrl);

                        requireActivity().runOnUiThread(() -> {
                            saveImageUrlToBackend(appointmentId, secureUrl);
                        });

                    } catch (JSONException e) {
                        Log.e(TAG, "❌ Failed to parse Cloudinary response: " + e.getMessage(), e);
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "Failed to parse Cloudinary response", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            btnUpload.setEnabled(true);
                        });
                    }
                } else {
                    String errorBody = response.body() != null ? response.body().string() : "null";
                    Log.e(TAG, "❌ Cloudinary upload failed: " + response.code() + " - " + errorBody);
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Cloudinary upload failed: " + response.code(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        btnUpload.setEnabled(true);
                    });
                }
            }

            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                Log.e(TAG, "❌ Cloudinary error: " + e.getMessage(), e);
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Cloudinary error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    btnUpload.setEnabled(true);
                });
            }
        });
    }

    private void saveImageUrlToBackend(String appointmentId, String imageUrl) {
        String token = tokenManager.getToken();
        if (token == null) {
            progressBar.setVisibility(View.GONE);
            btnUpload.setEnabled(true);
            return;
        }

        Log.d(TAG, "📡 Saving image URL to backend: " + imageUrl);

        ImageUploadRequest request = new ImageUploadRequest(imageUrl, "REPAIR_IMAGE");

        apiService.uploadImage("Bearer " + token, appointmentId, request).enqueue(new retrofit2.Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<ApiResponse<Object>> call, @NonNull retrofit2.Response<ApiResponse<Object>> response) {
                progressBar.setVisibility(View.GONE);
                btnUpload.setEnabled(true);

                Log.d(TAG, "📡 Backend save response code: " + response.code());

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(getContext(), "✅ Image uploaded successfully!", Toast.LENGTH_SHORT).show();
                    capturedFile = null;
                    fetchImages();
                } else {
                    String errorMsg = "Failed to save image";
                    try {
                        if (response.errorBody() != null) {
                            errorMsg = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.e(TAG, "❌ Backend save error: " + errorMsg);
                    Toast.makeText(getContext(), "❌ " + errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<ApiResponse<Object>> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnUpload.setEnabled(true);
                Log.e(TAG, "❌ Network error saving image: " + t.getMessage(), t);
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // ==========================================
    // FETCH IMAGES
    // ==========================================
    private void fetchImages() {
        String appointmentId = etAppointmentId.getText() != null ? etAppointmentId.getText().toString().trim() : "";
        if (appointmentId.isEmpty()) {
            imageAdapter.updateList(null);
            return;
        }

        String token = tokenManager.getToken();
        if (token == null) {
            Toast.makeText(getContext(), "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }

        apiService.getAppointmentImages("Bearer " + token, appointmentId).enqueue(new retrofit2.Callback<ApiResponse<List<RepairImage>>>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<ApiResponse<List<RepairImage>>> call, @NonNull retrofit2.Response<ApiResponse<List<RepairImage>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<RepairImage> images = response.body().getData();
                    if (images != null && !images.isEmpty()) {
                        imageAdapter.updateList(images);
                        Toast.makeText(getContext(), "✅ Loaded " + images.size() + " images", Toast.LENGTH_SHORT).show();
                    } else {
                        imageAdapter.updateList(null);
                        Toast.makeText(getContext(), "No images found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to fetch images", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<ApiResponse<List<RepairImage>>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ==========================================
    // DELETE IMAGE
    // ==========================================
    private void deleteImage(RepairImage image) {
        String token = tokenManager.getToken();
        if (token == null) {
            Toast.makeText(getContext(), "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }

        apiService.deleteImage("Bearer " + token, image.getAppointment_id(), image.getId()).enqueue(new retrofit2.Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<ApiResponse<Object>> call, @NonNull retrofit2.Response<ApiResponse<Object>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(getContext(), "Image deleted", Toast.LENGTH_SHORT).show();
                    fetchImages();
                } else {
                    Toast.makeText(getContext(), "Failed to delete", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<ApiResponse<Object>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}