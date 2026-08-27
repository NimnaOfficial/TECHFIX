package com.mad.techfix.ui.parts;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.mad.techfix.R;
import com.mad.techfix.data.local.database.SparePartEntity;
import java.util.List;
import com.mad.techfix.R;

public class PartsAdapter extends RecyclerView.Adapter<PartsAdapter.ViewHolder> {

    private List<SparePartEntity> partsList;
    private OnPartClickListener listener;

    public interface OnPartClickListener {
        void onPartClick(SparePartEntity part);
    }

    public PartsAdapter(List<SparePartEntity> partsList, OnPartClickListener listener) {
        this.partsList = partsList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_part, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SparePartEntity part = partsList.get(position);
        holder.tvPartName.setText(part.getName());
        holder.tvCategory.setText("Category: " + part.getCategory());
        holder.tvQuantity.setText("Qty: " + part.getQuantity());
        holder.tvPrice.setText("$" + part.getPrice());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onPartClick(part);
        });
        
        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) listener.onPartClick(part);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return partsList == null ? 0 : partsList.size();
    }

    public void updateList(List<SparePartEntity> newList) {
        this.partsList = newList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPartName, tvCategory, tvQuantity, tvPrice;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPartName = itemView.findViewById(R.id.tvPartName);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }
    }
}