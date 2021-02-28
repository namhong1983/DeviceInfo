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
import android.view.InputDevice;
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
import com.toralabs.deviceinfo.adapter.InputAdapter;
import com.toralabs.deviceinfo.impClasses.BuildInfo;
import com.toralabs.deviceinfo.menuItems.Preferences;
import com.toralabs.deviceinfo.models.InputModel;

import java.util.ArrayList;

/**
 * Created by @mrudultora
 */

public class InputFragment extends Fragment {
    Preferences preferences;
    BuildInfo buildInfo;
    ArrayList<InputModel> list = new ArrayList<>();
    int[] id;
    RecyclerView recycler_input;
    TextView txtNoInput;
    int color;
    ProgressBar progressBar;
    InputAdapter inputAdapter;
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
        id = InputDevice.getDeviceIds();
        if (savedInstanceState != null)
            list = savedInstanceState.getParcelableArrayList("inputList");
        else
            list = mainActivity.inputList;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_input, container, false);
        setHasOptionsMenu(true);
        recycler_input = view.findViewById(R.id.recycler_input);
        txtNoInput = view.findViewById(R.id.txtNoInput);
        progressBar = view.findViewById(R.id.progressBar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            progressBar.getIndeterminateDrawable().setColorFilter(new BlendModeColorFilter(color, BlendMode.SRC_ATOP));
        else
            progressBar.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        updateUI();
        return view;
    }

    public void updateUI() {
        progressBar.setVisibility(View.GONE);
        recycler_input.setVisibility(View.VISIBLE);
        if (id.length == 0) {
            txtNoInput.setTextColor(color);
            txtNoInput.setVisibility(View.VISIBLE);
        } else txtNoInput.setVisibility(View.GONE);
        inputAdapter = new InputAdapter(getContext(), list, color);
        recycler_input.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler_input.setAdapter(inputAdapter);
        recycler_input.getRecycledViewPool().setMaxRecycledViews(1, 2);
        recycler_input.setItemViewCacheSize(10);
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
        outState.putParcelableArrayList("inputList", list);
    }
}
