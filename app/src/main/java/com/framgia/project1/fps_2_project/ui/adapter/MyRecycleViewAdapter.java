package com.framgia.project1.fps_2_project.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.framgia.project1.fps_2_project.R;
import com.framgia.project1.fps_2_project.data.model.Constant;
import com.framgia.project1.fps_2_project.data.model.EffectItem;
import com.framgia.project1.fps_2_project.ui.mylistener.MyOnClickListener;

import java.util.ArrayList;

/**
 * Created by nguyenxuantung on 06/05/2016.
 */
public class MyRecycleViewAdapter
    extends RecyclerView.Adapter<MyRecycleViewAdapter.MyRecycleViewHolder> implements Constant {
    private ArrayList<EffectItem> mArrayList;
    private Context mContext;
    private MyOnClickListener mOnClickListener;

    public MyRecycleViewAdapter(Context mContext, ArrayList<EffectItem> arrayList) {
        this.mContext = mContext;
        this.mArrayList = arrayList;
    }

    public void setOnItemClickListener(MyOnClickListener listener) {
        this.mOnClickListener = listener;
    }

    @Override
    public MyRecycleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =
            LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row, parent, false);
        return new MyRecycleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyRecycleViewHolder holder, int position) {
        EffectItem item = mArrayList.get(position);
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), item.getImage());
        holder.imageview.setImageBitmap(bitmap);
        holder.title.setText(item.getTitle());
        holder.mPosition = position;
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    class MyRecycleViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {
        private TextView title;
        private ImageView imageview;
        private int mPosition;

        public MyRecycleViewHolder(View itemView) {
            super(itemView);
            this.title = (TextView) itemView
                .findViewById(R.id.title);
            this.imageview = (ImageView) itemView
                .findViewById(R.id.image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnClickListener != null)
                mOnClickListener.onItemClick(v, mPosition);
        }
    }
}