package com.framgia.project1.fps_2_project.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.framgia.project1.fps_2_project.R;
import com.framgia.project1.fps_2_project.ui.widget.ActiveImageView;
import com.framgia.project1.fps_2_project.util.Constant;
import com.framgia.project1.fps_2_project.util.ImageUtils;
import com.framgia.project1.fps_2_project.util.TransformUtil;

public class MergeImageActivity extends AppCompatActivity {
    private static final String PICK_IMAGE_TYPE = "image/*";
    private static final String PICK_IMAGE_TITLE = "Select Picture";
    private static final int PICK_IMAGE_REQUEST_CODE = 1;
    private static final int MY_PERMISSIONS_READ_EXTERNAL_STORAGE = 1;
    private ImageView mImageBackground;
    private ActiveImageView mSubImageView;
    private Bitmap mBitmapBackground;
    private Bitmap mSubBitmap;
    private boolean mHasReadExternalPermission;
    private FrameLayout mFrameLayout;
    private ImageButton mButtonSave;
    private ImageButton mButtonCrop;
    private ImageButton mButtonAddImage;
    private ImageButton mButtonTransform;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merge_image);
        init();
        findView();
    }

    private void init() {
        int imageId = getIntent().getIntExtra(Constant.INTENT_DATA, 0);
        mBitmapBackground = ImageUtils.decodeSampleBitmapFromResource(getResources(), imageId,
            ChooseFrameActivity.IMAGE_SIZE, ChooseFrameActivity.IMAGE_SIZE);
    }

    private void findView() {
        mImageBackground = (ImageView) findViewById(R.id.background_image);
        mImageBackground.setImageBitmap(mBitmapBackground);
        mSubImageView = (ActiveImageView) findViewById(R.id.sub_image);
        mFrameLayout = (FrameLayout) findViewById(R.id.image_result);
        mButtonSave = (ImageButton) findViewById(R.id.button_save);
        mButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSubBitmap != null) {
                    mFrameLayout.setDrawingCacheEnabled(true);
                    mFrameLayout.buildDrawingCache();
                    mBitmapBackground = Bitmap.createBitmap(mFrameLayout.getDrawingCache());
                    mImageBackground.setImageBitmap(mBitmapBackground);
                    mFrameLayout.setDrawingCacheEnabled(false);
                }
            }
        });
        mButtonAddImage = (ImageButton) findViewById(R.id.button_add_image);
        mButtonAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionAndPickImage();
            }
        });
        mButtonTransform = (ImageButton) findViewById(R.id.button_transform);
        mButtonTransform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSubBitmap != null) {
                    mSubBitmap = TransformUtil.circleTransform(mSubBitmap);
                    mSubImageView.setImageBitmap(mSubBitmap);
                }
            }
        });
    }

    private void handlePickPhotoClick() {
        Intent intent = new Intent();
        intent.setType(PICK_IMAGE_TYPE);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, PICK_IMAGE_TITLE),
            PICK_IMAGE_REQUEST_CODE);
    }

    private void checkPermissionAndPickImage() {
        if (ContextCompat.checkSelfPermission(MergeImageActivity.this, Manifest.permission
            .READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            mHasReadExternalPermission = true;
            handlePickPhotoClick();
            return;
        }
        if (ContextCompat.checkSelfPermission(MergeImageActivity.this, Manifest.permission
            .READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MergeImageActivity.this, new String[]
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
            handlePickPhotoClick();
            return;
        }
        if (requestCode == MY_PERMISSIONS_READ_EXTERNAL_STORAGE) {
            mHasReadExternalPermission = false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE_REQUEST_CODE && data != null &&
            mHasReadExternalPermission) {
            String imagePath = ImageUtils.getDataColumn(getApplicationContext(), data);
            mSubBitmap = ImageUtils.decodeBitmapFromPath(imagePath, ChooseFrameActivity.IMAGE_SIZE,
                ChooseFrameActivity.IMAGE_SIZE);
            mSubImageView.setImageBitmap(mSubBitmap);
        }
    }
}
