package com.mad.techfix.ui.parts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.mad.techfix.R;
import com.mad.techfix.data.local.database.AppDatabase;
import com.mad.techfix.data.local.database.SparePartEntity;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.mad.techfix.R;

public class PartsManagerFragment extends Fragment {

    private RecyclerView recyclerView;
    private PartsAdapter adapter;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_parts_manager, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.rvParts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PartsAdapter(java.util.Collections.emptyList());
        recyclerView.setAdapter(adapter);

        AppDatabase db = AppDatabase.getInstance(getContext());

        // Insert dummy data in background
        executor.execute(() -> {
            SparePartEntity dummy = new SparePartEntity("P001", "B001", "iPhone 14 Screen", "Display", 15, 85.00);
            db.techFixDao().insertPart(dummy);

            // Fetch data in background
            List<SparePartEntity> parts = db.techFixDao().getPartsByBranch("B001");
            requireActivity().runOnUiThread(() -> adapter.updateList(parts));
        });
    }
}