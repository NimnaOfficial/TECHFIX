package com.mad.techfix.ui.parts;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;
import com.mad.techfix.R;
import com.mad.techfix.data.local.database.AppDatabase;
import com.mad.techfix.data.local.database.SparePartEntity;
import com.mad.techfix.models.ApiResponse;
import com.mad.techfix.models.SparePart;
import com.mad.techfix.network.ApiService;
import com.mad.techfix.network.RetrofitClient;
import com.mad.techfix.utils.TokenManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PartsManagerFragment extends Fragment {

    private RecyclerView recyclerView;
    private PartsAdapter adapter;
    private ApiService apiService;
    private TokenManager tokenManager;

    private android.widget.ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_parts_manager, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = view.findViewById(R.id.progress_bar);

        // 1. Setup RecyclerView
        recyclerView = view.findViewById(R.id.rvParts);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PartsAdapter(new ArrayList<>(), this::showPartDialog);
        recyclerView.setAdapter(adapter);

        // 2. Initialize Network Helpers
        apiService = RetrofitClient.getClient().create(ApiService.class);
        tokenManager = new TokenManager(requireContext());

        // 3. Setup FAB for adding new parts
        com.google.android.material.floatingactionbutton.FloatingActionButton fabAdd = view.findViewById(R.id.fab_add_part);
        fabAdd.setOnClickListener(v -> showPartDialog(null));

        // 4. Fetch Real Data from API
        fetchSparePartsFromApi();
    }

    private void showPartDialog(@Nullable SparePartEntity partEntity) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_part_form, null);
        builder.setView(dialogView);
        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        android.widget.TextView tvTitle = dialogView.findViewById(R.id.tv_form_title);
        
        TextInputLayout tilName = dialogView.findViewById(R.id.til_part_name);
        TextInputLayout tilNumber = dialogView.findViewById(R.id.til_part_sku);
        TextInputLayout tilDesc = dialogView.findViewById(R.id.til_part_category);
        TextInputLayout tilPrice = dialogView.findViewById(R.id.til_part_price);
        TextInputLayout tilStock = dialogView.findViewById(R.id.til_part_quantity);

        com.google.android.material.textfield.TextInputEditText etName = dialogView.findViewById(R.id.et_part_name);
        com.google.android.material.textfield.TextInputEditText etNumber = dialogView.findViewById(R.id.et_part_sku);
        android.widget.AutoCompleteTextView etDesc = dialogView.findViewById(R.id.et_part_category);
        com.google.android.material.textfield.TextInputEditText etPrice = dialogView.findViewById(R.id.et_part_price);
        com.google.android.material.textfield.TextInputEditText etStock = dialogView.findViewById(R.id.et_part_quantity);
        
        View btnSave = dialogView.findViewById(R.id.btn_save);
        View btnCancel = dialogView.findViewById(R.id.btn_cancel);
        View btnDelete = dialogView.findViewById(R.id.btn_delete);

        // Clear errors as user types
        etName.addTextChangedListener(new SimpleTextWatcher(tilName));
        etNumber.addTextChangedListener(new SimpleTextWatcher(tilNumber));
        etDesc.addTextChangedListener(new SimpleTextWatcher(tilDesc));
        etPrice.addTextChangedListener(new SimpleTextWatcher(tilPrice));
        etStock.addTextChangedListener(new SimpleTextWatcher(tilStock));

        // Setup Combobox
        String[] categories = new String[]{"Display", "Battery", "Motherboard", "Accessories", "Other"};
        android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, categories);
        etDesc.setAdapter(adapter);

        if (partEntity != null) {
            tvTitle.setText("Edit Spare Part");
            etName.setText(partEntity.getName());
            etNumber.setText(partEntity.getId());
            etDesc.setText(partEntity.getCategory(), false);
            etPrice.setText(String.valueOf(partEntity.getPrice()));
            etStock.setText(String.valueOf(partEntity.getQuantity()));
            btnDelete.setVisibility(View.VISIBLE);
        }

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnDelete.setOnClickListener(v -> {
            if (partEntity != null) {
                dialog.dismiss();
                deletePartViaApi(partEntity.getId());
            }
        });

        btnSave.setOnClickListener(v -> {
            String nameStr = etName.getText() != null ? etName.getText().toString().trim() : "";
            String numStr = etNumber.getText() != null ? etNumber.getText().toString().trim() : "";
            String priceStr = etPrice.getText() != null ? etPrice.getText().toString().trim() : "";
            String stockStr = etStock.getText() != null ? etStock.getText().toString().trim() : "";
            String descStr = etDesc.getText() != null ? etDesc.getText().toString().trim() : "";

            boolean isValid = true;
            if (nameStr.isEmpty()) { tilName.setError("Name is required"); isValid = false; }
            if (numStr.isEmpty()) { tilNumber.setError("Part number is required"); isValid = false; }
            if (descStr.isEmpty()) { tilDesc.setError("Category is required"); isValid = false; }
            if (priceStr.isEmpty()) { tilPrice.setError("Price is required"); isValid = false; }
            if (stockStr.isEmpty()) { tilStock.setError("Stock is required"); isValid = false; }

            if (!isValid) return;

            SparePart part = new SparePart();
            part.setId(partEntity != null ? partEntity.getId() : ("P" + System.currentTimeMillis()));
            part.setName(nameStr);
            part.setPart_number(numStr);
            part.setDescription(descStr);
            part.setIs_active(true);
            
            try { 
                part.setUnit_price(Double.parseDouble(priceStr)); 
            } catch(Exception e){
                tilPrice.setError("Invalid price format");
                return;
            }
            
            try { 
                part.setMinimum_stock(Integer.parseInt(stockStr)); 
            } catch(Exception e){
                tilStock.setError("Invalid stock format");
                return;
            }

            dialog.dismiss();
            
            if (partEntity == null) {
                createPartViaApi(part);
            } else {
                updatePartViaApi(partEntity.getId(), part);
            }
        });
    }
    
    private void createPartViaApi(SparePart part) {
        String token = tokenManager.getToken();
        if (token == null) return;
        progressBar.setVisibility(View.VISIBLE);
        
        apiService.createSparePart("Bearer " + token, part).enqueue(new Callback<ApiResponse<SparePart>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<SparePart>> call, @NonNull Response<ApiResponse<SparePart>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(getContext(), "Part created successfully!", Toast.LENGTH_SHORT).show();
                    fetchSparePartsFromApi();
                } else {
                    Toast.makeText(getContext(), "API Error: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<ApiResponse<SparePart>> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Failed: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    
    private void updatePartViaApi(String id, SparePart part) {
        String token = tokenManager.getToken();
        if (token == null) return;
        progressBar.setVisibility(View.VISIBLE);
        
        apiService.updateSparePart("Bearer " + token, id, part).enqueue(new Callback<ApiResponse<SparePart>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<SparePart>> call, @NonNull Response<ApiResponse<SparePart>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(getContext(), "Part updated successfully!", Toast.LENGTH_SHORT).show();
                    fetchSparePartsFromApi();
                } else {
                    Toast.makeText(getContext(), "API Error: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<ApiResponse<SparePart>> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Failed: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void deletePartViaApi(String id) {
        String token = tokenManager.getToken();
        if (token == null) return;
        progressBar.setVisibility(View.VISIBLE);
        
        apiService.deleteSparePart("Bearer " + token, id).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<Object>> call, @NonNull Response<ApiResponse<Object>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(getContext(), "Part deleted successfully!", Toast.LENGTH_SHORT).show();
                    fetchSparePartsFromApi();
                } else {
                    Toast.makeText(getContext(), "API Error: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<ApiResponse<Object>> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Failed: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void fetchSparePartsFromApi() {
        String token = tokenManager.getToken();

        // Check if user is logged in
        if (token == null) {
            Toast.makeText(getContext(), "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        // Call the API
        apiService.getSpareParts("Bearer " + token).enqueue(new Callback<ApiResponse<List<SparePart>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<SparePart>>> call,
                                   @NonNull Response<ApiResponse<List<SparePart>>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<SparePart> apiParts = response.body().getData();
                    List<SparePartEntity> entityList = new ArrayList<>();

                    for (SparePart part : apiParts) {
                        SparePartEntity entity = new SparePartEntity(
                                part.getId(),
                                "B001", 
                                part.getName(),
                                part.getDescription(), 
                                part.getMinimum_stock(),
                                part.getUnit_price()
                        );
                        entityList.add(entity);
                    }

                    adapter.updateList(entityList);
                } else {
                    Toast.makeText(getContext(), "Failed to load parts: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<SparePart>>> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Helper TextWatcher to clear errors
    private static class SimpleTextWatcher implements TextWatcher {
        private final TextInputLayout til;
        public SimpleTextWatcher(TextInputLayout til) { this.til = til; }
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
            til.setError(null);
        }
        @Override public void afterTextChanged(Editable s) {}
    }
}

