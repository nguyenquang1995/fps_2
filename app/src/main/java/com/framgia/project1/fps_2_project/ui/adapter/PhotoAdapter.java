package com.framgia.project1.fps_2_project.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.framgia.project1.fps_2_project.R;
import com.framgia.project1.fps_2_project.data.model.PhotoModel;

import java.util.ArrayList;

/**
 * Created by hacks_000 on 4/13/2016.
 */
public class PhotoAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private Context mContext;
    private ArrayList<PhotoModel> mListImages;

    public PhotoAdapter(Context context, ArrayList<PhotoModel> listImages) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        mListImages = listImages;
    }

    @Override
    public int getCount() {
        return mListImages.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final MyViewHolder mMyViewHolder;
        if (convertView == null) {
            mMyViewHolder = new MyViewHolder();
            convertView = mInflater.inflate(R.layout.choose_photo_item, null);
            mMyViewHolder.mImgThumb = (ImageView) convertView.findViewById(R.id.img_photo);
            mMyViewHolder.mCheckBox = (CheckBox) convertView.findViewById(R.id.checkbox_photo);
            convertView.setTag(mMyViewHolder);
        } else {
            mMyViewHolder = (MyViewHolder) convertView.getTag();
        }
        mMyViewHolder.mImgThumb.setId(position);
        mMyViewHolder.mCheckBox.setId(position);
        mMyViewHolder.mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkBox = (CheckBox) v;
                int position = checkBox.getId();
                if (mListImages.get(position).isSelected()) {
                    checkBox.setChecked(false);
                    mListImages.get(position).setSelected(false);
                } else {
                    checkBox.setChecked(true);
                    mListImages.get(position).setSelected(true);
                }
            }
        });
        mMyViewHolder.mImgThumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = mMyViewHolder.mCheckBox.getId();
                if (mListImages.get(position).isSelected()) {
                    mMyViewHolder.mCheckBox.setChecked(false);
                    mListImages.get(position).setSelected(false);
                } else {
                    mMyViewHolder.mCheckBox.setChecked(true);
                    mListImages.get(position).setSelected(true);
                }
            }
        });
        try {
            setBitmap(mMyViewHolder.mImgThumb, mListImages.get(position).getId());
        } catch (Throwable e) {
            e.printStackTrace();
        }
        mMyViewHolder.mCheckBox.setChecked(mListImages.get(position).isSelected());
        return convertView;
    }

    private void setBitmap(final ImageView imageView, final int id) {
        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... params) {
                return MediaStore.Images.Thumbnails.getThumbnail(mContext
                    .getContentResolver(), id, MediaStore.Images.Thumbnails.MICRO_KIND, null);
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                imageView.setImageBitmap(bitmap);
            }
        }.execute();
    }

    private class MyViewHolder {
        private ImageView mImgThumb;
        private CheckBox mCheckBox;
    }
}
