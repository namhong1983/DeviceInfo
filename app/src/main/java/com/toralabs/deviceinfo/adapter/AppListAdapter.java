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

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.toralabs.deviceinfo.R;
import com.toralabs.deviceinfo.impClasses.ExtractThread;
import com.toralabs.deviceinfo.models.AppListModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.Viewholder> {
    private final Context context;
    private List<AppListModel> list;
    private final int color;
    ImageView icon;
    View view;
    LinearLayout linear_minsdk;
    RelativeLayout rel_head;
    TextView txt_name, txt_pkg, txt_version, txt_targetsdk, txt_minsdk, txt_size, txt_uid, txt_permissions, tv_per;

    public AppListAdapter(Context context, List<AppListModel> list, int color,View view) {
        this.context = context;
        this.list = list;
        this.color = color;
        this.view=view;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_app_layout, parent, false);
        return new Viewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, final int position) {
        holder.img_icon.setImageDrawable(list.get(position).getIcon());
        holder.name.setText(list.get(position).getName());
        holder.packagename.setText(list.get(position).getPackageName());
        holder.size.setText(list.get(position).getSize());

        holder.rel_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);
                View customview = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_appdetails, null);
                builder.setView(customview);
                icon = customview.findViewById(R.id.icon);
                icon.setImageDrawable(list.get(position).getIcon());
                txt_minsdk = customview.findViewById(R.id.txt_minsdk);
                linear_minsdk = customview.findViewById(R.id.linear_minsdk);
                if (list.get(position).getMinsdk() != null) {
                    linear_minsdk.setVisibility(View.VISIBLE);
                    txt_minsdk.setText(list.get(position).getMinsdk());
                }
                txt_name = customview.findViewById(R.id.txt_name);
                txt_pkg = customview.findViewById(R.id.txt_pkg);
                txt_version = customview.findViewById(R.id.txt_version);
                txt_targetsdk = customview.findViewById(R.id.txt_targetsdk);
                txt_size = customview.findViewById(R.id.txt_size);
                txt_uid = customview.findViewById(R.id.txt_uid);
                txt_permissions = customview.findViewById(R.id.txt_permissions);
                tv_per = customview.findViewById(R.id.tv_per);
                rel_head = customview.findViewById(R.id.rel_head);
                txt_name.setText(list.get(position).getName());
                txt_pkg.setText(list.get(position).getPackageName());
                txt_version.setText(list.get(position).getVersion());
                txt_targetsdk.setText(list.get(position).getTargetsdk());
                txt_size.setText(list.get(position).getSize());
                txt_uid.setText(list.get(position).getUid());
                txt_permissions.setText(list.get(position).getPermissions());
                Log.d("listper", list.get(position).getPermissions());
                if (list.get(position).getPermissions() != null) {
                    tv_per.setVisibility(View.VISIBLE);
                    txt_permissions.setVisibility(View.VISIBLE);
                }
                rel_head.setBackgroundColor(color);
                AlertDialog dialog = builder.create();
                dialog.setButton(-2, context.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.setButton(-1, context.getResources().getString(R.string.extract), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        extract(position, list.get(position).getName());
                        dialog.dismiss();
                    }
                });
                dialog.show();
                final Button btn_canel = dialog.getButton(-2);
                final Button btn_extract = dialog.getButton(-1);
                btn_canel.setTextColor(color);
                btn_extract.setTextColor(color);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        ImageView img_icon;
        TextView name, packagename, size;
        RelativeLayout rel_main;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            rel_main = itemView.findViewById(R.id.rel_main);
            img_icon = itemView.findViewById(R.id.appicon);
            name = itemView.findViewById(R.id.appname);
            packagename = itemView.findViewById(R.id.pkgname);
            size = itemView.findViewById(R.id.txt_size);
        }
    }

    public void filteredList(ArrayList<AppListModel> arrayList) {
        list = arrayList;
        notifyDataSetChanged();
    }

    public void extract(int position, String name) {
        File file = new File(String.valueOf(list.get(position).getFile()));
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            File newFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), name + ".apk");
            ExtractThread extractThread = new ExtractThread(context, file, newFile, newFile.getPath(), color,view);
            extractThread.start();
        } else {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }
}
