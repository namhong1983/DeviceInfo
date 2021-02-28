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
package com.toralabs.deviceinfo.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by @mrudultora
 */

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
