package com.framgia.project1.fps_2_project.ui.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.framgia.project1.fps_2_project.R;
import com.framgia.project1.fps_2_project.data.model.Constant;
import com.framgia.project1.fps_2_project.ui.adapter.ImageSlideAdapter;
import com.framgia.project1.fps_2_project.ui.widget.CirclePageIndicator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements Constant {
    private static final int FUNCTION_MERGEPHOTO = 1;
    private static final int FUNCTION_MAKEVIDEO = 2;
    int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private ImageButton mMergePhotoButton;
    private ImageButton mMakeVideoButton;
    private int mTypeFunction;
    private ViewPager mViewPager;
    private CirclePageIndicator mIndicator;
    private int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 10;
    private CallbackManager mCallbackManager;
    private ShareDialog mShareDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_main);
        findView();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(getString(R.string.permission_facebook));
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                selectImage();
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
            }
        });
        mShareDialog = new ShareDialog(this);
    }

    private void findView() {
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mViewPager.setAdapter(new ImageSlideAdapter(MainActivity.this));
        mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        mIndicator.setViewPager(mViewPager);
        findViewById(R.id.button_take_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });
        findViewById(R.id.button_sketch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditImageActivity.class);
                startActivity(intent);
            }
        });
        mMergePhotoButton = (ImageButton) findViewById(R.id.button_merge_image);
        mMergePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTypeFunction = FUNCTION_MERGEPHOTO;
                startActivity(new Intent(MainActivity.this, ChooseFrameActivity.class));
            }
        });
        mMakeVideoButton = (ImageButton) findViewById(R.id.button_make_video);
        mMakeVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTypeFunction = FUNCTION_MAKEVIDEO;
                startActivity(new Intent(MainActivity.this, ChoosePhotoActivity.class));
            }
        });
    }

    private void selectImage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(getString(R.string.title_share_facebook));
        builder.setItems(LIST_ITEM_FACEBOOK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (LIST_ITEM_FACEBOOK[item].equals(LIST_ITEM_FACEBOOK[0])) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (LIST_ITEM_FACEBOOK[item].equals(LIST_ITEM_FACEBOOK[1])) {
                    Intent intent = new Intent(
                        Intent.ACTION_GET_CONTENT
                    );
                    intent.setType(getString(R.string.meadia_type));
                    startActivityForResult(
                        Intent.createChooser(intent, getString(R.string.select)),
                        SELECT_FILE);
                } else if (LIST_ITEM_FACEBOOK[item].equals(LIST_ITEM_FACEBOOK[2])) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        }
    }

    private void onSelectFromGalleryResult(Intent data) {
        Uri selectedImageUri = data.getData();
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = managedQuery(selectedImageUri, projection, null, null,
            null);
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        String selectedImagePath = cursor.getString(columnIndex);
        Bitmap thumbnail;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(selectedImagePath, options);
        final int REQUIRED_SIZE = 200;
        int scale = 1;
        while (options.outWidth / scale / 2 >= REQUIRED_SIZE
            && options.outHeight / scale / 2 >= REQUIRED_SIZE)
            scale *= 2;
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        thumbnail = BitmapFactory.decodeFile(selectedImagePath, options);
        shareDialog(thumbnail);
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get(getString(R.string.data));
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
            System.currentTimeMillis() + getString(R.string.jpg));
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        shareDialog(thumbnail);
    }

    public void shareDialog(Bitmap imagePath) {
        SharePhoto photo = new SharePhoto.Builder()
            .setBitmap(imagePath)
            .setCaption(getString(R.string.testing))
            .build();
        SharePhotoContent content = new SharePhotoContent.Builder()
            .addPhoto(photo)
            .build();
        mShareDialog.show(content);
        startActivity(new Intent(MainActivity.this, ChooseFrameActivity.class));
    }
}