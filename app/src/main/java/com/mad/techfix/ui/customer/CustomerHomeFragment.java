package com.mad.techfix.ui.customer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.card.MaterialCardView;
import com.mad.techfix.R;
import com.mad.techfix.data.SessionManager;
import com.mad.techfix.models.ApiResponse;
import com.mad.techfix.models.Device;
import com.mad.techfix.network.RetrofitClient;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerHomeFragment extends Fragment {

    private SessionManager sessionManager;
    private TextView tvWelcome, tvEmail, tvPhone, tvDeviceCount, tvNotificationStatus;
    private MaterialCardView cardProfile, cardDevices, cardNotifications;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_home, container, false);
        
        sessionManager = new SessionManager(requireContext());
        
        tvWelcome = view.findViewById(R.id.tvWelcome);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvPhone = view.findViewById(R.id.tvPhone);
        tvDeviceCount = view.findViewById(R.id.tvDeviceCount);
        tvNotificationStatus = view.findViewById(R.id.tvNotificationStatus);
        
        cardProfile = view.findViewById(R.id.cardProfile);
        cardDevices = view.findViewById(R.id.cardDevices);
        cardNotifications = view.findViewById(R.id.cardNotifications);
        
        String userName = sessionManager.getUserName();
        tvWelcome.setText("Welcome, " + (userName != null && !userName.isEmpty() ? userName : "Customer") + "!");
        
        tvEmail.setText(sessionManager.getUserEmail());
        tvPhone.setText(sessionManager.getUserPhone());

        // Quick navigation clicks (Assuming CustomerDashboardActivity handles bottom nav switching or fragment transactions)
        cardProfile.setOnClickListener(v -> navigateTo(new CustomerProfileFragment()));
        cardDevices.setOnClickListener(v -> navigateTo(new CustomerDevicesFragment()));
        cardNotifications.setOnClickListener(v -> navigateTo(new CustomerNotificationsFragment()));

        fetchDeviceSummary();
        fetchNotificationsSummary();
        
        return view;
    }

    private void fetchDeviceSummary() {
        String token = sessionManager.getBearerToken();
        RetrofitClient.getApiService().getCustomerDevices(token).enqueue(new Callback<ApiResponse<List<Device>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<Device>>> call, @NonNull Response<ApiResponse<List<Device>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Device> devices = response.body().getData();
                    if (devices == null || devices.isEmpty()) {
                        tvDeviceCount.setText("You have no devices registered.");
                    } else {
                        tvDeviceCount.setText("You have " + devices.size() + " registered device(s).");
                    }
                } else {
                    tvDeviceCount.setText("Failed to load devices.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<Device>>> call, @NonNull Throwable t) {
                tvDeviceCount.setText("Network error loading devices.");
            }
        });
    }

    private void fetchNotificationsSummary() {
        // Placeholder for notifications. If the endpoint is added to ApiService, we can call it.
        // For now, just show a default message.
        tvNotificationStatus.setText("You have no new notifications.");
    }

    private void navigateTo(Fragment fragment) {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
}
