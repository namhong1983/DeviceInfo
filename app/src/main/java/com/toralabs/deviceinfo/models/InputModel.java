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

public class InputModel implements Parcelable {
    private final String name;
    private final String desc;
    private final String vendorId;
    private final String proId;
    private final String hasVibrator;
    private final String keyboardType;
    private final String deviceId;
    private final String sources;
    private final String axis;
    private final String range;
    private final String flat;
    private final String fuzz;
    private final String resol;
    private final String source;
    private final boolean hasMotionRange;

    public InputModel(String name, String desc, String vendorId, String proId, String hasVibrator, String keyboardType, String deviceId, String sources, String axis, String range, String flat, String fuzz, String resol, String source, boolean hasMotionRange) {
        this.name = name;
        this.desc = desc;
        this.vendorId = vendorId;
        this.proId = proId;
        this.hasVibrator = hasVibrator;
        this.keyboardType = keyboardType;
        this.deviceId = deviceId;
        this.sources = sources;
        this.axis = axis;
        this.range = range;
        this.flat = flat;
        this.fuzz = fuzz;
        this.resol = resol;
        this.source = source;
        this.hasMotionRange = hasMotionRange;
    }

    protected InputModel(Parcel in) {
        name = in.readString();
        desc = in.readString();
        vendorId = in.readString();
        proId = in.readString();
        hasVibrator = in.readString();
        keyboardType = in.readString();
        deviceId = in.readString();
        sources = in.readString();
        axis = in.readString();
        range = in.readString();
        flat = in.readString();
        fuzz = in.readString();
        resol = in.readString();
        source = in.readString();
        hasMotionRange = in.readByte() == 1;
    }

    public static final Creator<InputModel> CREATOR = new Creator<InputModel>() {
        @Override
        public InputModel createFromParcel(Parcel in) {
            return new InputModel(in);
        }

        @Override
        public InputModel[] newArray(int size) {
            return new InputModel[size];
        }
    };

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public String getVendorId() {
        return vendorId;
    }

    public String getProId() {
        return proId;
    }

    public String getHasVibrator() {
        return hasVibrator;
    }

    public String getKeyboardType() {
        return keyboardType;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getSources() {
        return sources;
    }

    public String getAxis() {
        return axis;
    }

    public String getRange() {
        return range;
    }

    public String getFlat() {
        return flat;
    }

    public String getFuzz() {
        return fuzz;
    }

    public String getResol() {
        return resol;
    }

    public String getSource() {
        return source;
    }

    public boolean isHasMotionRange() {
        return hasMotionRange;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(desc);
        dest.writeString(vendorId);
        dest.writeString(proId);
        dest.writeString(hasVibrator);
        dest.writeString(keyboardType);
        dest.writeString(deviceId);
        dest.writeString(sources);
        dest.writeString(axis);
        dest.writeString(range);
        dest.writeString(flat);
        dest.writeString(fuzz);
        dest.writeString(resol);
        dest.writeString(source);
        dest.writeByte((byte) (hasMotionRange ? 1 : 0));
    }
}
