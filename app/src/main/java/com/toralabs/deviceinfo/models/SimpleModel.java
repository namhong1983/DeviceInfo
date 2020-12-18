package com.toralabs.deviceinfo.models;

import android.os.Parcel;
import android.os.Parcelable;

public class SimpleModel implements Parcelable {
    private final String title;
    private final String desc;

    public SimpleModel(String title, String desc) {
        this.title = title;
        this.desc = desc;
    }

    protected SimpleModel(Parcel in) {
        title = in.readString();
        desc = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(desc);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SimpleModel> CREATOR = new Creator<SimpleModel>() {
        @Override
        public SimpleModel createFromParcel(Parcel in) {
            return new SimpleModel(in);
        }

        @Override
        public SimpleModel[] newArray(int size) {
            return new SimpleModel[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

}
