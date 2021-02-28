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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.toralabs.deviceinfo.R;
import com.toralabs.deviceinfo.activities.MainActivity;
import com.toralabs.deviceinfo.adapter.ThermalAdapter;
import com.toralabs.deviceinfo.impClasses.BuildInfo;
import com.toralabs.deviceinfo.menuItems.Preferences;
import com.toralabs.deviceinfo.models.ThermalModel;

import java.util.ArrayList;

/**
 * Created by @mrudultora
 */

public class CodecsFragment extends Fragment {
    RecyclerView recycler_codecs;
    ThermalAdapter adapter;
    TextView txt_nocodec;
    BuildInfo buildInfo;
    Preferences preferences;
    int color;
    ArrayList<ThermalModel> codecsList = new ArrayList<>();
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
        if (savedInstanceState != null)
            codecsList = savedInstanceState.getParcelableArrayList("codecList");
        else
            codecsList = mainActivity.codecsList;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_codecs, container, false);
        setHasOptionsMenu(true);
        recycler_codecs = view.findViewById(R.id.recycler_codecs);
        txt_nocodec = view.findViewById(R.id.txt_nocodec);
        progressBar = view.findViewById(R.id.progressBar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            progressBar.getIndeterminateDrawable().setColorFilter(new BlendModeColorFilter(color, BlendMode.SRC_ATOP));
        else
            progressBar.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        updateUI();
        return view;
    }

    public void updateUI() {
        adapter = new ThermalAdapter(codecsList, getContext(), 2);
        recycler_codecs.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler_codecs.setAdapter(adapter);
        recycler_codecs.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        MenuItem item = menu.findItem(R.id.search);
        item.setVisible(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("codecList", codecsList);
    }
}
