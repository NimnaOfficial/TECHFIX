package com.mad.techfix.ui.technician;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mad.techfix.R;
import com.mad.techfix.ui.history.RepairHistoryFragment;

public class TechnicianActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_technician
        );

        bottomNavigationView =
                findViewById(
                        R.id.technician_bottom_nav
                );

        setupBottomNavigation();

        if (savedInstanceState == null) {

            bottomNavigationView.setSelectedItemId(
                    R.id.nav_technician_dashboard
            );

            openFragment(
                    new TechnicianDashboardFragment()
            );
        }
    }

    private void setupBottomNavigation() {

        bottomNavigationView
                .setOnItemSelectedListener(item -> {

                    int itemId =
                            item.getItemId();

                    if (itemId ==
                            R.id.nav_technician_dashboard) {

                        openFragment(
                                new TechnicianDashboardFragment()
                        );

                        return true;
                    }

                    if (itemId ==
                            R.id.nav_assigned_repairs) {

                        openFragment(
                                new AssignedRepairsFragment()
                        );

                        return true;
                    }

                    if (itemId ==
                            R.id.nav_repair_history) {

                        openFragment(
                                new RepairHistoryFragment()
                        );

                        return true;
                    }

                    return false;
                });
    }

    private void openFragment(
            @NonNull Fragment fragment
    ) {

        getSupportFragmentManager()
                .beginTransaction()
                .replace(
                        R.id.technician_fragment_container,
                        fragment
                )
                .commit();
    }
}