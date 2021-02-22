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
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.toralabs.deviceinfo.R;
import com.toralabs.deviceinfo.adapter.AppListAdapter;
import com.toralabs.deviceinfo.menuItems.Preferences;
import com.toralabs.deviceinfo.models.AppListModel;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class AppsFragment extends Fragment implements MenuItem.OnActionExpandListener {
    RecyclerView recycler_apps;
    Spinner spinner_apps;
    TextView txt_noapps, txt_loading;
    TextView tv_count;
    ProgressBar progressBar;
    Drawable icon;
    AppListAdapter appListAdapter;
    String name, packagename, size, version, uid, targetsdk, minsdk = null, permissions = null;
    String firstInstallDate, lastUpdateDate;
    ArrayList<AppListModel> list = new ArrayList<>();
    List<ApplicationInfo> packagelist = new ArrayList<>();
    File file;
    int color;
    int flag;
    Preferences preferences;
    long longsize;
    androidx.appcompat.widget.SearchView searchView;

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_apps, container, false);
        setHasOptionsMenu(true);
        if (getContext() != null)
            preferences = new Preferences(getContext());
        color = Color.parseColor(preferences.getCircleColor());
        recycler_apps = view.findViewById(R.id.recycler_apps);
        spinner_apps = view.findViewById(R.id.spinner_apps);
        txt_noapps = view.findViewById(R.id.txt_noapps);
        txt_loading = view.findViewById(R.id.txt_loading);
        tv_count = view.findViewById(R.id.tv_count);
        progressBar = view.findViewById(R.id.progressBar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            progressBar.getIndeterminateDrawable().setColorFilter(new BlendModeColorFilter(color, BlendMode.SRC_ATOP));
        else
            progressBar.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        txt_loading.setTextColor(color);
        txt_noapps.setTextColor(color);
        PackageManager packageManager = getContext().getPackageManager();
        packagelist = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        tv_count.setText(getResources().getString(R.string.totalapps) + " : " + packagelist.size());
        tv_count.setTextColor(color);
        NewThread newThread = new NewThread();
        newThread.start();
        spinner_apps.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        allApps();
                        break;
                    case 1:
                        sortSystemApps();
                        break;
                    case 2:
                        installedApps();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return view;
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        MenuItem item = menu.findItem(R.id.search);
        item.setVisible(true);
        searchView = (androidx.appcompat.widget.SearchView) item.getActionView();
        menu.findItem(R.id.search).setOnActionExpandListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.search) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if (!newText.isEmpty()) {
                        filter(newText);
                    }
                    return true;
                }
            });
        } else
            return super.onOptionsItemSelected(item);
        return true;
    }

    public void filter(String s) {
        ArrayList<AppListModel> arrayList = new ArrayList<>();
        if (list.size() == packagelist.size()) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getName().toLowerCase().contains(s.toLowerCase())) {
                    arrayList.add(list.get(i));
                    txt_noapps.setVisibility(View.GONE);
                }
            }
            appListAdapter.filteredList(arrayList);
            if (arrayList.size() == 0) {
                txt_noapps.setVisibility(View.VISIBLE);
            }
        } else {
            if (getContext() != null)
                Toast.makeText(getContext().getApplicationContext(), "Please Wait...", Toast.LENGTH_SHORT).show();
        }
    }

    public void sortSystemApps() {
        ArrayList<AppListModel> systemList = new ArrayList<>();
        if (list.size() == packagelist.size()) {
            for (int i = 0; i < packagelist.size(); i++) {
                if (list.get(i).getFlag() == 0) {
                    systemList.add(list.get(i));
                }
            }
            appListAdapter.filteredList(systemList);
        } else {
            if (getContext() != null)
                Toast.makeText(getContext().getApplicationContext(), "Please Wait...", Toast.LENGTH_SHORT).show();
        }
    }

    public void installedApps() {
        ArrayList<AppListModel> installedList = new ArrayList<>();
        if (list.size() == packagelist.size()) {
            for (int i = 0; i < packagelist.size(); i++) {
                if (list.get(i).getFlag() == 1) {
                    installedList.add(list.get(i));
                }
            }
            appListAdapter.filteredList(installedList);

        } else {
            if (getContext() != null)
                Toast.makeText(getContext().getApplicationContext(), "Please Wait...", Toast.LENGTH_SHORT).show();
        }
    }

    public void allApps() {
        if (list != null && list.size() == packagelist.size()) {
            appListAdapter.filteredList(list);
        }
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        allApps();
        return true;
    }

    class NewThread extends Thread {
        @Override
        public void run() {
            super.run();
            list.clear();
            int i = 0;
            if (getActivity() == null)
                return;
            PackageManager manager = getActivity().getPackageManager();
            if (packagelist.size() > 0) {
                for (ApplicationInfo applicationInfo : packagelist) {
                    i++;
                    if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                        flag = 1;
                    } else {
                        flag = 0;
                    }
                    StringBuilder stringBuilder = new StringBuilder();
                    packagename = applicationInfo.packageName;
                    try {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        firstInstallDate = dateFormat.format(new Date(getContext().getPackageManager().getPackageInfo(packagename, 0).firstInstallTime));
                        lastUpdateDate = dateFormat.format(new Date(getContext().getPackageManager().getPackageInfo(packagename, 0).lastUpdateTime));
                        version = manager.getPackageInfo(packagename, 0).versionName;
                        String[] reqper = manager.getPackageInfo(packagename, PackageManager.GET_PERMISSIONS).requestedPermissions;
                        if (reqper != null) {
                            for (String per : reqper) {
                                stringBuilder.append("\n").append(per);
                            }
                        } else {
                            permissions = null;
                        }
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    permissions = stringBuilder.toString();
                    if (getActivity() == null) {
                        return;
                    } else {
                        icon = applicationInfo.loadIcon(getActivity().getPackageManager());
                    }
                    if (getActivity() == null) {
                        return;
                    } else {
                        name = applicationInfo.loadLabel(getActivity().getPackageManager()).toString();
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        minsdk = String.valueOf(applicationInfo.minSdkVersion);
                    }
                    targetsdk = String.valueOf(applicationInfo.targetSdkVersion);
                    uid = String.valueOf(applicationInfo.uid);
                    file = new File(applicationInfo.publicSourceDir);
                    longsize = file.length();
                    if (longsize > 1024 && longsize <= 1024 * 1024) {
                        size = (longsize / 1024 + " KB");
                    } else if (longsize > 1024 * 1024 && longsize <= 1024 * 1024 * 1024) {
                        size = (longsize / (1024 * 1024) + " MB");
                    } else {
                        size = (longsize / (1024 * 1024 * 1024) + " GB");
                    }
                    list.add(new AppListModel(icon, name, packagename, file, size, flag, version, targetsdk, minsdk, uid, permissions,firstInstallDate,lastUpdateDate));
                    if (i == packagelist.size() - 1) {
                        if (getActivity() == null)
                            return;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                                appListAdapter = new AppListAdapter(getContext(), list, Color.parseColor(preferences.getCircleColor()), recycler_apps);
                                appListAdapter.notifyDataSetChanged();
                                recycler_apps.setAdapter(appListAdapter);
                                recycler_apps.setLayoutManager(layoutManager);
                                recycler_apps.setHasFixedSize(true);
                                recycler_apps.setVisibility(View.VISIBLE);
                                txt_loading.setVisibility(View.GONE);
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }
                }
            }
        }
    }
}
