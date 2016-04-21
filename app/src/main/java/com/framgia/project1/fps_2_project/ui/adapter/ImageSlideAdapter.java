package com.framgia.project1.fps_2_project.ui.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.framgia.project1.fps_2_project.R;

/**
 * Created by hacks_000 on 4/10/2016.
 */
public class ImageSlideAdapter extends PagerAdapter {
    public static final int IMAGE_COUNT = 5;
    public static final int[] mImageIds = {R.drawable.slide1, R.drawable.slide2, R.drawable
        .slide3, R.drawable.slide4, R.drawable.slide5};
    private Context mContext;
    public ImageSlideAdapter(Context context) {
        this.mContext = context;
    }
    @Override
    public int getCount() {
        return IMAGE_COUNT;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.vp_image, container, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.image_display);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageResource(mImageIds[position]);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }
}
