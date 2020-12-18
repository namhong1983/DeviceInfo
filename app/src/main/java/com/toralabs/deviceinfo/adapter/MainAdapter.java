package com.toralabs.deviceinfo.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.toralabs.deviceinfo.R;
import com.toralabs.deviceinfo.fragments.AppsFragment;
import com.toralabs.deviceinfo.fragments.BatteryFragment;
import com.toralabs.deviceinfo.fragments.CameraFragment;
import com.toralabs.deviceinfo.fragments.CodecsFragment;
import com.toralabs.deviceinfo.fragments.CpuFragment;
import com.toralabs.deviceinfo.fragments.DashboardFragment;
import com.toralabs.deviceinfo.fragments.DeviceFragment;
import com.toralabs.deviceinfo.fragments.DisplayFragment;
import com.toralabs.deviceinfo.fragments.InputFragment;
import com.toralabs.deviceinfo.fragments.MemoryFragment;
import com.toralabs.deviceinfo.fragments.SensorsFragment;
import com.toralabs.deviceinfo.fragments.SystemFragment;
import com.toralabs.deviceinfo.fragments.TestsFragment;
import com.toralabs.deviceinfo.fragments.ThermalFragment;

public class MainAdapter extends FragmentStatePagerAdapter {
    private final Context context;

    public MainAdapter(@NonNull FragmentManager fm, Context context) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.context = context;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new DashboardFragment();
                break;
            case 1:
                fragment = new DeviceFragment();
                break;
            case 2:
                fragment = new SystemFragment();
                break;
            case 3:
                fragment = new CpuFragment();
                break;
            case 4:
                fragment = new BatteryFragment();
                break;
            case 5:
                fragment = new DisplayFragment();
                break;
            case 6:
                fragment = new MemoryFragment();
                break;
            case 7:
                fragment = new CameraFragment();
                break;
            case 8:
                fragment = new ThermalFragment();
                break;
            case 9:
                fragment = new SensorsFragment();
                break;
            case 10:
                fragment = new CodecsFragment();
                break;
            case 11:
                fragment = new InputFragment();
                break;
            case 12:
                fragment = new AppsFragment();
                break;
            case 13:
                fragment = new TestsFragment();
                break;
        }
        assert fragment != null;
        return fragment;
    }

    @Override
    public int getCount() {
        return 14;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        switch (position) {
            case 0:
                title = context.getResources().getString(R.string.dashboard);
                break;
            case 1:
                title = context.getResources().getString(R.string.device);
                break;
            case 2:
                title = context.getResources().getString(R.string.system);
                break;
            case 3:
                title = context.getResources().getString(R.string.cpu);
                break;
            case 4:
                title = context.getResources().getString(R.string.battery);
                break;
            case 5:
                title = context.getResources().getString(R.string.display);
                break;
            case 6:
                title = context.getResources().getString(R.string.memory);
                break;
            case 7:
                title = context.getResources().getString(R.string.camera);
                break;
            case 8:
                title = context.getResources().getString(R.string.thermal);
                break;
            case 9:
                title = context.getResources().getString(R.string.sensors);
                break;
            case 10:
                title = context.getResources().getString(R.string.codecs);
                break;
            case 11:
                title = context.getResources().getString(R.string.input);
                break;
            case 12:
                title = context.getResources().getString(R.string.apps);
                break;
            case 13:
                title = context.getResources().getString(R.string.tests);
                break;
        }
        return title;
    }
}
