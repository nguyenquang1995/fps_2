package com.framgia.project1.fps_2_project.data.model;

/**
 * Created by nguyenxuantung on 06/05/2016.
 */
public class EffectItem {
    // Getter and Setter model for recycler view items
    private String mTitle;
    private int mImage;

    public EffectItem(String mTitle, int mImage) {
        this.mTitle = mTitle;
        this.mImage = mImage;
    }

    public String getTitle() {
        return mTitle;
    }

    public int getImage() {
        return mImage;
    }
}
