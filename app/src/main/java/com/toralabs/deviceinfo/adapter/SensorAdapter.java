package com.toralabs.deviceinfo.adapter;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.toralabs.deviceinfo.R;
import com.toralabs.deviceinfo.models.SensorListModel;

import java.util.List;

public class SensorAdapter extends RecyclerView.Adapter<SensorAdapter.Viewholder> {
    private final Context context;
    private final List<SensorListModel> list;

    public SensorAdapter(Context context, List<SensorListModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_sensor_layout, parent, false);
        return new Viewholder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final Viewholder holder, final int position) {
        if (holder.type != null) {
            holder.type.setVisibility(View.VISIBLE);
            holder.type.setText(context.getResources().getString(R.string.type) + " : " + list.get(position).getType());
        }
        if (holder.img_sensor != null) {
            holder.img_sensor.setVisibility(View.VISIBLE);
            holder.img_sensor.setImageDrawable(ContextCompat.getDrawable(context,list.get(position).getImage()));
        }
        holder.name.setText(list.get(position).getName());
        holder.vendor.setText(context.getResources().getString(R.string.vendor) + " : " + list.get(position).getVendor());
        holder.power.setText(context.getResources().getString(R.string.power) + " : " + list.get(position).getPower());

        holder.card_sensor.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String data = context.getResources().getString(R.string.sensorname) + " : " + list.get(position).getName() + "\n" +
                        context.getResources().getString(R.string.vendor) + " : " + list.get(position).getVendor() + "\n" +
                        context.getResources().getString(R.string.power) + " : " + list.get(position).getPower() + "\n";
                if (holder.type != null) {
                    data += context.getResources().getString(R.string.type) + " : " + list.get(position).getType() + "\n";
                }
                ClipboardManager manager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("Label", data);
                manager.setPrimaryClip(clipData);
                Toast.makeText(context.getApplicationContext(), context.getResources().getString(R.string.copied) + " " + list.get(position).getName() + " " + context.getResources().getString(R.string.details), Toast.LENGTH_SHORT).show();
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class Viewholder extends RecyclerView.ViewHolder {

        TextView name, vendor, type, power;
        ImageView img_sensor;
        CardView card_sensor;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            img_sensor = itemView.findViewById(R.id.img_sensor);
            name = itemView.findViewById(R.id.sensor_name);
            vendor = itemView.findViewById(R.id.vendor_name);
            type = itemView.findViewById(R.id.txt_type);
            power = itemView.findViewById(R.id.txt_power);
            card_sensor = itemView.findViewById(R.id.card_sensor);
        }
    }
}
