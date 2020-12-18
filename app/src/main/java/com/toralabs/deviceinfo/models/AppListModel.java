package com.toralabs.deviceinfo.models;

import android.graphics.drawable.Drawable;

import java.io.File;

public class AppListModel {
    private final Drawable icon;
    private final String name;
    private final String packageName;
    private final File file;
    private final String size;
    private final String version;
    private final String targetsdk;
    private final String minsdk;
    private final String uid;
    private final int flag;
    private final String permissions;

    public AppListModel(Drawable icon, String name, String packageName, File file, String size, int flag, String version, String targetsdk, String minsdk, String uid, String permissions) {
        this.icon = icon;
        this.name = name;
        this.packageName = packageName;
        this.size = size;
        this.version = version;
        this.targetsdk = targetsdk;
        this.minsdk = minsdk;
        this.uid = uid;
        this.flag = flag;
        this.file = file;
        this.permissions = permissions;
    }

    public Drawable getIcon() {
        return icon;
    }

    public File getFile() {
        return file;
    }

    public String getName() {
        return name;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getSize() {
        return size;
    }

    public int getFlag() {
        return flag;
    }

    public String getVersion() {
        return version;
    }

    public String getTargetsdk() {
        return targetsdk;
    }

    public String getMinsdk() {
        return minsdk;
    }

    public String getUid() {
        return uid;
    }

    public String getPermissions() {
        return permissions;
    }
}
