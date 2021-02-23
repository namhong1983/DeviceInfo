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
package com.toralabs.deviceinfo.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.toralabs.deviceinfo.activities.ListFeaturesActivity;
import com.toralabs.deviceinfo.activities.MainActivity;
import com.toralabs.deviceinfo.adapter.ClickableAdapter;
import com.toralabs.deviceinfo.impClasses.BuildInfo;
import com.toralabs.deviceinfo.R;
import com.toralabs.deviceinfo.impClassMethods.DeviceMethod;
import com.toralabs.deviceinfo.menuItems.Preferences;
import com.toralabs.deviceinfo.models.ClickableModel;

import java.util.ArrayList;

/**
 * Created by @mrudultora
 */

public class DeviceFragment extends Fragment implements ClickableAdapter.ItemClickListener, Runnable, Handler.Callback {
    RecyclerView recycler_device;
    ClickableAdapter clickableAdapter;
    Preferences preferences;
    ArrayList<ClickableModel> list = new ArrayList<>();
    BuildInfo buildInfo;
    TextView name;
    ImageView img_company;
    int color, drawableId;
    boolean darkMode, iconAvail = true;
    CardView card_device;
    ProgressBar progressBar;
    Bundle bundle = null;
    MainActivity mainActivity;
    DeviceMethod deviceMethod;
    HandlerThread handlerThread = new HandlerThread("DeviceThread");
    Handler deviceHandler;
    Handler updateHandler;
    FeatureInfo[] featureInfo;
    ArrayList<String> featureList = new ArrayList<>();

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
        buildInfo = new BuildInfo(getContext());
        darkMode = preferences.getMode();
        color = Color.parseColor(preferences.getCircleColor());
        if (savedInstanceState != null) {
            bundle = savedInstanceState;
            list = savedInstanceState.getParcelableArrayList("deviceList");
            iconAvail = savedInstanceState.getBoolean("iconAvail");
            if (iconAvail) {
                drawableId = savedInstanceState.getInt("drawable");
            }
        } else {
            list = mainActivity.deviceList;
            drawableId = mainActivity.drawableId;
            iconAvail = mainActivity.iconAvail;
        }
        updateHandler = new Handler(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_device, container, false);
        setHasOptionsMenu(true);
        recycler_device = view.findViewById(R.id.recycler_device);
        name = view.findViewById(R.id.name);
        img_company = view.findViewById(R.id.img_company);
        card_device = view.findViewById(R.id.card_device);
        progressBar = view.findViewById(R.id.progressBar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            progressBar.getIndeterminateDrawable().setColorFilter(new BlendModeColorFilter(color, BlendMode.SRC_ATOP));
        else
            progressBar.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        card_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(Settings.ACTION_DEVICE_INFO_SETTINGS));
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        });
        updateUI();
        return view;
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        MenuItem item = menu.findItem(R.id.search);
        item.setVisible(false);
    }

    public void deviceFeatures() {
        if (getContext() != null)
            featureInfo = getContext().getPackageManager().getSystemAvailableFeatures();
        if (featureInfo != null)
            for (FeatureInfo feature : featureInfo) {
                if (feature.name != null)
                    featureList.add(feature.name);
            }
    }

    @SuppressLint("SetTextI18n")
    public void updateUI() {
        progressBar.setVisibility(View.GONE);
        name.setTextColor(color);
        if (iconAvail) {
            setHeader(drawableId);
        } else {
            name.setText(list.get(1).getDesc() + "\n" + list.get(0).getDesc());
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) name.getLayoutParams();
            lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
            name.setLayoutParams(lp);
        }
        clickableAdapter = new ClickableAdapter(getContext(), list, color, this);
        recycler_device.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler_device.setNestedScrollingEnabled(false);
        recycler_device.setAdapter(clickableAdapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                handlerThread.start();
                deviceHandler = new Handler(handlerThread.getLooper());
                deviceHandler.post(this);
            }
        }
    }

    @SuppressLint("SetTextI18n")
    public void setHeader(int id) {
        if (getContext() != null)
            img_company.setImageDrawable(ContextCompat.getDrawable(getContext(), id));
        name.setText(list.get(1).getDesc() + "\n" + list.get(0).getDesc());
        img_company.setVisibility(View.VISIBLE);
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
        outState.putParcelableArrayList("deviceList", list);
        outState.putStringArrayList("featureList", featureList);
        outState.putBoolean("iconAvail", iconAvail);
        if (iconAvail)
            outState.putInt("drawable", drawableId);
    }

    @Override
    public void onItemClick(int position, View view) {
        if (getContext() != null && getActivity() != null) {
            if (list.get(position).getClick() && list.get(position).getTitle().equals(getResources().getString(R.string.devicefeatures))) {
                deviceFeatures();
                Intent intent = new Intent(getActivity(), ListFeaturesActivity.class);
                intent.putExtra("name", "feature");
                intent.putStringArrayListExtra("featuresList", featureList);
                ActivityOptions options = ActivityOptions.makeCustomAnimation(getActivity(), android.R.anim.fade_in, android.R.anim.fade_out);
                startActivity(intent, options.toBundle());
            } else if (list.get(position).getClick() && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
            }
        }
    }

    @Override
    public void onItemLongClick(int position, View view) {
        if (getContext() != null) {
            ClipboardManager clipboardManager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("Text Label", list.get(position).getDesc());
            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText(getContext().getApplicationContext(), getResources().getString(R.string.copied) + " " + list.get(position).getTitle(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void run() {
        deviceMethod = new DeviceMethod(getContext());
        list = deviceMethod.getDeviceList();
        Bundle bundle = new Bundle();
        Message message = new Message();
        clickableAdapter = null;
        clickableAdapter = new ClickableAdapter(getContext(), list, color, this);
        bundle.putInt("update", 1);
        System.out.println("DeviceFragment update");
        message.setData(bundle);
        updateHandler.sendMessage(message);
    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        if (msg.getData().getInt("update") == 1) {
            clickableAdapter.notifyDataSetChanged();
            recycler_device.setAdapter(clickableAdapter);
        }
        return true;
    }
}

