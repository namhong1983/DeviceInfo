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
