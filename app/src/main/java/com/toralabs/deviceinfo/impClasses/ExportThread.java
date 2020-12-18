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
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.toralabs.deviceinfo.R;
import com.toralabs.deviceinfo.menuItems.Preferences;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ExportThread extends Thread {
    ExportDetails exportDetails;
    private final Context context;
    Preferences preferences;
    AlertDialog dialog;
    View v;
    int color;

    public ExportThread(Context context, View v, int color) {
        this.context = context;
        exportDetails = new ExportDetails(context);
        preferences = new Preferences(context);
        this.v = v;
        this.color = color;
    }

    @Override
    @SuppressLint("inflateParams")
    public void run() {
        super.run();
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showDialog(true);
            }
        });
        exportDetails.device();
        exportDetails.system();
        exportDetails.cpu();
        exportDetails.battery();
        exportDetails.display();
        exportDetails.memory();
        exportDetails.cameraApi21();
        exportDetails.thermal();
        exportDetails.sensor();
        exportDetails.inputDevices();
        exportDetails.codecs();
        exportDetails.glExtensions();
        exportDetails.deviceFeatures();
        exportDetails.drmDetails();

        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Device Info-Report.txt");
                final Uri uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
                BufferedWriter bufferedWriter = null;
                try {
                    bufferedWriter = new BufferedWriter(new FileWriter(file));
                    bufferedWriter.write(exportDetails.getBuilder());
                    showDialog(false);
                    Toast.makeText(context.getApplicationContext(), "Saved...", Toast.LENGTH_SHORT).show();
                    bufferedWriter.close();
                    Snackbar snackbar = Snackbar.make(v, "", BaseTransientBottomBar.LENGTH_LONG);
                    View custom = ((Activity) context).getLayoutInflater().inflate(R.layout.snackbar_layout, null);
                    snackbar.getView().setBackgroundColor(Color.TRANSPARENT);
                    Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
                    Button btn_open = custom.findViewById(R.id.btn_open);
                    btn_open.setTextColor(Color.parseColor(preferences.getCircleColor()));
                    btn_open.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setDataAndType(uri, "text/plain");
                            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            context.startActivity(i);
                        }
                    });
                    snackbarLayout.addView(custom, 0);
                    snackbar.show();
                } catch (IOException e) {
                    showDialog(false);
                    Toast.makeText(context.getApplicationContext(), "Unable to save...", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } finally {
                    if (bufferedWriter != null) {
                        try {
                            bufferedWriter.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
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
            tv_progress.setTextColor(color);
            tv_progress.setText(context.getResources().getString(R.string.exporting));
            dialog.setView(customview);
            dialog.show();
        } else {
            dialog.dismiss();
        }
    }
}
