package com.mad.techfix.ui.admin;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mad.techfix.R;
import com.mad.techfix.ui.admin.branches.BranchListFragment;
import com.mad.techfix.ui.admin.dashboard.AdminDashboardFragment;
import com.mad.techfix.ui.admin.technicians.TechnicianListFragment;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_dashboard) {
                loadFragment(new AdminDashboardFragment());
                return true;
            } else if (id == R.id.nav_branches) {
                loadFragment(new BranchListFragment());
                return true;
            } else if (id == R.id.nav_technicians) {
                loadFragment(new TechnicianListFragment());
                return true;
            }
            return false;
        });

        if (savedInstanceState == null) {
            bottomNav.setSelectedItemId(R.id.nav_dashboard);
        }
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
