package com.framgia.project1.fps_2_project.data.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hacks_000 on 4/19/2016.
 */
public class PhotoModel implements Parcelable {
    private int mId;
    private String mPath;
    private boolean mIsSelected;

    public PhotoModel(int id, String path) {
        mId = id;
        mPath = path;
        mIsSelected = false;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        mPath = path;
    }

    public int getId() {
        return mId;
    }

    void setId(int id) {
        mId = id;
    }

    public void setSelected(boolean isSelected) {
        mIsSelected = isSelected;
    }

    public boolean isSelected() {
        return mIsSelected;
    }

    protected PhotoModel(Parcel in) {
        mId = in.readInt();
        mPath = in.readString();
        mIsSelected = in.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mPath);
        dest.writeByte((byte) (mIsSelected ? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<PhotoModel> CREATOR =
        new Parcelable.Creator<PhotoModel>() {
            @Override
            public PhotoModel createFromParcel(Parcel in) {
                return new PhotoModel(in);
            }

            @Override
            public PhotoModel[] newArray(int size) {
                return new PhotoModel[size];
            }
        };
}