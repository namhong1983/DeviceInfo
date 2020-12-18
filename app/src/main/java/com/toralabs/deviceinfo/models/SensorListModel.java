package com.toralabs.deviceinfo.models;

import android.os.Parcel;
import android.os.Parcelable;

public class SensorListModel implements Parcelable {
    private final int image;
    private final String name;
    private final String vendor;
    private final String type;
    private final String power;

    public SensorListModel(int image, String name, String vendor, String type, String power) {
        this.image = image;
        this.name = name;
        this.vendor = vendor;
        this.type = type;
        this.power = power;
    }


    protected SensorListModel(Parcel in) {
        image = in.readInt();
        name = in.readString();
        vendor = in.readString();
        type = in.readString();
        power = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(image);
        dest.writeString(name);
        dest.writeString(vendor);
        dest.writeString(type);
        dest.writeString(power);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SensorListModel> CREATOR = new Creator<SensorListModel>() {
        @Override
        public SensorListModel createFromParcel(Parcel in) {
            return new SensorListModel(in);
        }

        @Override
        public SensorListModel[] newArray(int size) {
            return new SensorListModel[size];
        }
    };

    public int getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getVendor() {
        return vendor;
    }

    public String getType() {
        return type;
    }

    public String getPower() {
        return power;
    }

}
