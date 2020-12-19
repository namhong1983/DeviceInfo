package com.toralabs.deviceinfo.fragments;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.facebook.ads.NativeBannerAd;
import com.github.lzyzsd.circleprogress.ArcProgress;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.toralabs.deviceinfo.R;
import com.toralabs.deviceinfo.adapter.CpuFreqAdapter;
import com.toralabs.deviceinfo.impClasses.BuildInfo;
import com.toralabs.deviceinfo.activities.MainActivity;
import com.toralabs.deviceinfo.impClasses.NativeBannerAdInflate;
import com.toralabs.deviceinfo.menuItems.Preferences;
import com.toralabs.deviceinfo.models.CpuFreqModel;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment implements Handler.Callback, NativeAdListener {
    NativeAdLayout nativeAdLayout;
    NativeBannerAd nativeBannerAd;
    BuildInfo buildInfo;
    Preferences preferences;
    RecyclerView recyclerCpu;
    ActivityManager.MemoryInfo memoryInfo;
    ActivityManager manager;
    TextView txtRamTotal, txtRamUsed, txtFreeRam, txtSys_percent, txt_sensorcount, txt_appcount, txtSysStorageInfo, txtInt_percent, txtIntStorageInfo, txtExtStorageInfo, txtExt_percent;
    TextView txtBattery_percent, txtBatteryInfo, txt_charging;
    ArcProgress arcProgress;
    ProgressBar progress_sys, progress_int, progress_ext, progress_battery;
    LineChart lineChart;
    CardView cardRamStatus, cardExternal, cardInternal, cardSystem, cardBattery, cardSensors, cardApps, cardAd;
    ImageView img_sys, img_int, img_battery, img_sensor, img_apps, img_ext;
    ScrollView scrollview;
    long totalRAM, usedRAM, freeRam, totalSys, usedSys, totalInternal, usedInternal, totalExternal, usedExternal;
    int color, coresCount, sensorCount, appCount, count = 0;
    String voltage, temp, level, status;
    BroadcastReceiver broadcastReceiver;
    IntentFilter intentFilter;
    Handler broadcastHandler, cpuCoreHandler, ramHandler;
    List<Entry> values = new ArrayList<>();
    LineData lineData;
    LineDataSet lineDataSet;
    CpuFreqAdapter cpuFreqAdapter;
    ArrayList<CpuFreqModel> cpuCoresList = new ArrayList<>();
    String[] str;
    PackageManager packageManager;
    MainActivity mainActivity;
    HandlerThread broadcastThread = new HandlerThread("broadCastThread");
    HandlerThread cpuCoreThread = new HandlerThread("cpuCoreThread");
    HandlerThread ramThread = new HandlerThread("ramThread");
    boolean isExtAvail, bool;
    Handler mainHandler;
    ValueAnimator sensorAnim, appAnim;
    Bundle bundle;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getContext() != null) {
            preferences = new Preferences(getContext());
            bool = preferences.getPurchasePref();
        }
        color = Color.parseColor(preferences.getCircleColor());
        buildInfo = new BuildInfo(getContext());
        if (getContext() != null) {
            manager = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
            packageManager = getContext().getPackageManager();
        }
        cpuCoreThread.start();
        ramThread.start();
        broadcastThread.start();
        broadcastHandler = new Handler(broadcastThread.getLooper());
        cpuCoreHandler = new Handler(cpuCoreThread.getLooper());
        ramHandler = new Handler(ramThread.getLooper());
        coresCount = mainActivity.coresCount;
        str = new String[coresCount];
        mainHandler = new Handler(this);
        bundle = new Bundle();
        if (savedInstanceState != null) {
            cpuCoresList = savedInstanceState.getParcelableArrayList("cpuCoresList");
            str = savedInstanceState.getStringArray("strArray");
            sensorCount = savedInstanceState.getInt("sensorCount");
            coresCount = savedInstanceState.getInt("coresCount");
            appCount = savedInstanceState.getInt("appCount");
            isExtAvail = savedInstanceState.getBoolean("isExtAvail");
            level = savedInstanceState.getString("level");
            voltage = savedInstanceState.getString("voltage");
            temp = savedInstanceState.getString("temp");
            status = savedInstanceState.getString("status");
            usedRAM = savedInstanceState.getLong("usedRAM");
            totalRAM = savedInstanceState.getLong("totalRAM");
            freeRam = savedInstanceState.getLong("freeRam");
            usedSys = savedInstanceState.getLong("usedSys");
            totalSys = savedInstanceState.getLong("totalSys");
            usedInternal = savedInstanceState.getLong("usedInternal");
            totalInternal = savedInstanceState.getLong("totalInternal");
            if (isExtAvail) {
                usedExternal = savedInstanceState.getLong("usedExternal");
                totalExternal = savedInstanceState.getLong("totalExternal");
            }
        } else {
            cpuCoresList = mainActivity.cpuCoresList;
            totalRAM = mainActivity.totalRAM;
            freeRam = mainActivity.freeRam;
            usedRAM = mainActivity.usedRAM;
            totalSys = mainActivity.totalSys;
            usedSys = mainActivity.usedSys;
            totalInternal = mainActivity.totalInternal;
            usedInternal = mainActivity.usedInternal;
            isExtAvail = mainActivity.isExtAvail;
            if (isExtAvail) {
                totalExternal = mainActivity.totalExternal;
                usedExternal = mainActivity.usedExternal;
            }
            level = mainActivity.level;
            voltage = mainActivity.voltage;
            temp = mainActivity.temp;
            status = mainActivity.status;
            appCount = mainActivity.appCount;
            sensorCount = mainActivity.sensorCount;
        }
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                batteryBroadcast();
            }
        };
        sensorAnim = ValueAnimator.ofInt(0, sensorCount);
        sensorAnim.setDuration(1000);
        appAnim = ValueAnimator.ofInt(0, appCount);
        appAnim.setDuration(1000);
        intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        if (getActivity() == null)
            return;
        getActivity().registerReceiver(broadcastReceiver, intentFilter);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_dashboard, container, false);
        setHasOptionsMenu(true);
        findViewByIds(view);
        uiMoreInfo();
        recyclerCpu.scheduleLayoutAnimation();
        if (totalRAM != 0)
            ObjectAnimator.ofInt(arcProgress, "progress", (int) (usedRAM * 100 / totalRAM)).setDuration(800).start();
        if (totalSys != 0)
            ObjectAnimator.ofInt(progress_sys, "progress", (int) (usedSys * 100 / totalSys)).setDuration(800).start();
        if (totalInternal != 0)
            ObjectAnimator.ofInt(progress_int, "progress", (int) (usedInternal * 100 / totalInternal)).setDuration(800).start();
        ObjectAnimator.ofInt(progress_battery, "progress", Integer.parseInt(level.substring(0, level.indexOf("%")))).setDuration(800).start();
        if (isExtAvail && totalExternal != 0)
            ObjectAnimator.ofInt(progress_ext, "progress", (int) (usedExternal * 100 / totalExternal)).setDuration(800).start();
        sensorAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                txt_sensorcount.setText(animation.getAnimatedValue().toString());
            }
        });
        appAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                txt_appcount.setText(animation.getAnimatedValue().toString());
            }
        });
        sensorAnim.start();
        appAnim.start();
        updateUICpu();
        updateUIStorage();
        if (!bool && getContext() != null) {
            nativeBannerAd = new NativeBannerAd(getContext(), getResources().getString(R.string.nativead2));
            nativeBannerAd.loadAd(nativeBannerAd.buildLoadAdConfig().withAdListener(this).build());
        }
        return view;
    }

    @SuppressLint("SetTextI18n")
    public void updateUIStorage() {
        String f = getResources().getString(R.string.free) + ": ";
        String t = getResources().getString(R.string.total) + ": ";
        String v = getResources().getString(R.string.voltage) + ": ";
        String tp = getResources().getString(R.string.temp) + ": ";

        txtRamUsed.setText(String.valueOf((int) usedRAM));
        txtRamTotal.setText("RAM - " + (int) totalRAM);
        txtFreeRam.setText(String.valueOf((int) freeRam));
        arcProgress.setProgress((int) (usedRAM * 100 / totalRAM));
        lineChart.setData(lineData);

        progress_sys.setProgress((int) (usedSys * 100 / totalSys));
        txtSys_percent.setText((int) (usedSys * 100 / totalSys) + "%");
        txtSysStorageInfo.setText(f + formattedValue(totalSys - usedSys) + ", " + t + formattedValue(totalSys));

        progress_int.setProgress((int) (usedInternal * 100 / totalInternal));
        txtInt_percent.setText((int) (usedInternal * 100 / totalInternal) + "%");
        txtIntStorageInfo.setText(f + formattedValue(totalInternal - usedInternal) + ", " + t + formattedValue(totalInternal));

        if (getContext() == null)
            return;
        if (isExtAvail) {
            cardExternal.setVisibility(View.VISIBLE);
            progress_ext.setProgress((int) (usedExternal * 100 / totalExternal));
            txtExt_percent.setText((int) (usedExternal * 100 / totalExternal) + "%");
            txtExtStorageInfo.setText(f + formattedValue(totalExternal - usedExternal) + ", " + t + formattedValue(totalExternal));
        }
        txtBattery_percent.setText(level);
        if (status.equalsIgnoreCase("charging")) {
            txt_charging.setVisibility(View.VISIBLE);
            progress_battery.setIndeterminate(true);
        } else {
            progress_battery.setIndeterminate(false);
            txt_charging.setVisibility(View.GONE);
            progress_battery.setProgress(Integer.parseInt(level.substring(0, level.indexOf("%"))));
        }
        txtBatteryInfo.setText(v + voltage + ", " + tp + temp);
        txt_sensorcount.setText(sensorCount + "");
        txt_appcount.setText(appCount + "");
        values.add(new Entry(count, (int) usedRAM));
        lineDataSet = new LineDataSet(values, "DataSet 1");
        count++;
        lineDataSet.setCircleHoleRadius(0f);
        lineDataSet.setCircleRadius(0f);
        lineDataSet.setLineWidth(1f);
        lineDataSet.setColor(Color.WHITE);
        lineDataSet.setCircleColor(color);
        lineDataSet.setDrawCircles(false);
        lineDataSet.setDrawValues(false);
        lineDataSet.setDrawValues(false);
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        lineDataSet.setCubicIntensity(0.3f);
        lineData = new LineData(lineDataSet);
        getRAM();
    }

    @SuppressLint("SetTextI18n")
    public void updateUICpu() {
        cpuFreqAdapter = new CpuFreqAdapter(cpuCoresList, getContext(), color);
        if (coresCount > 3) {
            recyclerCpu.setLayoutManager(new GridLayoutManager(getContext(), 4));
        } else if (coresCount > 0 && coresCount <= 3) {
            recyclerCpu.setLayoutManager(new GridLayoutManager(getContext(), 2));
        }
        recyclerCpu.setNestedScrollingEnabled(false);
        recyclerCpu.setAdapter(cpuFreqAdapter);
        getRunning();
    }

    public void getRunning() {
        cpuCoreHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < coresCount; i++) {
                    str[i] = buildInfo.getRunningCpu(i, false);
                }
                bundle.putInt("update", 1);
                Message message = new Message();
                message.setData(bundle);
                mainHandler.sendMessage(message);
                getRunning();
            }
        }, 1200);
    }

    public void getRAM() {
        ramHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                memoryInfo = new ActivityManager.MemoryInfo();
                manager.getMemoryInfo(memoryInfo);
                freeRam = (long) (memoryInfo.availMem / (1024 * 1024.0));
                usedRAM = totalRAM - freeRam;
                lineDataSet.addEntry(new Entry(count, (int) usedRAM));
                lineDataSet.setLineWidth(2);
                if (lineDataSet.getEntryCount() > 15) {
                    lineDataSet.removeFirst();
                }
                lineData.notifyDataChanged();
                lineChart.setData(lineData);
                lineChart.notifyDataSetChanged();
                lineChart.postInvalidate();
                lineChart.moveViewToX(lineData.getEntryCount());
                count++;
                bundle.putInt("update", 2);
                Message message = new Message();
                message.setData(bundle);
                mainHandler.sendMessage(message);
                getRAM();
            }
        }, 1200);
    }

    public int selectedTabColor(int col) {
        int alpha = Color.alpha(col);
        int r = Math.round(Color.red(col) * 0.85f);
        int g = Math.round(Color.green(col) * 0.85f);
        int b = Math.round(Color.blue(col) * 0.85f);
        return Color.argb(alpha, Math.min(255, r), Math.min(255, g), Math.min(255, b));
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("cpuCoresList", cpuCoresList);
        outState.putStringArray("strArray", str);
        outState.putInt("sensorCount", sensorCount);
        outState.putInt("coresCount", coresCount);
        outState.putInt("appCount", appCount);
        outState.putString("level", level);
        outState.putString("voltage", voltage);
        outState.putString("temp", temp);
        outState.putString("status", status);
        outState.putLong("usedRAM", usedRAM);
        outState.putLong("totalRAM", totalRAM);
        outState.putLong("freeRam", freeRam);
        outState.putBoolean("isExtAvail", isExtAvail);
        outState.putLong("usedSys", usedSys);
        outState.putLong("totalSys", totalSys);
        outState.putLong("usedInternal", usedInternal);
        outState.putLong("totalInternal", totalInternal);
        if (isExtAvail) {
            outState.putLong("usedExternal", usedExternal);
            outState.putLong("totalExternal", totalExternal);
        }
    }

    public void batteryBroadcast() {
        broadcastHandler.post(new Runnable() {
            @Override
            public void run() {
                level = buildInfo.getBatteryLevel();
                voltage = buildInfo.getVoltage();
                temp = buildInfo.getBatteryTemp();
                status = buildInfo.getBatteryStatus();
                bundle.putInt("update", 3);
                Message message = new Message();
                message.setData(bundle);
                mainHandler.sendMessage(message);
            }
        });
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        MenuItem item = menu.findItem(R.id.search);
        item.setVisible(false);
    }

    public void findViewByIds(View view) {
        scrollview = view.findViewById(R.id.scrollview);
        txt_charging = view.findViewById(R.id.txt_charging);
        txt_appcount = view.findViewById(R.id.txt_appcount);
        txtRamTotal = view.findViewById(R.id.txtRamTotal);
        txtRamUsed = view.findViewById(R.id.txtRamUsed);
        txtFreeRam = view.findViewById(R.id.txtFreeRam);
        txtSys_percent = view.findViewById(R.id.txtSys_percent);
        txt_sensorcount = view.findViewById(R.id.txt_sensorcount);
        txtSysStorageInfo = view.findViewById(R.id.txtSysStorageInfo);
        txtInt_percent = view.findViewById(R.id.txtInt_percent);
        txtIntStorageInfo = view.findViewById(R.id.txtIntStorageInfo);
        txtBattery_percent = view.findViewById(R.id.txtBattery_percent);
        txtBatteryInfo = view.findViewById(R.id.txtBatteryInfo);
        progress_battery = view.findViewById(R.id.progress_battery);
        progress_ext = view.findViewById(R.id.progress_ext);
        progress_int = view.findViewById(R.id.progress_int);
        progress_sys = view.findViewById(R.id.progress_sys);
        lineChart = view.findViewById(R.id.lineChart);
        arcProgress = view.findViewById(R.id.arcProgress);
        img_sys = view.findViewById(R.id.img_sys);
        img_ext = view.findViewById(R.id.img_ext);
        img_int = view.findViewById(R.id.img_int);
        img_battery = view.findViewById(R.id.img_battery);
        img_sensor = view.findViewById(R.id.img_sensor);
        img_apps = view.findViewById(R.id.img_apps);
        cardRamStatus = view.findViewById(R.id.cardRamStatus);
        cardApps = view.findViewById(R.id.cardApps);
        cardBattery = view.findViewById(R.id.cardBattery);
        cardExternal = view.findViewById(R.id.cardExternal);
        cardInternal = view.findViewById(R.id.cardInternal);
        cardSystem = view.findViewById(R.id.cardSystem);
        cardSensors = view.findViewById(R.id.cardSensors);
        cardAd = view.findViewById(R.id.cardAd);
        recyclerCpu = view.findViewById(R.id.recyclerCpu);
        txtExt_percent = view.findViewById(R.id.txtExternal_percent);
        txtExtStorageInfo = view.findViewById(R.id.txtExternalInfo);
        nativeAdLayout = view.findViewById(R.id.nativeBannerAd);
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

    public void uiMoreInfo() {
        img_apps.setColorFilter(color);
        img_sensor.setColorFilter(color);
        img_sys.setColorFilter(color);
        img_int.setColorFilter(color);
        img_battery.setColorFilter(color);
        img_ext.setColorFilter(color);
        arcProgress.setSuffixText("%");
        arcProgress.animate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (getContext() != null) {
                progress_int.setProgressTintList(ColorStateList.valueOf(color));
                progress_ext.setProgressTintList(ColorStateList.valueOf(color));
                progress_sys.setProgressTintList(ColorStateList.valueOf(color));
                progress_battery.setProgressTintList(ColorStateList.valueOf(color));
                progress_battery.setIndeterminateTintList(ColorStateList.valueOf(color));
            }
        }
        cardRamStatus.setCardBackgroundColor(color);
        arcProgress.setUnfinishedStrokeColor(selectedTabColor(color));
        arcProgress.setTextColor(getResources().getColor(R.color.white));
        txtRamTotal.setTextColor(getResources().getColor(R.color.white));
        txtFreeRam.setTextColor(getResources().getColor(R.color.white));
        txtRamUsed.setTextColor(getResources().getColor(R.color.white));
        lineChart.setBackgroundColor(color);
        lineChart.setDragEnabled(false);
        lineChart.setLogEnabled(false);
        lineChart.setScaleEnabled(false);
        lineChart.setBorderColor(color);
        lineChart.setTouchEnabled(false);
        lineChart.setNoDataText("");
        lineChart.getXAxis().setDrawAxisLine(false);
        lineChart.getAxisLeft().setDrawAxisLine(false);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisRight().setDrawGridLines(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.getXAxis().setDrawLabels(false);
        lineChart.getAxisLeft().setDrawLabels(false);
        lineChart.getAxisRight().setDrawLabels(true);
        lineChart.getAxisRight().setInverted(false);
        lineChart.getAxisRight().setAxisLineColor(Color.WHITE);
        lineChart.getAxisRight().setLabelCount(4);
        lineChart.getAxisRight().setTextColor(Color.WHITE);
        lineChart.getLegend().setEnabled(false);
        lineChart.setVisibleXRangeMaximum(15);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() != null) {
            arcProgress.clearAnimation();
            getActivity().unregisterReceiver(broadcastReceiver);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() == null)
            return;
        try {
            getActivity().registerReceiver(broadcastReceiver, intentFilter);
        } catch (Exception e) {
            // Exception
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (broadcastThread != null)
            broadcastThread.quit();
        if (cpuCoreThread != null)
            cpuCoreThread.quit();
        if (ramThread != null)
            ramThread.quit();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public boolean handleMessage(@NonNull Message msg) {
        switch (msg.getData().getInt("update")) {
            case 1:
                for (int i = 0; i < coresCount; i++) {
                    cpuFreqAdapter.notifyItemChanged(i, str[i]);
                }
                break;
            case 2:
                lineChart.animate();
                txtRamUsed.setText(String.valueOf((int) usedRAM));
                txtFreeRam.setText(String.valueOf((int) freeRam));
                arcProgress.setProgress((int) (usedRAM * 100 / totalRAM));
                break;
            case 3:
                txtBattery_percent.setText(level);
                if (status.equalsIgnoreCase("charging")) {
                    txt_charging.setVisibility(View.VISIBLE);
                    progress_battery.setIndeterminate(true);
                } else {
                    txt_charging.setVisibility(View.GONE);
                    progress_battery.setIndeterminate(false);
                    ObjectAnimator.ofInt(progress_battery, "progress", Integer.parseInt(level.substring(0, level.indexOf("%")))).setDuration(700).start();
                    progress_battery.setProgress(Integer.parseInt(level.substring(0, level.indexOf("%"))));
                }
                txtBatteryInfo.setText("Voltage: " + voltage + ", Temperature: " + temp);
                break;
        }
        return true;
    }

    @Override
    public void onMediaDownloaded(Ad ad) {
    }

    @Override
    public void onError(Ad ad, AdError adError) {
    }

    @Override
    public void onAdLoaded(Ad ad) {
        if (nativeBannerAd == null || nativeBannerAd != ad) {
        } else if (isAdded() && getContext() != null) {
            NativeBannerAdInflate nativeBannerAdInflate = new NativeBannerAdInflate(getContext(), color);
            nativeBannerAdInflate.inflateAd(nativeBannerAd, nativeAdLayout);
            cardAd.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAdClicked(Ad ad) {
    }

    @Override
    public void onLoggingImpression(Ad ad) {
    }

}
