package com.toralabs.deviceinfo.fragments;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.facebook.ads.NativeBannerAd;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.toralabs.deviceinfo.R;
import com.toralabs.deviceinfo.adapter.SimpleAdapter;
import com.toralabs.deviceinfo.impClasses.BuildInfo;
import com.toralabs.deviceinfo.impClasses.NativeBannerAdInflate;
import com.toralabs.deviceinfo.menuItems.Preferences;
import com.toralabs.deviceinfo.models.SimpleModel;

import java.util.ArrayList;

public class BatteryFragment extends Fragment implements NativeAdListener {
    NativeAdLayout nativeAdLayout;
    NativeBannerAd nativeBannerAd;
    RecyclerView recycler_battery;
    SimpleAdapter simpleAdapter;
    Preferences preferences;
    ArrayList<SimpleModel> list = new ArrayList<>();
    BuildInfo buildInfo;
    int color;
    boolean darkmode;
    TextView txt_level, txt_status, txt_current, text_ct;
    BroadcastReceiver broadcastReceiver;
    IntentFilter intentFilter;
    FloatingActionButton fab_battery;
    ProgressBar progressBar;
    CardView cardBattery;
    NestedScrollView nestedScroll;
    HandlerThread thread = new HandlerThread("BatteryThread");
    String health, powerSource, status, level, voltage, tech, temp, capacity;
    Handler handler, currentHandler;
    boolean bool;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getContext() != null) {
            preferences = new Preferences(getContext());
            bool = preferences.getPurchasePref();
        }
        buildInfo = new BuildInfo(getContext());
        darkmode = preferences.getMode();
        color = Color.parseColor(preferences.getCircleColor());
        currentHandler = new Handler();
        broadCast();
        if (savedInstanceState != null) {
            list = savedInstanceState.getParcelableArrayList("batteryList");
        } else {
            thread.start();
            handler = new Handler(thread.getLooper());
            batteryDetails();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_battery, container, false);
        setHasOptionsMenu(true);
        recycler_battery = view.findViewById(R.id.recycler_battery);
        txt_level = view.findViewById(R.id.txt_level);
        txt_status = view.findViewById(R.id.txt_status);
        txt_current = view.findViewById(R.id.txt_current);
        text_ct = view.findViewById(R.id.text_ct);
        fab_battery = view.findViewById(R.id.fab_battery);
        nestedScroll = view.findViewById(R.id.nestedScroll);
        progressBar = view.findViewById(R.id.progressBar);
        cardBattery = view.findViewById(R.id.cardBattery);
        nativeAdLayout = view.findViewById(R.id.nativeBannerAd);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            progressBar.getIndeterminateDrawable().setColorFilter(new BlendModeColorFilter(color, BlendMode.SRC_ATOP));
        else
            progressBar.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        fab_battery.setBackgroundTintList(ColorStateList.valueOf(color));
        fab_battery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(Intent.ACTION_POWER_USAGE_SUMMARY));
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        });
        if (nestedScroll != null) {
            nestedScroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (v.getChildAt(0).getBottom() <= (nestedScroll.getHeight() + scrollY)) {
                        fab_battery.hide();
                    } else {
                        fab_battery.show();
                    }
                }
            });
        }
        if (savedInstanceState != null)
            updateUI();
        if (!bool && getContext() != null) {
            nativeBannerAd = new NativeBannerAd(getContext(), getResources().getString(R.string.nativead2));
            nativeBannerAd.loadAd(nativeBannerAd.buildLoadAdConfig().withAdListener(this).build());
        }
        return view;
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        MenuItem item = menu.findItem(R.id.search);
        item.setVisible(false);
    }

    public void batteryDetails() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                health = buildInfo.getBatteryHealth();
                status = buildInfo.getBatteryStatus();
                level = buildInfo.getBatteryLevel();
                voltage = buildInfo.getVoltage();
                powerSource = buildInfo.getPowerSource();
                tech = buildInfo.getBatteryTechnology();
                temp = buildInfo.getBatteryTemp();
                capacity = buildInfo.getBatteryCapacity();

                list.add(new SimpleModel(getResources().getString(R.string.health), health));
                list.add(new SimpleModel(getResources().getString(R.string.status), status));
                list.add(new SimpleModel(getResources().getString(R.string.level), level));
                list.add(new SimpleModel(getResources().getString(R.string.voltage), voltage));
                list.add(new SimpleModel(getResources().getString(R.string.power_source), powerSource));
                list.add(new SimpleModel(getResources().getString(R.string.tech), tech));
                list.add(new SimpleModel(getResources().getString(R.string.temp), temp));
                list.add(new SimpleModel(getResources().getString(R.string.capacity), capacity));
            }
        });
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (getActivity() == null)
                    return;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateUI();
                    }
                });
            }
        });
    }

    public void broadCast() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                BroadCast broadCast = new BroadCast();
                broadCast.start();
            }
        };
        intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        if (getActivity() == null)
            return;
        getActivity().registerReceiver(broadcastReceiver, intentFilter);
    }

    @SuppressLint("SetTextI18n")
    public void updateUI() {
        txt_level.setTextColor(color);
        txt_status.setTextColor(color);
        txt_current.setTextColor(color);
        text_ct.setTextColor(color);
        text_ct.setVisibility(View.VISIBLE);
        cardBattery.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        text_ct.setText(getResources().getString(R.string.current));
        txt_current.setText(" : " + buildInfo.getCurrentLevel());
        currentFlow();
        txt_status.setText(list.get(1).getDesc());
        txt_level.setText(list.get(2).getDesc().trim());
        simpleAdapter = new SimpleAdapter(list, getContext(), color);
        recycler_battery.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler_battery.setNestedScrollingEnabled(false);
        recycler_battery.setAdapter(simpleAdapter);
    }

    class BroadCast extends Thread {
        String batterylevel = "", voltage = "", status = "", powersource = "";

        @Override
        public void run() {
            super.run();
            batterylevel = buildInfo.getBatteryLevel();
            status = buildInfo.getBatteryStatus();
            voltage = buildInfo.getVoltage();
            powersource = buildInfo.getPowerSource();
            if (getActivity() == null)
                return;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (batterylevel != null) {
                        txt_level.setText(batterylevel);
                        simpleAdapter.update(2, batterylevel);
                    }
                    if (status != null) {
                        txt_status.setText(status);
                        simpleAdapter.update(1, status);
                    }
                    if (voltage != null) simpleAdapter.update(3, voltage);
                    if (powersource != null) simpleAdapter.update(4, powersource);
                }
            });
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() == null)
            return;
        getActivity().unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() == null)
            return;
        getActivity().registerReceiver(broadcastReceiver, intentFilter);
    }

    public void currentFlow() {
        currentHandler.postDelayed(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                txt_current.setText(" : " + buildInfo.getCurrentLevel());
                currentFlow();
            }
        }, 1000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (thread != null)
            thread.quit();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("batteryList", list);
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
        } else if (isAdded()) {
            NativeBannerAdInflate nativeBannerAdInflate = new NativeBannerAdInflate(getContext(), color);
            nativeBannerAdInflate.inflateAd(nativeBannerAd, nativeAdLayout);
            nativeAdLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAdClicked(Ad ad) {
    }

    @Override
    public void onLoggingImpression(Ad ad) {
    }
}
