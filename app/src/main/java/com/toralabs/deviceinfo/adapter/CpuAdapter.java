package com.toralabs.deviceinfo.adapter;

import android.app.ActivityOptions;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.toralabs.deviceinfo.R;
import com.toralabs.deviceinfo.activities.ListFeaturesActivity;
import com.toralabs.deviceinfo.models.CpuModel;

import java.util.List;

public class CpuAdapter extends RecyclerView.Adapter<CpuAdapter.ViewHolder> {
    private final Context context;
    List<CpuModel> list;
    int color;

    public CpuAdapter(Context context, List<CpuModel> list, int color) {
        this.context = context;
        this.list = list;
        this.color = color;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.size() == 1) {
            holder.desc.setText(payloads.get(0).toString());
        } else {
            super.onBindViewHolder(holder, position, payloads);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.title.setText(list.get(position).getTitle());
        holder.desc.setText(list.get(position).getDesc());
        holder.desc.setTextColor(color);
        holder.rel_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list.get(position).getTag().equals("extensions")) {
                    Intent intent = new Intent(context, ListFeaturesActivity.class);
                    intent.putExtra("name", "ext");
                    ActivityOptions options = ActivityOptions.makeCustomAnimation(context, android.R.anim.fade_in, android.R.anim.fade_out);
                    context.startActivity(intent, options.toBundle());
                }
            }
        });
        holder.rel_main.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("Text Label", list.get(position).getDesc());
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(context.getApplicationContext(), context.getResources().getString(R.string.copied) + " " + list.get(position).getTitle(), Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, desc;
        RelativeLayout rel_main;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            desc = itemView.findViewById(R.id.desc);
            rel_main = itemView.findViewById(R.id.rel_main);
        }
    }
}
