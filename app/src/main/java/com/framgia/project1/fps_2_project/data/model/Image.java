package com.framgia.project1.fps_2_project.data.model;

import android.database.Cursor;

import com.framgia.project1.fps_2_project.data.model.Constant;

/**
 * Created by nguyenxuantung on 20/04/2016.
 */
public class Image implements Constant {
    private int mImageId;
    private String mImageName;
    private String mImagePath;
    private int mImageLike;

    public Image(String mImageName, String mImagePath, int mImageLike, int mImageShare) {
        this.mImageName = mImageName;
        this.mImagePath = mImagePath;
        this.mImageLike = mImageLike;
    }

    public Image(Cursor cursor){
        this.mImageName=cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_NAME));
        this.mImagePath=cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_PATH));
        this.mImageLike=cursor.getInt(cursor.getColumnIndex(COLUMN_IMAGE_LIKE));
    }
    public int getImageId() {
        return mImageId;
    }

    public void setImageId(int mImageId) {
        this.mImageId = mImageId;
    }

    public String getImageName() {
        return mImageName;
    }

    public void setImageName(String mImageName) {
        this.mImageName = mImageName;
    }

    public String getImagePath() {
        return mImagePath;
    }

    public void setImagePath(String mImagePath) {
        this.mImagePath = mImagePath;
    }

    public int getImageLike() {
        return mImageLike;
    }

    public void setImageLike(int mImageLike) {
        this.mImageLike = mImageLike;
    }

}
