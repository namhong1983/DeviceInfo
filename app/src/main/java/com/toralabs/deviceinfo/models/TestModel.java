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

public class TestModel implements Parcelable {
    private final int imgMain;
    private int imgStatus;
    private final String testName;
    String tag;

    public TestModel(int imgMain, String testName, int imgStatus, String tag) {
        this.imgMain = imgMain;
        this.imgStatus = imgStatus;
        this.testName = testName;
        this.tag = tag;
    }

    protected TestModel(Parcel in) {
        imgMain = in.readInt();
        imgStatus = in.readInt();
        testName = in.readString();
        tag = in.readString();
    }

    public static final Creator<TestModel> CREATOR = new Creator<TestModel>() {
        @Override
        public TestModel createFromParcel(Parcel in) {
            return new TestModel(in);
        }

        @Override
        public TestModel[] newArray(int size) {
            return new TestModel[size];
        }
    };

    public int getImgMain() {
        return imgMain;
    }

    public int getImgStatus() {
        return imgStatus;
    }

    public String getTestName() {
        return testName;
    }

    public String getTag() {
        return tag;
    }

    public void setImgStatus(int imgStatus) {
        this.imgStatus = imgStatus;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(imgMain);
        dest.writeInt(imgStatus);
        dest.writeString(testName);
        dest.writeString(tag);
    }
}
