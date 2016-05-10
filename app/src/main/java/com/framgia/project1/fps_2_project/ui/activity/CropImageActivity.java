package com.framgia.project1.fps_2_project.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.framgia.project1.fps_2_project.R;
import com.framgia.project1.fps_2_project.data.model.Constant;
import com.framgia.project1.fps_2_project.data.model.EffectItem;
import com.framgia.project1.fps_2_project.ui.adapter.MyRecycleViewAdapter;
import com.framgia.project1.fps_2_project.ui.fragment.MainFragment;
import com.framgia.project1.fps_2_project.ui.mylistener.MyOnClickListener;
import com.framgia.project1.fps_2_project.ui.widget.CropDemoPreset;
import com.framgia.project1.fps_2_project.ui.widget.CropImageViewOptions;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;

public class CropImageActivity extends AppCompatActivity implements MyOnClickListener, Constant {
    private ActionBar mActionBar;
    private String[] mTITLES;
    private MainFragment mCurrentFragment;
    private Uri mCropImageUri;
    private CropImageViewOptions mCropImageViewOptions = new CropImageViewOptions();

    public void setCurrentFragment(MainFragment fragment) {
        mCurrentFragment = fragment;
    }

    public void setCurrentOptions(CropImageViewOptions options) {
        mCropImageViewOptions = options;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);
        mActionBar = getSupportActionBar();
        mTITLES = this.getResources().getStringArray(R.array.list_option);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        initViews();
        if (savedInstanceState == null) {
            setMainFragmentByPreset(CropDemoPreset.RECT);
        }
    }

    private void initViews() {
        RecyclerView recyclerView = (RecyclerView)
            findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        ArrayList<EffectItem> arrayList = new ArrayList<>();
        for (int i = 0; i < mTITLES.length; i++) {
            arrayList.add(new EffectItem(mTITLES[i], R.drawable.crop));
        }
        MyRecycleViewAdapter adapter = new MyRecycleViewAdapter(CropImageActivity.this, arrayList);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);// set adapter on recyclerview
        recyclerView
            .setLayoutManager(
                new LinearLayoutManager(CropImageActivity.this, LinearLayoutManager.HORIZONTAL,
                    false));
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mCurrentFragment.updateCurrentCropViewOptions();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mCurrentFragment != null && mCurrentFragment.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    @SuppressLint("NewApi")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE &&
            resultCode == Activity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);
            boolean requirePermissions = false;
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                requirePermissions = true;
                mCropImageUri = imageUri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            } else {
                mCurrentFragment.setImageUri(imageUri);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        if (mCropImageUri != null && grantResults.length > 0 &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mCurrentFragment.setImageUri(mCropImageUri);
        } else {
            Toast.makeText(this, getString(R.string.notice_permissions),
                Toast.LENGTH_LONG).show();
        }
    }

    private void setMainFragmentByPreset(CropDemoPreset demoPreset) {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
            .replace(R.id.container, MainFragment.newInstance(demoPreset, this))
            .commit();
    }

    @Override
    public void onItemClick(View view, int position) {
        switch (position) {
            case CIRCULAR_CONS:
                setMainFragmentByPreset(CropDemoPreset.CIRCULAR);
                break;
            case RECT_CONS:
                setMainFragmentByPreset(CropDemoPreset.RECT);
                break;
            case MIN_MAX_OVERRIDE_CONS:
                setMainFragmentByPreset(CropDemoPreset.MIN_MAX_OVERRIDE);
                break;
            case CUSTOMIZED_OVERLAY_CONS:
                setMainFragmentByPreset(CropDemoPreset.CUSTOMIZED_OVERLAY);
                break;
            case GUIDE_LINES_CONS:
                mCropImageViewOptions.guidelines =
                    mCropImageViewOptions.guidelines == CropImageView.Guidelines.OFF
                        ? CropImageView.Guidelines.ON :
                        mCropImageViewOptions.guidelines == CropImageView.Guidelines.ON
                            ? CropImageView.Guidelines.ON_TOUCH : CropImageView.Guidelines.OFF;
                mCurrentFragment.setCropImageViewOptions(mCropImageViewOptions);
                break;
            case SCALE_TYPE_CONS:
                mCropImageViewOptions.cropShape =
                    mCropImageViewOptions.cropShape == CropImageView.CropShape.RECTANGLE
                        ? CropImageView.CropShape.OVAL : CropImageView.CropShape.RECTANGLE;
                mCurrentFragment.setCropImageViewOptions(mCropImageViewOptions);
                break;
            case FIX_ASPECT_RATIO_CONS:
                if (!mCropImageViewOptions.fixAspectRatio) {
                    mCropImageViewOptions.fixAspectRatio = true;
                    mCropImageViewOptions.aspectRatio = new Pair<>(1, 1);
                } else {
                    if (mCropImageViewOptions.aspectRatio.first == 1 &&
                        mCropImageViewOptions.aspectRatio.second == 1) {
                        mCropImageViewOptions.aspectRatio = new Pair<>(4, 3);
                    } else if (mCropImageViewOptions.aspectRatio.first == 4 &&
                        mCropImageViewOptions.aspectRatio.second == 3) {
                        mCropImageViewOptions.aspectRatio = new Pair<>(16, 9);
                    } else if (mCropImageViewOptions.aspectRatio.first == 16 &&
                        mCropImageViewOptions.aspectRatio.second == 9) {
                        mCropImageViewOptions.aspectRatio = new Pair<>(9, 16);
                    } else {
                        mCropImageViewOptions.fixAspectRatio = false;
                    }
                }
                mCurrentFragment.setCropImageViewOptions(mCropImageViewOptions);
                break;
            case AUTO_ZOOM_CONS:
                mCropImageViewOptions.autoZoomEnabled = !mCropImageViewOptions.autoZoomEnabled;
                mCurrentFragment.setCropImageViewOptions(mCropImageViewOptions);
                break;
            case MAX_ZOOM_CONS:
                mCropImageViewOptions.maxZoomLevel = mCropImageViewOptions.maxZoomLevel == 4 ? 8
                    : mCropImageViewOptions.maxZoomLevel == 8 ? 2 : 4;
                mCurrentFragment.setCropImageViewOptions(mCropImageViewOptions);
                break;
            case INIT_CROP_CONS:
                mCurrentFragment.setInitialCropRect();
                break;
            case RESET_CROP_CONS:
                mCurrentFragment.resetCropRect();
                break;
            case OVER_lAY_CONS:
                mCropImageViewOptions.showCropOverlay = !mCropImageViewOptions.showCropOverlay;
                mCurrentFragment.setCropImageViewOptions(mCropImageViewOptions);
                break;
            default:
                break;
        }
    }
}