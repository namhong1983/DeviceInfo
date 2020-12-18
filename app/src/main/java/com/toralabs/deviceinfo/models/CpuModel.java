package com.toralabs.deviceinfo.models;

import android.os.Parcel;
import android.os.Parcelable;

public class CpuModel implements Parcelable {
    private final String title;
    private final String desc;
    private final String tag;

    public CpuModel(String title, String desc, String tag) {
        this.title = title;
        this.desc = desc;
        this.tag = tag;
    }

    protected CpuModel(Parcel in) {
        title = in.readString();
        desc = in.readString();
        tag = in.readString();
    }

    public static final Creator<CpuModel> CREATOR = new Creator<CpuModel>() {
        @Override
        public CpuModel createFromParcel(Parcel in) {
            return new CpuModel(in);
        }

        @Override
        public CpuModel[] newArray(int size) {
            return new CpuModel[size];
        }
    };

    public String getTag() {
        return tag;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(desc);
        dest.writeString(tag);
    }
}
