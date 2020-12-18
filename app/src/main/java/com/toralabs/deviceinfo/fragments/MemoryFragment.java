package com.toralabs.deviceinfo.fragments;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.facebook.ads.NativeBannerAd;
import com.toralabs.deviceinfo.R;
import com.toralabs.deviceinfo.activities.MainActivity;
import com.toralabs.deviceinfo.impClasses.BuildInfo;
import com.toralabs.deviceinfo.impClasses.NativeBannerAdInflate;
import com.toralabs.deviceinfo.menuItems.Preferences;

import java.io.File;
import java.util.Map;

public class MemoryFragment extends Fragment implements Handler.Callback, NativeAdListener, View.OnClickListener {
    NativeAdLayout nativeAdLayout;
    NativeBannerAd nativeBannerAd;
    CardView card_ram, card_rom, card_system, card_sd, card_zram, card_ad, card_raminfo;
    TextView txt_usedram, txt_usedsys, txt_usedrom, txt_usedsd, txt_sdpath, txt_rompath, txt_syspath, txt_usedzram, txtRamType, txtBandwidth, txtChannels,txtRamInfo;
    ProgressBar progress_ram, progress_sys, progress_rom, progress_ext, progress_zram;
    long totalRAM, usedRAM, totalSys, usedSys, totalInternal, usedInternal, totalExternal, usedExternal, totalZRAM, usedZRAM;
    ActivityManager manager;
    LinearLayout linear1, linear2, linear3;
    BuildInfo buildInfo;
    ActivityManager.MemoryInfo memoryInfo;
    Handler refreshRAM, handler;
    RelativeLayout rel_ram, rel_rom;
    String system, data, path;
    ProgressBar progressBar;
    Preferences preferences;
    ScrollView scrollView;
    int color;
    Bundle bundle;
    Handler mainHandler;
    HandlerThread refreshThread = new HandlerThread("RefreshRAM");
    HandlerThread thread = new HandlerThread("MemoryThread");
    String txtUsedRam, txtUsedSys, txtUsedRom, txtUsedSd, txtUsedZRAM;
    int progressRam, progressRom, progressSys, progressExt, progressZRAM;
    boolean isExtAvail, isZramAvail, bool;
    Map<String, String> map;
    String memory, bandwidth, channels;
    MainActivity mainActivity;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getContext() != null)
            preferences = new Preferences(getContext());
        bool = preferences.getPurchasePref();
        buildInfo = new BuildInfo(getContext());
        color = Color.parseColor(preferences.getCircleColor());
        if (getContext() != null) {
            manager = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
        }
        refreshThread.start();
        refreshRAM = new Handler(refreshThread.getLooper());
        bundle = new Bundle();
        mainHandler = new Handler(this);
        if (savedInstanceState != null) {
            memory = savedInstanceState.getString("memory");
            progressRam = savedInstanceState.getInt("progressRam");
            progressRom = savedInstanceState.getInt("progressRom");
            progressSys = savedInstanceState.getInt("progressSys");
            totalRAM = savedInstanceState.getLong("totalRAM");
            usedRAM = savedInstanceState.getLong("usedRAM");
            totalSys = savedInstanceState.getLong("totalSys");
            usedSys = savedInstanceState.getLong("usedSys");
            totalInternal = savedInstanceState.getLong("totalInternal");
            usedInternal = savedInstanceState.getLong("usedInternal");
            txtUsedRam = savedInstanceState.getString("txtUsedRam");
            txtUsedSys = savedInstanceState.getString("txtUsedSys");
            system = savedInstanceState.getString("system");
            data = savedInstanceState.getString("data");
            memory = savedInstanceState.getString("memory");
            bandwidth = savedInstanceState.getString("bandwidth");
            channels = savedInstanceState.getString("channels");
            isExtAvail = savedInstanceState.getBoolean("extAvail");
            isZramAvail = savedInstanceState.getBoolean("isZramAvail");
            if (isZramAvail) {
                totalZRAM = savedInstanceState.getLong("totalZRAM");
                usedZRAM = savedInstanceState.getLong("usedZRAM");
            }
            if (isExtAvail) {
                progressExt = savedInstanceState.getInt("progressExt");
                totalExternal = savedInstanceState.getLong("totalExternal");
                usedExternal = savedInstanceState.getLong("usedExternal");
                txtUsedSd = savedInstanceState.getString("txtUsedSd");
                path = savedInstanceState.getString("path");
            }
        } else {
            thread.start();
            memory = mainActivity.memory;
            bandwidth = mainActivity.bandwidth;
            channels = mainActivity.channel;
            handler = new Handler(thread.getLooper());
            memoryDetails();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_memory, container, false);
        setHasOptionsMenu(true);
        progressBar = view.findViewById(R.id.progressBar);
        scrollView = view.findViewById(R.id.scrollView);
        card_ram = view.findViewById(R.id.card_ram);
        card_rom = view.findViewById(R.id.card_rom);
        card_system = view.findViewById(R.id.card_system);
        card_sd = view.findViewById(R.id.card_sd);
        card_ad = view.findViewById(R.id.card_ad);
        card_ram = view.findViewById(R.id.card_ram);
        card_raminfo = view.findViewById(R.id.card_raminfo);
        card_zram = view.findViewById(R.id.card_zram);
        txt_usedram = view.findViewById(R.id.txt_usedram);
        txt_usedsd = view.findViewById(R.id.txt_usedsd);
        txt_usedsys = view.findViewById(R.id.txt_usedsys);
        txt_usedrom = view.findViewById(R.id.txt_usedrom);
        txt_usedzram = view.findViewById(R.id.txt_usedzram);
        txtRamType = view.findViewById(R.id.txtRamType);
        txtBandwidth = view.findViewById(R.id.txtBandwidth);
        txtChannels = view.findViewById(R.id.txtChannels);
        progress_ext = view.findViewById(R.id.progress_ext);
        progress_ram = view.findViewById(R.id.progress_ram);
        progress_sys = view.findViewById(R.id.progress_sys);
        progress_rom = view.findViewById(R.id.progress_rom);
        progress_zram = view.findViewById(R.id.progress_zram);
        txt_sdpath = view.findViewById(R.id.txt_sdpath);
        txt_rompath = view.findViewById(R.id.txt_rompath);
        txt_syspath = view.findViewById(R.id.txt_syspath);
        txtRamInfo=view.findViewById(R.id.txtRamInfo);
        rel_ram = view.findViewById(R.id.rel_ram);
        rel_rom = view.findViewById(R.id.rel_rom);
        progressBar = view.findViewById(R.id.progressBar);
        linear1 = view.findViewById(R.id.linear1);
        linear2 = view.findViewById(R.id.linear2);
        linear3 = view.findViewById(R.id.linear3);
        nativeAdLayout = view.findViewById(R.id.nativeBannerAd);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            progressBar.getIndeterminateDrawable().setColorFilter(new BlendModeColorFilter(color, BlendMode.SRC_ATOP));
        else
            progressBar.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        rel_ram.setOnClickListener(this);
        rel_rom.setOnClickListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (getContext() != null) {
                progress_rom.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.romcolor)));
                progress_sys.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.syscolor)));
                progress_ram.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.ramcolor)));
                progress_ext.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.sdcardcolor)));
                progress_zram.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.zram)));
            }
        } else {
            if (getContext() != null) {
                progress_ram.getProgressDrawable().setColorFilter(ContextCompat.getColor(getContext(), R.color.ramcolor), PorterDuff.Mode.SRC_IN);
                progress_rom.getProgressDrawable().setColorFilter(ContextCompat.getColor(getContext(), R.color.romcolor), PorterDuff.Mode.SRC_IN);
                progress_sys.getProgressDrawable().setColorFilter(ContextCompat.getColor(getContext(), R.color.syscolor), PorterDuff.Mode.SRC_IN);
                progress_ext.getProgressDrawable().setColorFilter(ContextCompat.getColor(getContext(), R.color.sdcardcolor), PorterDuff.Mode.SRC_IN);
                progress_zram.getProgressDrawable().setColorFilter(ContextCompat.getColor(getContext(), R.color.zram), PorterDuff.Mode.SRC_IN);
            }
        }
        if (savedInstanceState != null)
            updateUI();
        if (!bool) {
            nativeBannerAd = new NativeBannerAd(getContext(), getResources().getString(R.string.nativead1));
            nativeBannerAd.loadAd(nativeBannerAd.buildLoadAdConfig().withAdListener(this).build());
        }
        return view;
    }

    public void memoryDetails() {
        handler.post(new Runnable() {
            @SuppressLint({"DefaultLocale", "SetTextI18n"})
            @Override
            public void run() {
                map = buildInfo.getZRamInfo();
                system = Environment.getRootDirectory().getPath();
                data = Environment.getDataDirectory().getPath();
                memoryInfo = new ActivityManager.MemoryInfo();
                manager.getMemoryInfo(memoryInfo);
                totalRAM = (memoryInfo.totalMem);
                usedRAM = totalRAM - (memoryInfo.availMem);
                totalSys = buildInfo.getTotalStorageInfo(system);
                usedSys = buildInfo.getUsedStorageInfo(system);
                totalInternal = buildInfo.getTotalStorageInfo(data);
                usedInternal = buildInfo.getUsedStorageInfo(data);
                if (map.size() != 0) {
                    isZramAvail = true;
                    totalZRAM = Long.parseLong(map.get("swaptotal").trim()) / 1024L;
                    usedZRAM = totalZRAM - Long.parseLong(map.get("swapfree").trim()) / 1024L;
                }
                if (getContext() == null)
                    return;
                File[] files = ContextCompat.getExternalFilesDirs(getContext(), null);
                if (files.length > 1 && files[0] != null && files[1] != null) {
                    path = files[1].getPath();
                    isExtAvail = true;
                    totalExternal = buildInfo.getTotalStorageInfo(files[1].getPath());
                    usedExternal = buildInfo.getUsedStorageInfo(files[1].getPath());
                }
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

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    public void updateUI() {
        progressBar.setVisibility(View.GONE);
        scrollView.setVisibility(View.VISIBLE);
        progressRam = (int) (usedRAM * 100 / totalRAM);
        progressRom = (int) (usedInternal * 100 / totalInternal);
        progressSys = (int) (usedSys * 100 / totalSys);
        progress_ram.setProgress(progressRam);
        progress_rom.setProgress(progressRom);
        progress_sys.setProgress(progressSys);
        txtUsedRam = formattedValue(usedRAM) + " used of " + formattedValue(totalRAM);
        txtUsedSys = formattedValue(usedSys) + " used of " + formattedValue(totalSys);
        txtUsedRom = formattedValue(usedInternal) + " used of " + formattedValue(totalInternal);
        txt_usedram.setText(txtUsedRam);
        txt_usedsys.setText(txtUsedSys);
        txt_usedrom.setText(txtUsedRom);
        txt_syspath.setText(system);
        txt_rompath.setText(data);
        txtRamInfo.setTextColor(color);
        if (isZramAvail && totalZRAM > 0) {
            card_zram.setVisibility(View.VISIBLE);
            progressZRAM = (int) (usedZRAM * 100 / totalZRAM);
            progress_zram.setProgress(progressZRAM);
            txtUsedZRAM = String.format("%.1f", (float) usedZRAM) + " MB used of " + String.format("%.1f", (float) totalZRAM) + " MB";
            txt_usedzram.setText(txtUsedZRAM);
        }
        if (getContext() == null)
            return;
        File[] files = ContextCompat.getExternalFilesDirs(getContext(), null);
        if (files.length > 1 && files[0] != null && files[1] != null) {
            progressExt = (int) (usedExternal * 100 / totalExternal);
            card_sd.setVisibility(View.VISIBLE);
            path = files[1].getPath();
            txt_sdpath.setText(path);
            progress_ext.setProgress(progressExt);
            txtUsedSd = formattedValue(usedExternal) + " used of " + formattedValue(totalExternal);
            txt_usedsd.setText(txtUsedSd);
        }
        if (!memory.equals("") || !bandwidth.equals("") || !channels.equals("")) {
            card_raminfo.setVisibility(View.VISIBLE);
            if (!memory.equals("")) {
                txtRamType.setText(memory);
                txtRamType.setTextColor(color);
                linear1.setVisibility(View.VISIBLE);
            }
            if (!bandwidth.equals("")) {
                txtBandwidth.setText(bandwidth);
                txtBandwidth.setTextColor(color);
                linear2.setVisibility(View.VISIBLE);
            }
            if (!channels.equals("")) {
                txtChannels.setText(channels);
                txtChannels.setTextColor(color);
                linear3.setVisibility(View.VISIBLE);
            }
        }
        getRAM();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("extAvail", isExtAvail);
        outState.putInt("progressRam", progressRam);
        outState.putInt("progressRom", progressRom);
        outState.putInt("progressSys", progressSys);
        outState.putLong("totalRAM", totalRAM);
        outState.putLong("usedRAM", usedRAM);
        outState.putLong("totalSys", totalSys);
        outState.putLong("usedSys", usedSys);
        outState.putLong("totalInternal", totalInternal);
        outState.putLong("usedInternal", usedInternal);
        outState.putString("txtUsedRam", txtUsedRam);
        outState.putString("txtUsedSys", txtUsedSys);
        outState.putString("txtUsedRom", txtUsedRom);
        outState.putString("system", system);
        outState.putString("data", data);
        outState.putString("memory", memory);
        outState.putString("bandwidth", bandwidth);
        outState.putString("channels", channels);
        outState.putBoolean("isZramAvail", isZramAvail);
        if (isZramAvail) {
            outState.putLong("usedZRAM", usedZRAM);
            outState.putLong("totalZRAM", totalZRAM);
        }
        if (isExtAvail) {
            outState.putInt("progressExt", progressExt);
            outState.putLong("totalExternal", totalExternal);
            outState.putLong("usedExternal", usedExternal);
            outState.putString("txtUsedSd", txtUsedSd);
            outState.putString("path", path);
        }
    }

    public void getRAM() {
        refreshRAM.postDelayed(new Runnable() {
            @Override
            public void run() {
                memoryInfo = new ActivityManager.MemoryInfo();
                manager.getMemoryInfo(memoryInfo);
                totalRAM = memoryInfo.totalMem;
                usedRAM = totalRAM - memoryInfo.availMem;
                bundle.putInt("update", 1);
                Message message = new Message();
                message.setData(bundle);
                mainHandler.sendMessage(message);
                getRAM();
            }
        }, 2000);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        MenuItem item = menu.findItem(R.id.search);
        item.setVisible(false);
    }

    @SuppressLint("DefaultLocale")
    public String formattedValue(long l) {
        float f;
        String s;
        if (l > 1024 * 1024 * 1024 * 10.0) {
            f = (float) l / (1024 * 1024 * 1024);
            s = String.format("%.1f", f) + " GB";
        } else if (l > 1024 * 1024 * 10.0 && l <= 1024 * 1024 * 1024 * 10.0) {
            f = (float) l / (1024 * 1024);
            s = String.format("%.1f", f) + " MB";
        } else if (l > 1024 && l <= 1024 * 1024 * 10.0) {
            f = (float) l / (1024);
            s = String.format("%.1f", f) + " KB";
        } else {
            f = (float) l;
            s = String.format("%.2f", f) + " Bytes";
        }
        return s;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (thread != null)
            thread.quit();
        if (refreshThread != null)
            refreshThread.quit();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public boolean handleMessage(@NonNull Message msg) {
        if (msg.getData().getInt("update") == 1) {
            progress_ram.setProgress((int) (usedRAM * 100 / totalRAM));
            txt_usedram.setText(formattedValue(usedRAM) + " used of " + formattedValue(totalRAM));
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
        } else if (isAdded()) {
            NativeBannerAdInflate nativeBannerAdInflate = new NativeBannerAdInflate(getContext(), color);
            nativeBannerAdInflate.inflateAd(nativeBannerAd, nativeAdLayout);
            card_ad.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAdClicked(Ad ad) {
    }

    @Override
    public void onLoggingImpression(Ad ad) {
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rel_ram) {
            try {
                startActivity(new Intent("android.settings.APP_MEMORY_USAGE"));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else if (id == R.id.rel_rom) {
            try {
                startActivity(new Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}















