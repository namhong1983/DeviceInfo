package com.toralabs.deviceinfo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.toralabs.deviceinfo.R;
import com.toralabs.deviceinfo.models.CpuFreqModel;

import java.util.List;

public class CpuFreqAdapter extends RecyclerView.Adapter<CpuFreqAdapter.ViewHolder> {
    List<CpuFreqModel> list;
    private final Context context;
    private final int color;

    public CpuFreqAdapter(List<CpuFreqModel> list, Context context, int color) {
        this.list = list;
        this.context = context;
        this.color = color;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cpufreq_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.size() != 0) {
            if (payloads.get(0) instanceof String)
                holder.txtCoreStatus.setText(payloads.get(0).toString());
        } else {
            super.onBindViewHolder(holder, position, payloads);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.cardCpu.setCardBackgroundColor(color);
        holder.txtCore.setText(list.get(position).getCoreNo());
        holder.txtCoreStatus.setText(list.get(position).getCpuFreq());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtCore, txtCoreStatus;
        CardView cardCpu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCore = itemView.findViewById(R.id.txtCore);
            cardCpu = itemView.findViewById(R.id.cardCpu);
            txtCoreStatus = itemView.findViewById(R.id.txtCoreStatus);
        }
    }
}
