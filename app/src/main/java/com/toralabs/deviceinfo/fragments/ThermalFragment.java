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
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.toralabs.deviceinfo.R;
import com.toralabs.deviceinfo.activities.MainActivity;
import com.toralabs.deviceinfo.adapter.ThermalAdapter;
import com.toralabs.deviceinfo.impClasses.BuildInfo;
import com.toralabs.deviceinfo.menuItems.Preferences;
import com.toralabs.deviceinfo.models.ThermalModel;

import java.util.ArrayList;

public class ThermalFragment extends Fragment {
    RecyclerView recycler_thermal;
    ThermalAdapter thermalAdapter;
    Preferences preferences;
    ArrayList<ThermalModel> list = new ArrayList<>();
    boolean darkmode;
    Handler h;
    BuildInfo buildInfo;
    ProgressBar progressBar;
    TextView txt_nosensor;
    int color;
    boolean unit;
    MainActivity mainActivity;
    HandlerThread handlerThread = new HandlerThread("GetRunThread");

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
        unit = preferences.getTemp();
        buildInfo = new BuildInfo(getContext());
        color = Color.parseColor(preferences.getCircleColor());
        darkmode = preferences.getMode();
        handlerThread.start();
        h = new Handler(handlerThread.getLooper());
        if (savedInstanceState != null)
            list = savedInstanceState.getParcelableArrayList("thermalList");
        else
            list = mainActivity.thermalList;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_thermal, container, false);
        setHasOptionsMenu(true);
        recycler_thermal = view.findViewById(R.id.recycler_thermal);
        txt_nosensor = view.findViewById(R.id.txt_nosensor);
        progressBar = view.findViewById(R.id.progressBar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            progressBar.getIndeterminateDrawable().setColorFilter(new BlendModeColorFilter(color, BlendMode.SRC_ATOP));
        else
            progressBar.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        updateUI();
        return view;
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        MenuItem item = menu.findItem(R.id.search);
        item.setVisible(false);
    }

    public void updateUI() {
        if (list.size() == 0) {
            txt_nosensor.setTextColor(color);
            txt_nosensor.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            recycler_thermal.setVisibility(View.GONE);
        } else {
            txt_nosensor.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            thermalAdapter = new ThermalAdapter(list, getContext(), 1);
            recycler_thermal.setLayoutManager(new GridLayoutManager(getContext(), 2));
            recycler_thermal.setAdapter(thermalAdapter);
            getRun();
        }
    }

    public void getRun() {
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 29; i++) {
                    if (buildInfo.thermalType(i) != null) {
                        final String temp = buildInfo.thermalTemp(i);
                        if (temp!=null && !temp.contains("0.0")) {
                            final int finalI = i;
                            if (getActivity() == null)
                                return;
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    thermalAdapter.notifyItemChanged(finalI, temp);
                                }
                            });
                        }
                    }
                }
                getRun();
            }
        }, 2000);
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
        outState.putParcelableArrayList("thermalList", list);
    }
}
