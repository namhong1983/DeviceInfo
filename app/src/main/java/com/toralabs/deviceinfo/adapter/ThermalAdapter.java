package com.toralabs.deviceinfo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.toralabs.deviceinfo.R;
import com.toralabs.deviceinfo.models.ThermalModel;

import java.util.List;

public class ThermalAdapter extends RecyclerView.Adapter<ThermalAdapter.ViewHolder> {
    List<ThermalModel> list;
    private final Context context;
    private final int itemNo;

    public ThermalAdapter(List<ThermalModel> list, Context context, int itemNo) {
        this.list = list;
        this.context = context;
        this.itemNo = itemNo;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(itemNo==1) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_thermal_layout, parent, false);
            return new ViewHolder(view);
        }
        View view = LayoutInflater.from(context).inflate(R.layout.item_codec_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.size() != 0) {
            if (payloads.get(0) instanceof String)
                holder.desc.setText(payloads.get(0).toString());
        } else {
            super.onBindViewHolder(holder, position, payloads);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText(list.get(position).getType());
        holder.desc.setText(list.get(position).getTemp());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, desc;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            desc = itemView.findViewById(R.id.desc);
        }
    }
}
