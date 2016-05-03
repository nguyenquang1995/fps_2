package com.framgia.project1.fps_2_project.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import com.framgia.project1.fps_2_project.R;
import com.framgia.project1.fps_2_project.ui.adapter.ImageSlideAdapter;
import com.framgia.project1.fps_2_project.ui.widget.CirclePageIndicator;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int FUNCTION_MERGEPHOTO = 1;
    private static final int FUNCTION_MAKEVIDEO = 2;
    private static final int REQUEST_CODE_GET_PHOTOS = 1;
    private static final String INTENT_TITLE_CHOOSE_PHOTO = "choose photos";
    private static final String INTENT_TYPE_CHOOSE_PHOTO = "image/*";
    private ViewPager mViewPager;
    private CirclePageIndicator mIndicator;
    private ImageButton mMergePhotoButton;
    private ImageButton mMakeVideoButton;
    private int mTypeFunction;
    private List<String> mListImageSelected = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findView();
    }

    private void findView() {
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mViewPager.setAdapter(new ImageSlideAdapter(MainActivity.this));
        mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        mIndicator.setViewPager(mViewPager);
        findViewById(R.id.button_take_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivity(cameraIntent);
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
}