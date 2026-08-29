package com.mad.techfix.ui.payment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.mad.techfix.R;
import com.mad.techfix.models.Payment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.ViewHolder> {

    private List<Payment> paymentList = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Payment payment = paymentList.get(position);

        holder.tvAmount.setText(String.format("$%.2f", payment.getAmount()));
        holder.tvPaymentId.setText(payment.getId() != null ? payment.getId() : "N/A");
        holder.tvMethod.setText(payment.getPayment_method() != null ? payment.getPayment_method() : "UNKNOWN");

        // Status
        String status = payment.getPayment_status() != null ? payment.getPayment_status() : "PENDING";
        holder.tvStatus.setText(status);

        // Color status badge
        int color;
        switch (status.toUpperCase()) {
            case "PAID":
            case "COMPLETED":
                color = ContextCompat.getColor(holder.itemView.getContext(), R.color.status_completed);
                break;
            case "FAILED":
            case "CANCELLED":
                color = ContextCompat.getColor(holder.itemView.getContext(), R.color.status_cancelled);
                break;
            default:
                color = ContextCompat.getColor(holder.itemView.getContext(), R.color.status_pending);
                break;
        }
        holder.tvStatus.setTextColor(color);

        // Date
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = inputFormat.parse(payment.getCreated_at());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
            holder.tvDate.setText(outputFormat.format(date));
        } catch (Exception e) {
            holder.tvDate.setText(payment.getCreated_at());
        }
    }

    @Override
    public int getItemCount() {
        return paymentList.size();
    }

    public void updateList(List<Payment> newList) {
        this.paymentList = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvAmount, tvPaymentId, tvMethod, tvStatus, tvDate;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAmount = itemView.findViewById(R.id.tv_payment_amount);
            tvPaymentId = itemView.findViewById(R.id.tv_payment_id);
            tvMethod = itemView.findViewById(R.id.tv_payment_method);
            tvStatus = itemView.findViewById(R.id.tv_payment_status);
            tvDate = itemView.findViewById(R.id.tv_payment_date);
        }
    }
}