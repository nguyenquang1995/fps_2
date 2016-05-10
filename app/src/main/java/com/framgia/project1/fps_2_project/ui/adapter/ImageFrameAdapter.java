package com.framgia.project1.fps_2_project.ui.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.framgia.project1.fps_2_project.ui.activity.ChooseFrameActivity;
import com.framgia.project1.fps_2_project.ui.mylistener.MyOnClickListener;
import com.framgia.project1.fps_2_project.util.ImageUtils;

/**
 * Created by hacks_000 on 4/24/2016.
 */
public class ImageFrameAdapter extends PagerAdapter implements View.OnClickListener {
    private int[] mImageIds;
    private Context mContext;
    private MyOnClickListener mMyOnClickListener;

    public ImageFrameAdapter(Context context, int[] imageIds) {
        mContext = context;
        mImageIds = imageIds;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = new ImageView(mContext);
        imageView.setTag(position);
        imageView.setImageBitmap(ImageUtils.decodeSampleBitmapFromResource(mContext.getResources(),
            mImageIds[position], ChooseFrameActivity.IMAGE_SIZE, ChooseFrameActivity.IMAGE_SIZE));
        imageView.setOnClickListener(this);
        container.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return mImageIds.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public void setOnItemClickListener(MyOnClickListener listener) {
        this.mMyOnClickListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (mMyOnClickListener != null) {
            mMyOnClickListener.onItemClick(v, (int) v.getTag());
        }
    }
}

