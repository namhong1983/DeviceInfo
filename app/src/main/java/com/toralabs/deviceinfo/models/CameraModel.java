package com.toralabs.deviceinfo.models;

import android.os.Parcel;
import android.os.Parcelable;

public class CameraModel implements Parcelable {
    String title;
    String txtPixel;
    String txtFocal;
    boolean selected;

    public CameraModel(String title, String txtPixel, String txtFocal, boolean selected) {
        this.title = title;
        this.txtPixel = txtPixel;
        this.txtFocal = txtFocal;
        this.selected = selected;
    }

    protected CameraModel(Parcel in) {
        title = in.readString();
        txtPixel = in.readString();
        txtFocal = in.readString();
        selected = in.readByte() == 1;
    }

    public static final Creator<CameraModel> CREATOR = new Creator<CameraModel>() {
        @Override
        public CameraModel createFromParcel(Parcel in) {
            return new CameraModel(in);
        }

        @Override
        public CameraModel[] newArray(int size) {
            return new CameraModel[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public String getTxtPixel() {
        return txtPixel;
    }

    public String getTxtFocal() {
        return txtFocal;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(txtPixel);
        dest.writeString(txtFocal);
        dest.writeByte((byte) (selected ? 1 : 0));
    }
}
