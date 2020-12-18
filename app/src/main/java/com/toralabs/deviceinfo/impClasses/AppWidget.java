package com.toralabs.deviceinfo.impClasses;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.toralabs.deviceinfo.R;
import com.toralabs.deviceinfo.activities.MainActivity;
import com.toralabs.deviceinfo.activities.SplashActivity;

public class AppWidget extends AppWidgetProvider {
    int pct, storagePct;
    long totalRAM, freeRam, usedRAM, totalStorage, usedStorage;
    BuildInfo buildInfo;

    @SuppressLint("DefaultLocale")
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        manager.getMemoryInfo(memoryInfo);
        buildInfo = new BuildInfo(context);
        for (int appWidget : appWidgetIds) {
            Intent intent = new Intent(context, SplashActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, 0);
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_widget);
            remoteViews.setOnClickPendingIntent(R.id.relRam, pendingIntent);
            totalRAM = memoryInfo.totalMem / (1024 * 1024);
            freeRam = memoryInfo.availMem / (1024 * 1024);
            usedRAM = totalRAM - freeRam;
            pct = (int) (usedRAM * 100 / totalRAM);
            totalStorage = buildInfo.getTotalStorageInfo("/data");
            usedStorage = buildInfo.getUsedStorageInfo("/data");
            storagePct = (int) ((usedStorage * 100) / totalStorage);
            remoteViews.setTextViewText(R.id.txtIntValue, formattedValue(usedStorage) + "/" + formattedValue(totalStorage));
            remoteViews.setTextViewText(R.id.txtStoragePct, storagePct + "%");
            remoteViews.setTextViewText(R.id.txtPct, pct + "%\nRAM");
            remoteViews.setProgressBar(R.id.progressBar, 100, pct, false);
            remoteViews.setProgressBar(R.id.progressInt, 100, storagePct, false);
            remoteViews.setTextViewText(R.id.txtRam, freeRam + " MB RAM available of " + totalRAM + " MB");
            remoteViews.setTextViewText(R.id.txtCpuUsage, "Cpu Usage: " + buildInfo.getUsage());
            appWidgetManager.updateAppWidget(appWidget, remoteViews);
        }
    }

    @SuppressLint("DefaultLocale")
    public String formattedValue(long l) {
        float f;
        String s;
        if (l > 1024 * 1024 * 1024) {
            f = (float) l / (1024 * 1024 * 1024);
            s = String.format("%.1f", f) + " GB";
        } else if (l > 1024 * 1024 && l <= 1024 * 1024 * 1024.0) {
            f = (float) l / (1024 * 1024);
            s = String.format("%.1f", f) + " MB";
        } else if (l > 1024 && l <= 1024 * 1024.0) {
            f = (float) l / (1024);
            s = String.format("%.1f", f) + " KB";
        } else {
            f = (float) l;
            s = String.format("%.2f", f) + " Bytes";
        }
        return s;
    }
}
