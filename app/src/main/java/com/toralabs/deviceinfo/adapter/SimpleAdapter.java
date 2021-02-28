/*
Copyright 2020 Mrudul Tora (ToraLabs)
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package com.toralabs.deviceinfo.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.toralabs.deviceinfo.R;
import com.toralabs.deviceinfo.models.SimpleModel;
import java.util.List;

/**
 * Created by @mrudultora
 */

public class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.Viewholder> {
    private List<SimpleModel> list;
    final private Context context;
    final private int color;

    public SimpleAdapter(List<SimpleModel> list, Context context, int color) {
        this.list = list;
        this.context = context;
        this.color = color;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        holder.title.setText(list.get(position).getTitle());
        holder.desc.setText(list.get(position).getDesc());
        holder.desc.setTextColor(color);

    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, final int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            holder.title.setText(list.get(position).getTitle());
            holder.desc.setText(list.get(position).getDesc());
            holder.desc.setTextColor(color);
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
        } else if (payloads.size() == 1) {
            holder.desc.setText(payloads.get(0).toString());
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class Viewholder extends RecyclerView.ViewHolder {
        TextView title, desc;
        RelativeLayout rel_main;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            desc = itemView.findViewById(R.id.desc);
            rel_main = itemView.findViewById(R.id.rel_main);
        }
    }

    public void dataSetCamera(List<SimpleModel> arrayList){
        this.list=arrayList;
        notifyDataSetChanged();
    }

    public void update(int position, String data) {
        notifyItemRangeChanged(position, 1, data);
    }

}
