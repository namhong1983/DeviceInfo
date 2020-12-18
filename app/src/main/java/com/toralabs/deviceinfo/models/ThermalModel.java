package com.toralabs.deviceinfo.models;

import android.os.Parcel;
import android.os.Parcelable;

public class ThermalModel implements Parcelable {
    private final String temp;
    private final String type;

    public ThermalModel(String temp, String type) {
        this.temp = temp;
        this.type = type;
    }

    protected ThermalModel(Parcel in) {
        temp = in.readString();
        type = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(temp);
        dest.writeString(type);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ThermalModel> CREATOR = new Creator<ThermalModel>() {
        @Override
        public ThermalModel createFromParcel(Parcel in) {
            return new ThermalModel(in);
        }

        @Override
        public ThermalModel[] newArray(int size) {
            return new ThermalModel[size];
        }
    };

    public String getTemp() {
        return temp;
    }

    public String getType() {
        return type;
    }
}
