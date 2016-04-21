package com.framgia.project1.fps_2_project.ui.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.framgia.project1.fps_2_project.R;
import com.framgia.project1.fps_2_project.data.model.Image;
import com.framgia.project1.fps_2_project.data.remote.DataBaseRemote;
import com.framgia.project1.fps_2_project.ui.widget.ActiveImageView;
import com.framgia.project1.fps_2_project.util.Constant;
import com.framgia.project1.fps_2_project.util.ImageUtils;
import com.framgia.project1.fps_2_project.util.TransformUtil;

import java.io.File;
import java.sql.SQLException;

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
    private ImageButton mButtonCancel;
    private ImageButton mButtonReturn;
    private ImageButton mButtonAddImage;
    private ImageButton mButtonTransformCircle;
    private ImageButton mButtonTransformSquare;
    private ImageButton mButtonDone;
    private LinearLayout mLayoutGet;
    private LinearLayout mLayoutEdit;
    private EditText mEditImageName;
    private AlertDialog.Builder mImageNameDialog;
    private boolean mHasWriteExternalPermission;
    private DataBaseRemote mDataBaseRemote;

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
        mDataBaseRemote = new DataBaseRemote(this);
    }

    private void findView() {
        mLayoutGet = (LinearLayout) findViewById(R.id.layout_tool_get);
        mLayoutEdit = (LinearLayout) findViewById(R.id.layout_tool_edit);
        mLayoutEdit.setVisibility(View.GONE);
        mImageBackground = (ImageView) findViewById(R.id.background_image);
        mImageBackground.setImageBitmap(mBitmapBackground);
        mSubImageView = (ActiveImageView) findViewById(R.id.sub_image);
        mFrameLayout = (FrameLayout) findViewById(R.id.image_result);
        mButtonSave = (ImageButton) findViewById(R.id.button_save);
        mButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureLayout();
                showDialog();
            }
        });
        mButtonAddImage = (ImageButton) findViewById(R.id.button_add_image);
        mButtonAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionAndPickImage();
            }
        });
        mButtonTransformCircle = (ImageButton) findViewById(R.id.button_transform);
        mButtonTransformCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSubBitmap != null) {
                    mSubImageView.setImageBitmap(TransformUtil.circleTransform(mSubBitmap));
                    mSubImageView.invalidate();
                }
            }
        });
        mButtonTransformSquare = (ImageButton) findViewById(R.id.button_crop);
        mButtonTransformSquare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSubBitmap != null) {
                    mSubImageView.setImageBitmap(TransformUtil.squareTransform(mSubBitmap));
                    mSubImageView.invalidate();
                }
            }
        });
        mButtonCancel = (ImageButton) findViewById(R.id.button_cancel);
        mButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSubImageView.setImageBitmap(null);
                mSubImageView.invalidate();
                mLayoutEdit.setVisibility(LinearLayout.GONE);
                mLayoutGet.setVisibility(LinearLayout.VISIBLE);
            }
        });
        mButtonDone = (ImageButton) findViewById(R.id.button_done);
        mButtonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSubBitmap != null) {
                    captureLayout();
                    mSubImageView.setImageBitmap(null);
                    mSubImageView.invalidate();
                    mLayoutEdit.setVisibility(LinearLayout.GONE);
                    mLayoutGet.setVisibility(LinearLayout.VISIBLE);
                }
            }
        });
        mButtonReturn = (ImageButton) findViewById(R.id.button_return);
        mButtonReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSubBitmap != null) {
                    mSubImageView.setImageBitmap(mSubBitmap);
                    mSubImageView.invalidate();
                }
            }
        });
    }

    private void captureLayout() {
        mFrameLayout.setDrawingCacheEnabled(true);
        mFrameLayout.buildDrawingCache();
        mBitmapBackground = mFrameLayout.getDrawingCache().copy(Bitmap.Config.ARGB_8888, true);
        mImageBackground.setImageBitmap(mBitmapBackground);
        mFrameLayout.setDrawingCacheEnabled(false);
    }

    private void showDialog() {
        mImageNameDialog = new AlertDialog.Builder(MergeImageActivity.this);
        mImageNameDialog.setTitle(getString(R.string.image_dialog_title));
        mEditImageName = new EditText(MergeImageActivity.this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT);
        mEditImageName.setLayoutParams(layoutParams);
        mImageNameDialog.setView(mEditImageName);
        mImageNameDialog.setPositiveButton(getString(R.string
            .dialog_positive_button_name), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String imageName = mEditImageName.getText().toString();
                try {
                    mDataBaseRemote.openDataBase();
                    if (mDataBaseRemote.searchImage(imageName)) {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(),
                            R.string.image_exist, Toast.LENGTH_LONG).show();
                        showDialog();
                    } else {
                        File imagePath = new File(Environment.getExternalStorageDirectory(),
                            imageName);
                        Image image = new Image(imageName, imagePath.getAbsolutePath(), 0, 0);
                        mDataBaseRemote.insertImage(image);
                        ImageUtils.saveImage(MergeImageActivity.this, mBitmapBackground, imageName);
                        Toast.makeText(getApplicationContext(), R.string.notice_save, Toast
                            .LENGTH_SHORT).show();
                    }
                    mDataBaseRemote.closeDataBase();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
        mImageNameDialog.setNegativeButton(getString(R.string
            .dialog_negative_button_name), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        mImageNameDialog.show();
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
            String imagePath = ImageUtils.getPath(getApplicationContext(), data);
            mSubBitmap = ImageUtils.decodeBitmapFromPath(imagePath, ChooseFrameActivity.IMAGE_SIZE,
                ChooseFrameActivity.IMAGE_SIZE);
            mSubImageView.setImageBitmap(mSubBitmap);
            mSubImageView.returnOldPosition();
            mLayoutGet.setVisibility(LinearLayout.GONE);
            mLayoutEdit.setVisibility(LinearLayout.VISIBLE);
        }
    }
}
