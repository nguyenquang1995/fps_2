package com.framgia.project1.fps_2_project.data.model;

import android.database.Cursor;

import com.framgia.project1.fps_2_project.data.model.Constant;

/**
 * Created by nguyenxuantung on 20/04/2016.
 */
public class Video implements Constant {
    private int mVideoId;
    private String mVideoName;
    private String mVideoPath;

    public Video(String mVideoName, String mVideoPath) {
        this.mVideoName = mVideoName;
        this.mVideoPath = mVideoPath;
    }
    public Video(Cursor cursor){
        this.mVideoName=cursor.getString(cursor.getColumnIndex(COLUMN_VIDEO_NAME));
        this.mVideoPath=cursor.getString(cursor.getColumnIndex(COLUMN_VIDEO_PATH));
}
    public int getVideoId() {
        return mVideoId;
    }

    public void setVideoId(int mVideoId) {
        this.mVideoId = mVideoId;
    }

    public String getVideoName() {
        return mVideoName;
    }

    public void setVideoName(String mVideoName) {
        this.mVideoName = mVideoName;
    }

    public String getVideoPath() {
        return mVideoPath;
    }

    public void setVideoPath(String mVideoPath) {
        this.mVideoPath = mVideoPath;
    }
}
