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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.toralabs.deviceinfo.R;
import com.toralabs.deviceinfo.models.ClickableModel;

import java.util.List;

/**
 * Created by @mrudultora
 */

public class ClickableAdapter extends RecyclerView.Adapter<ClickableAdapter.Viewholder> {
    private final Context context;
    List<ClickableModel> list;
    private final int color;
    ItemClickListener itemClickListener;

    public ClickableAdapter(Context context, List<ClickableModel> list, int color, ItemClickListener itemClickListener) {
        this.context = context;
        this.list = list;
        this.color = color;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout, parent, false);
        return new ClickableAdapter.Viewholder(view,itemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, final int position) {
        holder.title.setText(list.get(position).getTitle());
        holder.desc.setText(list.get(position).getDesc());
        holder.desc.setTextColor(color);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    class Viewholder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView title, desc;
        RelativeLayout rel_main;
        ItemClickListener itemClickListener;

        public Viewholder(@NonNull View itemView,ItemClickListener itemClickListener) {
            super(itemView);
            this.itemClickListener=itemClickListener;
            title = itemView.findViewById(R.id.title);
            desc = itemView.findViewById(R.id.desc);
            rel_main = itemView.findViewById(R.id.rel_main);
            rel_main.setOnClickListener(this);
            rel_main.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onItemClick(getAdapterPosition(),desc);
        }

        @Override
        public boolean onLongClick(View v) {
            itemClickListener.onItemLongClick(getAdapterPosition(),desc);
            return true;
        }
    }

    public interface ItemClickListener{
        void onItemClick(int position,View view);
        void onItemLongClick(int position,View view);
    }
}
