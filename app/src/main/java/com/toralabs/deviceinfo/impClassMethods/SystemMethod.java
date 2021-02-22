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
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.media.MediaDrm;
import android.os.Build;

import com.toralabs.deviceinfo.R;
import com.toralabs.deviceinfo.impClasses.BuildInfo;
import com.toralabs.deviceinfo.menuItems.Preferences;
import com.toralabs.deviceinfo.models.SimpleModel;

import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

import static java.lang.System.getProperty;

public class SystemMethod {
    ArrayList<SimpleModel> systemList = new ArrayList<>();
    int logoId;
    String[] wide = new String[9];
    String[] clearkey = new String[2];
    private final Context context;
    String codeName, securityPatch, bootloader, buildNo, baseBand, javaVm, kernel, lang, openGl, rootAccess, seLinux, playServices, uptime, treble, seamlessUpdates;
    int api;
    BuildInfo buildInfo;
    Preferences preferences;
    ConfigurationInfo info;
    ActivityManager activityManager;
    MediaDrm mediaDrm;

    public SystemMethod(Context context) {
        this.context = context;
        buildInfo = new BuildInfo(context);
        preferences = new Preferences(context);
        system();
        drmDetails();
        getAndroidLogo();
    }

    @SuppressLint("DefaultLocale")
    private void system() {
        activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        info = activityManager.getDeviceConfigurationInfo();
        api = Build.VERSION.SDK_INT;
        codeName = buildInfo.getCodeName(api);
        systemList.add(new SimpleModel(context.getString(R.string.codename), codeName));
        systemList.add(new SimpleModel(context.getString(R.string.api), String.valueOf(api)));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            securityPatch = Build.VERSION.SECURITY_PATCH;
            systemList.add(new SimpleModel(context.getString(R.string.sec_patch), securityPatch));
        }
        bootloader = Build.BOOTLOADER;
        buildNo = Build.DISPLAY;
        baseBand = Build.getRadioVersion();
        if (baseBand == null || baseBand.equals(""))
            baseBand = context.getResources().getString(R.string.unknown);
        javaVm = getProperty("java.vm.version");
        kernel = getProperty("os.version");
        lang = Locale.getDefault().getDisplayLanguage();
        openGl = info.getGlEsVersion();
        rootAccess = buildInfo.getRootInfo();
        seLinux = buildInfo.getSeLinuxInfo();
        playServices = buildInfo.getPlayServices();
        uptime = buildInfo.getUpTime();
        treble = buildInfo.getTrebleInfo();
        seamlessUpdates = buildInfo.getSeamlessUpdatesInfo();
        systemList.add(new SimpleModel(context.getString(R.string.bootloader), bootloader));
        systemList.add(new SimpleModel(context.getString(R.string.build_no), buildNo));
        systemList.add(new SimpleModel(context.getString(R.string.baseband), baseBand));
        systemList.add(new SimpleModel(context.getString(R.string.java_vm), javaVm));
        systemList.add(new SimpleModel(context.getString(R.string.kernel), kernel));
        systemList.add(new SimpleModel(context.getString(R.string.lang), lang));
        systemList.add(new SimpleModel(context.getString(R.string.opengl), openGl));
        systemList.add(new SimpleModel(context.getString(R.string.root_access), rootAccess));
        systemList.add(new SimpleModel(context.getString(R.string.se_linux), seLinux));
        systemList.add(new SimpleModel(context.getString(R.string.play_services), playServices));
        systemList.add(new SimpleModel(context.getString(R.string.uptime), uptime));
        systemList.add(new SimpleModel(context.getString(R.string.treble), treble));
        systemList.add(new SimpleModel(context.getString(R.string.updates), seamlessUpdates));
    }

    private void drmDetails() {
        try {
            mediaDrm = new MediaDrm(UUID.fromString("edef8ba9-79d6-4ace-a3c8-27dcd51d21ed"));
            wide[0] = mediaDrm.getPropertyString("vendor");
            wide[1] = mediaDrm.getPropertyString("version");
            wide[2] = mediaDrm.getPropertyString("algorithms");
            wide[3] = mediaDrm.getPropertyString("systemId");
            wide[4] = mediaDrm.getPropertyString("securityLevel");
            wide[5] = mediaDrm.getPropertyString("maxHdcpLevel");
            wide[6] = mediaDrm.getPropertyString("maxNumberOfSessions");
            wide[7] = mediaDrm.getPropertyString("usageReportingSupport");
            wide[8] = mediaDrm.getPropertyString("hdcpLevel");
            mediaDrm.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mediaDrm = new MediaDrm(UUID.fromString("e2719d58-a985-b3c9-781a-b030af78d30e"));
            clearkey[0] = mediaDrm.getPropertyString("vendor");
            clearkey[1] = mediaDrm.getPropertyString("version");
            mediaDrm.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getAndroidLogo() {
        if (Build.VERSION.SDK_INT < 23) {
            logoId = R.drawable.lollipop;
        } else if (Build.VERSION.SDK_INT < 24) {
            logoId = R.drawable.marshmellow;
        } else if (Build.VERSION.SDK_INT < 26) {
            logoId = R.drawable.nougat;
        } else if (Build.VERSION.SDK_INT < 28) {
            logoId = R.drawable.oreo;
        } else if (Build.VERSION.SDK_INT < 29) {
            logoId = R.drawable.pie;
        } else if (Build.VERSION.SDK_INT < 30) {
            logoId = R.drawable.q;
        } else if (Build.VERSION.SDK_INT < 31) {
            logoId = R.drawable.r;
        }
    }

    public ArrayList<SimpleModel> getSystemList() {
        return systemList;
    }

    public String[] getWide() {
        return wide;
    }

    public String[] getClearkey() {
        return clearkey;
    }

    public int getLogoId() {
        return logoId;
    }

}
