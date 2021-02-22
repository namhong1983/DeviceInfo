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
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.toralabs.deviceinfo.R;
import com.toralabs.deviceinfo.activities.MainActivity;
import com.toralabs.deviceinfo.adapter.SimpleAdapter;
import com.toralabs.deviceinfo.impClasses.BuildInfo;
import com.toralabs.deviceinfo.menuItems.Preferences;
import com.toralabs.deviceinfo.models.SimpleModel;

import java.util.ArrayList;

public class DisplayFragment extends Fragment {
    RecyclerView recycler_display;
    SimpleAdapter simpleAdapter;
    Preferences preferences;
    ArrayList<SimpleModel> list = new ArrayList<>();
    BuildInfo buildInfo;
    int color;
    FloatingActionButton fab_display;
    boolean darkmode;
    CardView cardDisplay;
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
        color = Color.parseColor(preferences.getCircleColor());
        buildInfo = new BuildInfo(getContext());
        darkmode = preferences.getMode();
        if (savedInstanceState != null)
            list = savedInstanceState.getParcelableArrayList("displayList");
        else
            list = mainActivity.displayList;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_display, container, false);
        setHasOptionsMenu(true);
        fab_display = view.findViewById(R.id.fab_display);
        recycler_display = view.findViewById(R.id.recycler_display);
        progressBar = view.findViewById(R.id.progressBar);
        cardDisplay = view.findViewById(R.id.cardDisplay);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            progressBar.getIndeterminateDrawable().setColorFilter(new BlendModeColorFilter(color, BlendMode.SRC_ATOP));
        else
            progressBar.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        fab_display.setBackgroundTintList(ColorStateList.valueOf(color));
        fab_display.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(new Intent(Settings.ACTION_DISPLAY_SETTINGS));
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        });
        recycler_display.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0)
                    fab_display.hide();
                else
                    fab_display.show();
            }
        });
        updateUI();
        return view;
    }

    public void updateUI() {
        cardDisplay.setVisibility(View.VISIBLE);
        simpleAdapter = new SimpleAdapter(list, getContext(), color);
        recycler_display.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler_display.setNestedScrollingEnabled(true);
        recycler_display.setAdapter(simpleAdapter);
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
        outState.putParcelableArrayList("displayList", list);
    }
}
