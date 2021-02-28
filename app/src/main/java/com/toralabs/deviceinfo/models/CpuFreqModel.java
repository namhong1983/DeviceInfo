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

public class CpuFreqModel implements Parcelable {
    private final String coreNo;
    private final String cpuFreq;

    public CpuFreqModel(String coreNo, String cpuFreq) {
        this.coreNo = coreNo;
        this.cpuFreq = cpuFreq;
    }

    protected CpuFreqModel(Parcel in) {
        coreNo = in.readString();
        cpuFreq = in.readString();
    }

    public static final Creator<CpuFreqModel> CREATOR = new Creator<CpuFreqModel>() {
        @Override
        public CpuFreqModel createFromParcel(Parcel in) {
            return new CpuFreqModel(in);
        }

        @Override
        public CpuFreqModel[] newArray(int size) {
            return new CpuFreqModel[size];
        }
    };

    public String getCoreNo() {
        return coreNo;
    }

    public String getCpuFreq() {
        return cpuFreq;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(coreNo);
        dest.writeString(cpuFreq);
    }
}
