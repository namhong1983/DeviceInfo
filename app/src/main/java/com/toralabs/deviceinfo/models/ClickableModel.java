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

public class ClickableModel implements Parcelable {
    private final String title;
    private final String desc;
    private final boolean click;

    public ClickableModel(String title, String desc, boolean click) {
        this.title = title;
        this.desc = desc;
        this.click = click;
    }

    protected ClickableModel(Parcel in) {
        title = in.readString();
        desc = in.readString();
        click = in.readByte() == 1;
    }

    public static final Creator<ClickableModel> CREATOR = new Creator<ClickableModel>() {
        @Override
        public ClickableModel createFromParcel(Parcel in) {
            return new ClickableModel(in);
        }

        @Override
        public ClickableModel[] newArray(int size) {
            return new ClickableModel[size];
        }
    };

    public boolean getClick() {
        return click;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(desc);
        dest.writeByte((byte) (click ? 1 : 0));
    }
}
