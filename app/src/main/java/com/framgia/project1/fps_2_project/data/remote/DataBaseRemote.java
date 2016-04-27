package com.framgia.project1.fps_2_project.data.remote;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.framgia.project1.fps_2_project.data.local.DataBaseHelper;
import com.framgia.project1.fps_2_project.data.model.Constant;
import com.framgia.project1.fps_2_project.data.model.Image;
import com.framgia.project1.fps_2_project.data.model.Video;

import java.sql.SQLDataException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nguyenxuantung on 20/04/2016.
 */
public class DataBaseRemote implements Constant {
    private SQLiteDatabase mDataBase;
    private DataBaseHelper mHelper;

    public DataBaseRemote(Context context) {
        mHelper = new DataBaseHelper(context);
    }

    public void openDataBase() throws SQLException {
        mDataBase = mHelper.getWritableDatabase();
    }

    public void closeDataBase() throws SQLException {
        mDataBase.close();
    }

    //insert data into table image
    public long insertImage(Image image) throws SQLDataException {
        long result = -1;
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_IMAGE_NAME, image.getImageName());
        contentValues.put(COLUMN_IMAGE_PATH, image.getImagePath());
        contentValues.put(COLUMN_IMAGE_LIKE, image.getImageLike());
        result = mDataBase.insertOrThrow(TABLE_IMAGE, null, contentValues);
        return result;
    }

    //get an image from list
    public Cursor searchImage(int id) {
        String query = COLUMN_ID + " = " + id;
        Cursor cursor = null;
        cursor = mDataBase.query(true, TABLE_IMAGE, null, query, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    // get list of image
    public List<Image> getImageTopList() {
        List<Image> ImageList = new ArrayList<>();
        String orderBy = COLUMN_IMAGE_LIKE + "DESC";
        Cursor cursor = null;
        cursor = mDataBase.query(true, TABLE_IMAGE, null, null, null, null, null, orderBy, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                ImageList.add(new Image(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        }
        return ImageList;
    }

    //insert data into table video
    public long insertVideo(Video video) throws SQLDataException {
        long result = -1;
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_VIDEO_NAME, video.getVideoName());
        contentValues.put(COLUMN_VIDEO_PATH, video.getVideoPath());
        result = mDataBase.insertOrThrow(TABLE_VIDEO, null, contentValues);
        return result;
    }

    //get an video from list
    public Cursor searchVideo(int id) {
        String query = COLUMN_ID + " = " + id;
        Cursor cursor = null;
        cursor = mDataBase.query(true, TABLE_VIDEO, null, query, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public boolean searchVideo(String videoName) {
        String query = COLUMN_VIDEO_NAME + " = '" + videoName + "'";
        Cursor cursor = mDataBase.query(TABLE_VIDEO, null, query, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            cursor.close();
            return true;
        }
        return false;
    }
}
