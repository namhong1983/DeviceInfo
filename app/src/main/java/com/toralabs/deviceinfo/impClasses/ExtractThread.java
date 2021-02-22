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
package com.toralabs.deviceinfo.impClasses;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.toralabs.deviceinfo.R;
import com.toralabs.deviceinfo.activities.MainActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ExtractThread extends Thread {
    private final File file;
    private final File newFile;
    private final Context context;
    private final String path;
    AlertDialog dialog;
    private final int color;
    View view;

    public ExtractThread(Context context, File file, File newFile, String path, int color, View view) {
        this.file = file;
        this.newFile = newFile;
        this.context = context;
        this.path = path;
        this.color = color;
        this.view = view;
    }

    @Override
    public void run() {
        super.run();
        try {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showDialog(true);
                }
            });
            InputStream inputStream = new FileInputStream(file);
            OutputStream outputStream = new FileOutputStream(newFile);
            byte[] buf = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, len);
            }
            inputStream.close();
            outputStream.close();
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showDialog(false);
                    showSnackBar(true);
                    Toast.makeText(context, context.getResources().getString(R.string.extracted), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (final IOException e) {
            ((MainActivity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showDialog(false);
                    showSnackBar(false);
                    Toast.makeText(context, context.getResources().getString(R.string.unabletoext), Toast.LENGTH_SHORT).show();
                }
            });
            e.printStackTrace();
        }
    }

    @SuppressLint("inflateParams")
    public void showSnackBar(boolean saved) {
        Snackbar snackbar = Snackbar.make(view, "", BaseTransientBottomBar.LENGTH_LONG);
        View custom = ((Activity) context).getLayoutInflater().inflate(R.layout.snackbar_layout, null);
        snackbar.getView().setBackgroundColor(Color.TRANSPARENT);
        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
        TextView tv_snack = custom.findViewById(R.id.tv_snack);
        if (saved) {
            tv_snack.setText(context.getResources().getString(R.string.apksaved));
        } else {
            tv_snack.setText(context.getResources().getString(R.string.unabletoext));
        }
        Button btn_open = custom.findViewById(R.id.btn_open);
        btn_open.setTextColor(color);
        btn_open.setText(context.getResources().getString(R.string.shareapk));
        btn_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("application/vnd.android.package-archive");
                intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));
                context.startActivity(Intent.createChooser(intent, "Share Now"));
            }
        });
        snackbarLayout.addView(custom, 0);
        snackbar.show();
    }

    public void showDialog(boolean visiblity) {
        if (visiblity) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            dialog = builder.create();
            dialog.setCancelable(false);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View customview = inflater.inflate(R.layout.dialog_progress, null, false);
            ProgressBar progressBar = customview.findViewById(R.id.progressBar);
            TextView tv_progress = customview.findViewById(R.id.tv_progress);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                progressBar.getIndeterminateDrawable().setColorFilter(new BlendModeColorFilter(color, BlendMode.SRC_ATOP));
            else
                progressBar.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
            tv_progress.setText(context.getResources().getString(R.string.extracting));
            tv_progress.setTextColor(color);
            dialog.setView(customview);
            dialog.show();
        } else {
            dialog.dismiss();
        }
    }
}
