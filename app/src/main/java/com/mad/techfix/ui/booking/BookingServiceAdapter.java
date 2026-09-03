package com.mad.techfix.ui.booking;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.radiobutton.MaterialRadioButton;
import com.mad.techfix.R;
import com.mad.techfix.models.admin.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BookingServiceAdapter
        extends RecyclerView.Adapter<BookingServiceAdapter.ServiceViewHolder> {

    public interface OnServiceSelectedListener {
        void onServiceSelected(Service service);
    }

    private final List<Service> services = new ArrayList<>();
    private final OnServiceSelectedListener listener;

    private int selectedPosition = RecyclerView.NO_POSITION;

    public BookingServiceAdapter(OnServiceSelectedListener listener) {
        this.listener = listener;
    }

    public void setServices(List<Service> newServices) {
        services.clear();

        if (newServices != null) {
            services.addAll(newServices);
        }

        selectedPosition = RecyclerView.NO_POSITION;
        notifyDataSetChanged();
    }

    public Service getSelectedService() {
        if (selectedPosition == RecyclerView.NO_POSITION) {
            return null;
        }

        return services.get(selectedPosition);
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_booking_service, parent, false);

        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ServiceViewHolder holder,
            int position
    ) {

        Service service = services.get(position);

        String serviceName = service.getName();

        if (serviceName == null || serviceName.trim().isEmpty()) {
            serviceName = "Service";
        }

        holder.tvServiceName.setText(serviceName);

        String description = service.getDescription();

        if (description == null || description.trim().isEmpty()) {
            description = "Repair Service";
        }

        holder.tvServiceCategory.setText(description);

        String price = String.format(
                Locale.getDefault(),
                "From LKR %,.2f",
                service.getBasePrice()
        );

        holder.tvServicePrice.setText(price);

        holder.radioSelectService.setChecked(
                selectedPosition == holder.getBindingAdapterPosition()
        );

        holder.itemView.setOnClickListener(v -> {

            int clickedPosition = holder.getBindingAdapterPosition();

            if (clickedPosition == RecyclerView.NO_POSITION) {
                return;
            }

            int previousPosition = selectedPosition;
            selectedPosition = clickedPosition;

            if (previousPosition != RecyclerView.NO_POSITION) {
                notifyItemChanged(previousPosition);
            }

            notifyItemChanged(selectedPosition);

            if (listener != null) {
                listener.onServiceSelected(
                        services.get(selectedPosition)
                );
            }
        });
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    static class ServiceViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvServiceName;
        TextView tvServiceCategory;
        TextView tvServicePrice;

        MaterialRadioButton radioSelectService;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);

            tvServiceName =
                    itemView.findViewById(
                            R.id.tv_service_name
                    );

            tvServiceCategory =
                    itemView.findViewById(
                            R.id.tv_service_category
                    );

            tvServicePrice =
                    itemView.findViewById(
                            R.id.tv_service_price
                    );

            radioSelectService =
                    itemView.findViewById(
                            R.id.radio_select_service
                    );
        }
    }
}