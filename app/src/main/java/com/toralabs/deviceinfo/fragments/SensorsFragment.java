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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.toralabs.deviceinfo.R;
import com.toralabs.deviceinfo.activities.MainActivity;
import com.toralabs.deviceinfo.adapter.SensorAdapter;
import com.toralabs.deviceinfo.menuItems.Preferences;
import com.toralabs.deviceinfo.models.SensorListModel;

import java.util.ArrayList;

public class SensorsFragment extends Fragment {
    RecyclerView recycler_sensor;
    SensorAdapter sensorAdapter;
    Preferences preferences;
    int color, sensorCount;
    boolean darkmode;
    TextView txt_sensorcount;
    RelativeLayout relSensors;
    ProgressBar progressBar;
    ArrayList<SensorListModel> list = new ArrayList<>();
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
        darkmode = preferences.getMode();
        color = Color.parseColor(preferences.getCircleColor());
        if (savedInstanceState != null) {
            list = savedInstanceState.getParcelableArrayList("sensorList");
            sensorCount = savedInstanceState.getInt("sensorCount");
        } else {
            list = mainActivity.sensorList;
            sensorCount = mainActivity.sensorCount;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_sensors, container, false);
        setHasOptionsMenu(true);
        recycler_sensor = view.findViewById(R.id.recycler_sensor);
        txt_sensorcount = view.findViewById(R.id.txt_sensorcount);
        relSensors = view.findViewById(R.id.relSensors);
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

    @SuppressLint("SetTextI18n")
    public void updateUI() {
        progressBar.setVisibility(View.GONE);
        relSensors.setVisibility(View.VISIBLE);
        txt_sensorcount.setText(sensorCount + " " + getResources().getString(R.string.sensor_avai));
        txt_sensorcount.setTextColor(color);
        sensorAdapter = new SensorAdapter(getContext(), list);
        recycler_sensor.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler_sensor.setNestedScrollingEnabled(false);
        recycler_sensor.setAdapter(sensorAdapter);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("sensorList", list);
        outState.putInt("sensorCount", sensorCount);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
