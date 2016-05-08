package com.framgia.project1.fps_2_project.ui.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;

import com.framgia.project1.fps_2_project.R;
import com.framgia.project1.fps_2_project.data.model.PhotoModel;
import com.framgia.project1.fps_2_project.ui.adapter.PhotoAdapter;

import java.util.ArrayList;

public class ChoosePhotoActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_READ_EXTERNAL_STORAGE = 1;
    private GridView mGridViewPhoto;
    private Toolbar mToolbar;
    private PhotoAdapter mPhotoAdapter;
    private ArrayList<PhotoModel> mListImageAll = new ArrayList();
    private boolean mHasReadExternalPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_photo);
        checkPermission();
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(ChoosePhotoActivity.this, Manifest.permission
            .READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            findView();
            return;
        }
        if (ContextCompat.checkSelfPermission(ChoosePhotoActivity.this, Manifest.permission
            .READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ChoosePhotoActivity.this, new String[]
                    {Manifest.permission.READ_EXTERNAL_STORAGE},
                MY_PERMISSIONS_READ_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_READ_EXTERNAL_STORAGE
            && grantResults.length > 0
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mHasReadExternalPermission = true;
            findView();
            return;
        }
        if (requestCode == MY_PERMISSIONS_READ_EXTERNAL_STORAGE) {
            mHasReadExternalPermission = false;
            finish();
        }
    }

    private void findView() {
        mGridViewPhoto = (GridView) findViewById(R.id.grid_images);
        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        mToolbar.setTitle(getString(R.string.choose_photos_title));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
        final String orderBy = MediaStore.Images.Media._ID;
        Cursor imageCusor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            columns, null, null, orderBy);
        int idColumnIndex = imageCusor.getColumnIndex(MediaStore.Images.Media._ID);
        int dataColumnIndex = imageCusor.getColumnIndex(MediaStore.Images.Media.DATA);
        int count = imageCusor.getCount();
        for (int i = 0; i < count; i++) {
            imageCusor.moveToPosition(i);
            Bitmap bitmap = MediaStore.Images.Thumbnails
                .getThumbnail(getContentResolver(), imageCusor.getInt(idColumnIndex),
                    MediaStore.Images.Thumbnails.MICRO_KIND,
                    null);
            mListImageAll.add(new PhotoModel(imageCusor.getInt(idColumnIndex),
                imageCusor.getString(dataColumnIndex), bitmap));
        }
        mPhotoAdapter = new PhotoAdapter(ChoosePhotoActivity.this, mListImageAll);
        mGridViewPhoto.setAdapter(mPhotoAdapter);
        imageCusor.close();
    }

    private ArrayList getListPhotoSelected() {
        ArrayList photoSelected = new ArrayList();
        int count = mListImageAll.size();
        for (int i = 0; i < count; i++) {
            if (mListImageAll.get(i).isSelected()) {
                photoSelected.add(mListImageAll.get(i));
            }
        }
        return photoSelected;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_choose_photo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}