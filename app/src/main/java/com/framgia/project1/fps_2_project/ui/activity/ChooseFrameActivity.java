package com.framgia.project1.fps_2_project.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.framgia.project1.fps_2_project.R;
import com.framgia.project1.fps_2_project.ui.adapter.ImageFrameAdapter;
import com.framgia.project1.fps_2_project.ui.mylistener.MyOnClickListener;
import com.framgia.project1.fps_2_project.util.Constant;

public class ChooseFrameActivity extends AppCompatActivity implements MyOnClickListener {
    public static final int IMAGE_SIZE = 200;
    private ViewPager mViewPager;
    private ImageFrameAdapter mPagerAdapter;
    private ImageButton mNextButton;
    private ImageButton mPreviousButton;
    private Toast mToast;
    private int[] mImageIds = {
        R.drawable.bg1,
        R.drawable.bg2,
        R.drawable.bg3,
        R.drawable.bg4,
        R.drawable.bg5
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_frame);
        findView();
    }

    private void findView() {
        mViewPager = (ViewPager) findViewById(R.id.image_viewpager);
        mPagerAdapter = new ImageFrameAdapter(ChooseFrameActivity.this, mImageIds);
        mViewPager.setAdapter(mPagerAdapter);
        mPagerAdapter.setOnItemClickListener(this);
        mNextButton = (ImageButton) findViewById(R.id.next_button);
        mPreviousButton = (ImageButton) findViewById(R.id.previous_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mViewPager.getCurrentItem() == mImageIds.length - 1 && !mToast.getView()
                    .isShown()) {
                    mToast = Toast.makeText(getApplicationContext(), R.string.end_view_page,
                        Toast.LENGTH_SHORT);
                    mToast.show();
                } else {
                    mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
                }
            }
        });
        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mViewPager.getCurrentItem() == 0 && !mToast.getView()
                    .isShown()) {
                    mToast = Toast.makeText(getApplicationContext(), R.string.start_view_page,
                        Toast.LENGTH_SHORT);
                    mToast.show();
                } else {
                    mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
                }
            }
        });
        mToast = Toast.makeText(getApplicationContext(), R.string.merge_guide, Toast.LENGTH_SHORT);
        mToast.show();
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(ChooseFrameActivity.this, MergeImageActivity.class);
        intent.putExtra(Constant.INTENT_DATA, mImageIds[position]);
        startActivity(intent);
    }
}
