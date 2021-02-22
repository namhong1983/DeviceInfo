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

public class SimpleModel implements Parcelable {
    private final String title;
    private final String desc;

    public SimpleModel(String title, String desc) {
        this.title = title;
        this.desc = desc;
    }

    protected SimpleModel(Parcel in) {
        title = in.readString();
        desc = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(desc);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SimpleModel> CREATOR = new Creator<SimpleModel>() {
        @Override
        public SimpleModel createFromParcel(Parcel in) {
            return new SimpleModel(in);
        }

        @Override
        public SimpleModel[] newArray(int size) {
            return new SimpleModel[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

}
