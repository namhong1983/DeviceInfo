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

import android.annotation.SuppressLint;
import android.content.Context;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.toralabs.deviceinfo.R;
import com.toralabs.deviceinfo.activities.MainActivity;
import com.toralabs.deviceinfo.adapter.CpuAdapter;
import com.toralabs.deviceinfo.impClasses.BuildInfo;
import com.toralabs.deviceinfo.menuItems.Preferences;
import com.toralabs.deviceinfo.models.CpuModel;

import java.util.ArrayList;

/**
 * Created by @mrudultora
 */

public class CpuFragment extends Fragment {
    Preferences preferences;
    BuildInfo buildInfo;
    CpuAdapter cpuAdapter;
    RecyclerView recycler_cpu;
    ImageView image_company;
    TextView txt_company, txt;
    ArrayList<CpuModel> cpuList = new ArrayList<>();
    RelativeLayout rel_main, rel_cpu;
    int color;
    String family, machine, processor;
    Handler cpuCoreHandler;
    HandlerThread handlerThread = new HandlerThread("CpuCoreThread");
    ProgressBar progressBar;
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
        buildInfo = new BuildInfo(getContext());
        color = Color.parseColor(preferences.getCircleColor());
        handlerThread.start();
        cpuCoreHandler = new Handler(handlerThread.getLooper());
        if (savedInstanceState != null) {
            cpuList = savedInstanceState.getParcelableArrayList("cpuList");
            processor = savedInstanceState.getString("processor");
            family = savedInstanceState.getString("family");
            machine = savedInstanceState.getString("machine");
        } else {
            processor = mainActivity.processorName;
            family = mainActivity.family;
            machine = mainActivity.machine;
            cpuList = mainActivity.cpuList;
        }
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_cpu, container, false);
        setHasOptionsMenu(true);

        rel_main = view.findViewById(R.id.rel_main);
        rel_cpu = view.findViewById(R.id.rel_cpu);
        txt = view.findViewById(R.id.txt);
        recycler_cpu = view.findViewById(R.id.recycler_cpu);
        image_company = view.findViewById(R.id.img_company);
        txt_company = view.findViewById(R.id.name);
        progressBar = view.findViewById(R.id.progressBar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            progressBar.getIndeterminateDrawable().setColorFilter(new BlendModeColorFilter(color, BlendMode.SRC_ATOP));
        else
            progressBar.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        updateHeader();
        updateUI();
        return view;
    }

    @SuppressLint("SetTextI18n")
    public void updateHeader() {
        if (!processor.equals("error") && !machine.equals("error") && !family.equals("error")) {
            if (processor.trim().equalsIgnoreCase("qualcomm")) {
                if (getContext() != null)
                    image_company.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_qual));
                txt_company.setText("Qualcomm® Snapdragon™ " + machine);
                txt_company.setTextColor(color);
                image_company.setVisibility(View.VISIBLE);
                txt_company.setVisibility(View.VISIBLE);
            } else if (processor.equalsIgnoreCase("mediatek")) {
                if (getContext() != null)
                    image_company.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_mediatek));
                txt_company.setText("MediaTek Helio " + machine);
                txt_company.setTextColor(color);
                image_company.setVisibility(View.VISIBLE);
                txt_company.setVisibility(View.VISIBLE);
            } else if (processor.equalsIgnoreCase("hisilicon")) {
                if (getContext() != null)
                    image_company.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_kirin));
                txt_company.setText("HiSilicon Kirin " + machine);
                txt_company.setTextColor(color);
                image_company.setVisibility(View.VISIBLE);
                txt_company.setVisibility(View.VISIBLE);
            } else if (processor.equalsIgnoreCase("samsung")) {
                if (getContext() != null)
                    image_company.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_exynos));
                txt_company.setText("Samsung Exynos " + machine);
                txt_company.setTextColor(color);
                image_company.setVisibility(View.VISIBLE);
                txt_company.setVisibility(View.VISIBLE);
            } else if (processor.equalsIgnoreCase("nvidia")) {
                if (getContext() != null)
                    image_company.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_tegra));
                txt_company.setText("Nvidia Tegra " + machine);
                txt_company.setTextColor(color);
                image_company.setVisibility(View.VISIBLE);
                txt_company.setVisibility(View.VISIBLE);
            } else if (processor.equalsIgnoreCase("intel")) {
                if (getContext() != null)
                    image_company.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_atom));
                txt_company.setText("Intel Atom " + machine);
                txt_company.setTextColor(color);
                image_company.setVisibility(View.VISIBLE);
                txt_company.setVisibility(View.VISIBLE);
            } else if (processor.equalsIgnoreCase("rockchip")) {
                if (getContext() != null)
                    image_company.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_rockchip));
                txt_company.setText("Rockchip " + machine);
                txt_company.setTextColor(color);
                image_company.setVisibility(View.VISIBLE);
                txt_company.setVisibility(View.VISIBLE);
            } else {
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) txt_company.getLayoutParams();
                lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
                txt_company.setLayoutParams(lp);
                txt_company.setText(processor + " " + family + " " + machine);
                txt_company.setTextColor(color);
                txt_company.setVisibility(View.VISIBLE);
            }
            txt.setVisibility(View.VISIBLE);
            rel_cpu.setVisibility(View.VISIBLE);
        } else {
            txt.setVisibility(View.GONE);
            rel_cpu.setVisibility(View.GONE);
        }
    }

    public void updateUI() {
        cpuAdapter = new CpuAdapter(getContext(), cpuList, color);
        recycler_cpu.setAdapter(cpuAdapter);
        progressBar.setVisibility(View.GONE);
        recycler_cpu.setVisibility(View.VISIBLE);
        recycler_cpu.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler_cpu.setNestedScrollingEnabled(false);
        getRunning();
    }

    public void getRunning() {
        int pos = 8;
        final String[] str = new String[2];
        if (cpuList.get(0).getTag().equalsIgnoreCase("processorname"))
            pos = 9;
        final int position = pos;
        cpuCoreHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                str[0] = buildInfo.getRunningCpuString();
                str[1] = buildInfo.getUsage();
                if (getActivity() == null)
                    return;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cpuAdapter.notifyItemChanged(position, str[0]);
                        cpuAdapter.notifyItemChanged(position + 1, str[1]);
                    }
                });
                getRunning();
            }
        }, 1200);
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
        outState.putParcelableArrayList("cpuList", cpuList);
        outState.putString("processor", processor);
        outState.putString("family", family);
        outState.putString("machine", machine);
    }
}
