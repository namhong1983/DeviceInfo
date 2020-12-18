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
