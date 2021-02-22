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
package com.toralabs.deviceinfo.impClassMethods;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Display;

import com.toralabs.deviceinfo.R;
import com.toralabs.deviceinfo.impClasses.BuildInfo;
import com.toralabs.deviceinfo.models.SimpleModel;

import java.util.ArrayList;

public class DisplayMethod {
    private final Context context;
    ArrayList<SimpleModel> displayList = new ArrayList<>();
    BuildInfo buildInfo;
    String density, fontScale, size, refreshRate, hdr, hdrCapable, brightnessLevel, brightnessMode, timeout, orientation;
    StringBuilder str;

    public DisplayMethod(Context context) {
        this.context = context;
        display();
    }

    @SuppressLint("DefaultLocale")
    private void display() {
        buildInfo = new BuildInfo(context);
        int h, w, dpi;
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        h = metrics.heightPixels;
        w = metrics.widthPixels;
        dpi = metrics.densityDpi;
        refreshRate = String.format("%.1f", ((Activity) context).getWindowManager().getDefaultDisplay().getRefreshRate()) + " Hz";
        int brightness = 0, time = 0;
        float font = 0.0f;
        try {
            brightness = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            time = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT);
            font = context.getResources().getConfiguration().fontScale;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        brightnessLevel = (int) ((brightness * 100) / 255) + " %";
        timeout = (time / 1000) + " " + context.getResources().getString(R.string.seconds);
        fontScale = String.valueOf(font);
        hdr = context.getResources().getString(R.string.supported);
        hdrCapable = context.getResources().getString(R.string.none);
        str = new StringBuilder();
        density = buildInfo.getDensityDpi();
        size = buildInfo.getScreenSize();
        displayList.add(new SimpleModel(context.getResources().getString(R.string.resol), w + " x " + h + " " + context.getResources().getString(R.string.pixel)));
        displayList.add(new SimpleModel(context.getResources().getString(R.string.density), dpi + " dpi" + density));
        displayList.add(new SimpleModel(context.getResources().getString(R.string.fontscale), fontScale));
        displayList.add(new SimpleModel(context.getResources().getString(R.string.size), size));
        displayList.add(new SimpleModel(context.getResources().getString(R.string.ref_rate), refreshRate));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Display.HdrCapabilities hdrCapabilities = ((Activity)context).getWindowManager().getDefaultDisplay().getHdrCapabilities();
            if (hdrCapabilities.getSupportedHdrTypes().length == 0) {
                hdr = context.getResources().getString(R.string.not_supported);
            } else {
                int[] hdrtypes = hdrCapabilities.getSupportedHdrTypes();
                for (int hdrtype : hdrtypes) {
                    switch (hdrtype) {
                        case 1:
                            str.append("Dolby Vision HDR\n");
                            break;
                        case 2:
                            str.append("HDR10\n");
                            break;
                        case 3:
                            str.append("Hybrid Log-Gamma HDR\n");
                            break;
                        case 4:
                            str.append("HDR10+\n");
                            break;
                    }
                }
            }
            if (!str.toString().equals(""))
                hdrCapable = str.toString().substring(0, str.toString().length() - 1);
        }
        brightnessMode = buildInfo.getBrightnessMode();
        orientation = buildInfo.getOrientation();
        displayList.add(new SimpleModel(context.getResources().getString(R.string.hdr), hdr));
        displayList.add(new SimpleModel(context.getResources().getString(R.string.hdr_cap), hdrCapable));
        displayList.add(new SimpleModel(context.getResources().getString(R.string.brightness), brightnessLevel));
        displayList.add(new SimpleModel(context.getResources().getString(R.string.bright_mode), brightnessMode));
        displayList.add(new SimpleModel(context.getResources().getString(R.string.timeout), timeout));
        displayList.add(new SimpleModel(context.getResources().getString(R.string.orientation), orientation));
    }

    public ArrayList<SimpleModel> getDisplayList() {
        return displayList;
    }
}
