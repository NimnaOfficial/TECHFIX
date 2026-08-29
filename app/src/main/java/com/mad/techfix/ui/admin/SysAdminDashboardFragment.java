package com.mad.techfix.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.mad.techfix.R;
import com.mad.techfix.data.SessionManager;
import com.mad.techfix.ui.auth.LoginActivity;

public class SysAdminDashboardFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sys_admin_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageButton btnLogout = view.findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(v -> {
            SessionManager sessionManager = new SessionManager(requireContext());
            sessionManager.clearSession();
            Toast.makeText(requireContext(), "Signed out safely", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });
        
        view.findViewById(R.id.btn_manage_managers).setOnClickListener(v -> {
            Toast.makeText(getContext(), "Opening User Management...", Toast.LENGTH_SHORT).show();
        });
        
        view.findViewById(R.id.btn_system_logs).setOnClickListener(v -> {
            Toast.makeText(getContext(), "Loading System Logs...", Toast.LENGTH_SHORT).show();
        });
    }
}
