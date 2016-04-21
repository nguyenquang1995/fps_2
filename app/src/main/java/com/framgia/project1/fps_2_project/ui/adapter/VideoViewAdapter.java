package com.framgia.project1.fps_2_project.ui.adapter;

import android.content.Context;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.framgia.project1.fps_2_project.R;
import com.framgia.project1.fps_2_project.data.model.Video;
import com.framgia.project1.fps_2_project.ui.mylistener.MyOnClickListener;

import java.util.List;

/**
 * Created by hacks_000 on 5/16/2016.
 */
public class VideoViewAdapter extends RecyclerView.Adapter<VideoViewAdapter.VideoViewHolder> {
    private static final String VIDEO_NAME_HEADER = "Video name: ";
    private List<Video> mListVideo;
    private Context mContext;
    private MyOnClickListener mMyOnClickListener;

    public VideoViewAdapter(Context context, List<Video> videoList) {
        mContext = context;
        mListVideo = videoList;
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_view_item,
            parent, false);
        VideoViewHolder videoViewHolder = new VideoViewHolder(view, mMyOnClickListener);
        return videoViewHolder;
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        holder.mVideoView.setImageBitmap(ThumbnailUtils
            .createVideoThumbnail(mListVideo.get(position).getVideoPath(),
                MediaStore.Video.Thumbnails.MINI_KIND));
        holder.mTextView.setText(VIDEO_NAME_HEADER + mListVideo.get(position).getVideoName());
        holder.mPosition = position;
    }

    @Override
    public int getItemCount() {
        return mListVideo.size();
    }

    public void setOnItemClickListener(MyOnClickListener listener) {
        this.mMyOnClickListener = listener;
    }

    protected class VideoViewHolder extends RecyclerView.ViewHolder implements
        View.OnClickListener {
        private ImageView mVideoView;
        private TextView mTextView;
        private MyOnClickListener mMyOnClickListener;
        private int mPosition;

        public VideoViewHolder(View itemView, MyOnClickListener listener) {
            super(itemView);
            this.mMyOnClickListener = listener;
            itemView.setOnClickListener(this);
            mVideoView = (ImageView) itemView.findViewById(R.id.item_video);
            mTextView = (TextView) itemView.findViewById(R.id.video_name);
        }

        @Override
        public void onClick(View v) {
            if (mMyOnClickListener != null) {
                mMyOnClickListener.onItemClick(v, mPosition);
            }
        }
    }
}
