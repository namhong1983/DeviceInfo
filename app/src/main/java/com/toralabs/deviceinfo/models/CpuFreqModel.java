package com.toralabs.deviceinfo.models;

import android.os.Parcel;
import android.os.Parcelable;

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
