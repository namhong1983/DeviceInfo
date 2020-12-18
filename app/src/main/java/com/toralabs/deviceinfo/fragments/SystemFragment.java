package com.toralabs.deviceinfo.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaDrm;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.facebook.ads.NativeBannerAd;
import com.toralabs.deviceinfo.R;
import com.toralabs.deviceinfo.activities.MainActivity;
import com.toralabs.deviceinfo.adapter.SimpleAdapter;
import com.toralabs.deviceinfo.impClasses.BuildInfo;
import com.toralabs.deviceinfo.impClasses.NativeBannerAdInflate;
import com.toralabs.deviceinfo.menuItems.Preferences;
import com.toralabs.deviceinfo.models.SimpleModel;

import java.util.ArrayList;
import java.util.UUID;

public class SystemFragment extends Fragment implements NativeAdListener {
    NativeAdLayout nativeAdLayout;
    NativeBannerAd nativeBannerAd;
    TextView tv_version, tv_root, tv_releasedate;
    RecyclerView recycler_system;
    SimpleAdapter simpleAdapter;
    Preferences preferences;
    String uptime;
    ImageView img_android;
    BuildInfo buildInfo;
    CardView card_android, cardAd;
    int logoId, color;
    boolean darkmode, bool;
    CardView card1, card2, card3;
    TextView txt_clearkey, txt_vendor_clearkey, clearkey_vendor_name, clearkey_txt_version, clearkey_version_name, txt_widevine, widevine_txt_vendor;
    TextView widevine_vendor_name, widevine_txt_version, widevine_version_name, txt_algo, widevine_algo, txt_sysid, widevine_sysid, txt_security, widevine_seclevel;
    TextView txt_maxhdcp, widevine_maxhdcp, txt_maxsession, widevine_maxsess, txt_usage, widevine_usage, txt_hdcp, widevine_hdcp;
    ArrayList<SimpleModel> list = new ArrayList<>();
    String[] wide = new String[9];
    String[] clearkey = new String[2];
    RelativeLayout relSystem;
    ProgressBar progressBar;
    HandlerThread handlerThread = new HandlerThread("UptimeThread");
    Handler h;
    int rootPos;
    MainActivity mainActivity;

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
        darkmode = preferences.getMode();
        buildInfo = new BuildInfo(getContext());
        handlerThread.start();
        h = new Handler(handlerThread.getLooper());
        if (savedInstanceState != null) {
            list = savedInstanceState.getParcelableArrayList("systemList");
            wide = savedInstanceState.getStringArray("wide");
            clearkey = savedInstanceState.getStringArray("clearkey");
            logoId = savedInstanceState.getInt("logoId");
        } else {
            list = mainActivity.systemList;
            wide = mainActivity.wide;
            clearkey = mainActivity.clearkey;
            logoId = mainActivity.logoId;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_system, container, false);
        setHasOptionsMenu(true);
        findViewByIds(view);
        progressBar = view.findViewById(R.id.progressBar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            progressBar.getIndeterminateDrawable().setColorFilter(new BlendModeColorFilter(color, BlendMode.SRC_ATOP));
        else
            progressBar.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        updateUIList();
        updateUIDrm();
        if (!bool && getContext() != null) {
            nativeBannerAd = new NativeBannerAd(getContext(), getResources().getString(R.string.nativead2));
            nativeBannerAd.loadAd(nativeBannerAd.buildLoadAdConfig().withAdListener(this).build());
        }
        return view;
    }

    @SuppressLint("SetTextI18n")
    public void updateUIList() {
        progressBar.setVisibility(View.GONE);
        relSystem.setVisibility(View.VISIBLE);
        card_android.setCardBackgroundColor(color);
        simpleAdapter = new SimpleAdapter(list, getContext(), color);
        recycler_system.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler_system.setNestedScrollingEnabled(false);
        recycler_system.setAdapter(simpleAdapter);
        if (getContext() != null)
            img_android.setImageDrawable(ContextCompat.getDrawable(getContext(), logoId));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            rootPos = 10;
        else
            rootPos = 9;
        if (list.get(rootPos).getDesc().equalsIgnoreCase("yes")) {
            tv_root.setText(getResources().getString(R.string.root_yes));
        } else {
            tv_root.setText(getResources().getString(R.string.root_no));
        }
        tv_version.setText(list.get(0).getDesc());
        tv_releasedate.setText(getResources().getString(R.string.released) + ": " + buildInfo.getReleaseDate(Integer.parseInt(list.get(1).getDesc())));
        getRun();
    }

    public void updateUIDrm() {
        if (MediaDrm.isCryptoSchemeSupported(UUID.fromString("edef8ba9-79d6-4ace-a3c8-27dcd51d21ed"))) {
            card2.setVisibility(View.VISIBLE);
            txt_widevine.setTextColor(color);
            widevine_vendor_name.setTextColor(color);
            widevine_version_name.setTextColor(color);
            widevine_algo.setTextColor(color);
            widevine_hdcp.setTextColor(color);
            widevine_maxhdcp.setTextColor(color);
            widevine_maxsess.setTextColor(color);
            widevine_sysid.setTextColor(color);
            widevine_usage.setTextColor(color);
            widevine_seclevel.setTextColor(color);

            widevine_vendor_name.setText(wide[0]);
            widevine_version_name.setText(wide[1]);
            widevine_algo.setText(wide[2]);
            widevine_sysid.setText(wide[3]);
            widevine_seclevel.setText(wide[4]);
            widevine_maxhdcp.setText(wide[5]);
            widevine_maxsess.setText(wide[6]);
            widevine_usage.setText(wide[7]);
            widevine_hdcp.setText(wide[8]);
        }
        if (MediaDrm.isCryptoSchemeSupported(UUID.fromString("e2719d58-a985-b3c9-781a-b030af78d30e"))) {
            card3.setVisibility(View.VISIBLE);
            txt_clearkey.setTextColor(color);
            clearkey_vendor_name.setTextColor(color);
            clearkey_version_name.setTextColor(color);
            clearkey_vendor_name.setText(clearkey[0]);
            clearkey_version_name.setText(clearkey[1]);
        }
    }

    public void findViewByIds(View view) {
        relSystem = view.findViewById(R.id.relSystem);
        progressBar = view.findViewById(R.id.progressBar);
        card1 = view.findViewById(R.id.card1);
        card2 = view.findViewById(R.id.card2);
        card3 = view.findViewById(R.id.card3);
        cardAd = view.findViewById(R.id.cardAd);
        tv_version = view.findViewById(R.id.tv_version);
        tv_root = view.findViewById(R.id.tv_root);
        tv_releasedate = view.findViewById(R.id.tv_releasedate);
        recycler_system = view.findViewById(R.id.recycler_system);
        txt_algo = view.findViewById(R.id.txt_algo);
        clearkey_txt_version = view.findViewById(R.id.clearkey_txt_version);
        clearkey_vendor_name = view.findViewById(R.id.clearkey_vendor_name);
        clearkey_version_name = view.findViewById(R.id.clearkey_version_name);
        txt_clearkey = view.findViewById(R.id.txt_clearkey);
        txt_vendor_clearkey = view.findViewById(R.id.txt_vendor_clearkey);
        txt_widevine = view.findViewById(R.id.txt_widevine);
        widevine_algo = view.findViewById(R.id.widevine_algo);
        widevine_hdcp = view.findViewById(R.id.widevine_hdcp);
        widevine_maxhdcp = view.findViewById(R.id.widevine_maxhdcp);
        widevine_maxsess = view.findViewById(R.id.widevine_maxsess);
        txt_sysid = view.findViewById(R.id.txt_sysid);
        widevine_sysid = view.findViewById(R.id.widevine_sysid);
        widevine_seclevel = view.findViewById(R.id.widevine_seclevel);
        widevine_txt_vendor = view.findViewById(R.id.widevine_txt_vendor);
        widevine_version_name = view.findViewById(R.id.widevine_version_name);
        widevine_usage = view.findViewById(R.id.widevine_usage);
        widevine_vendor_name = view.findViewById(R.id.widevine_vendor_name);
        widevine_txt_version = view.findViewById(R.id.widevine_txt_version);
        txt_security = view.findViewById(R.id.txt_security);
        txt_hdcp = view.findViewById(R.id.txt_hdcp);
        txt_usage = view.findViewById(R.id.txt_usage);
        txt_maxhdcp = view.findViewById(R.id.txt_maxhdcp);
        txt_maxsession = view.findViewById(R.id.txt_maxsession);
        card_android = view.findViewById(R.id.card_android);
        img_android = view.findViewById(R.id.img_android);
        nativeAdLayout = view.findViewById(R.id.nativeBannerAd);
    }


    public void getRun() {
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                uptime = buildInfo.getUpTime();
                if (getActivity() == null)
                    return;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        simpleAdapter.update(list.size() - 3, uptime);
                    }
                });
                getRun();
            }
        }, 1000);

    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        MenuItem item = menu.findItem(R.id.search);
        item.setVisible(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handlerThread != null)
            handlerThread.quit();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("systemList", list);
        outState.putStringArray("wide", wide);
        outState.putStringArray("clearkey", clearkey);
        outState.putInt("logoId", logoId);
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
