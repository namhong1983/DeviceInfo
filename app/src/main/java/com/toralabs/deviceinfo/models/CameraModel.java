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
