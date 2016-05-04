package com.framgia.project1.fps_2_project.data.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.framgia.project1.fps_2_project.data.model.Constant;

/**
 * Created by nguyenxuantung on 20/04/2016.
 */
public class DataBaseHelper extends SQLiteOpenHelper implements Constant {
    private static final String DATABASE_NAME = "MediaManager.db"; // name of database
    private static final int DATABASE_VERSION = 1; // database version
    //create table Image
    private static final String CREATE_TABLE_IMAGE= "create table " + TABLE_IMAGE + " ( "
        + COLUMN_ID + " integer primary key autoincrement,"
        + COLUMN_IMAGE_NAME + " text,"
        + COLUMN_IMAGE_PATH + " text,"
        + COLUMN_IMAGE_LIKE+ " integer)";
    //create table Video
    private static final String CREATE_TABLE_VIDEO= "create table " + TABLE_VIDEO+ " ( "
        + COLUMN_ID + " integer primary key autoincrement,"
        + COLUMN_VIDEO_NAME + " text,"
        + COLUMN_VIDEO_PATH + " text)";
    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_IMAGE);
        db.execSQL(CREATE_TABLE_VIDEO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_IMAGE);
        db.execSQL("drop table if exists " + TABLE_VIDEO);
        onCreate(db);
    }
}
