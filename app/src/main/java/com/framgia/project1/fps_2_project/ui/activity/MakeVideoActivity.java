package com.framgia.project1.fps_2_project.ui.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.framgia.project1.fps_2_project.R;
import com.framgia.project1.fps_2_project.data.model.PhotoModel;
import com.framgia.project1.fps_2_project.data.model.Video;
import com.framgia.project1.fps_2_project.data.remote.DataBaseRemote;
import com.framgia.project1.fps_2_project.ui.adapter.VideoViewAdapter;
import com.framgia.project1.fps_2_project.ui.mylistener.MyOnClickListener;
import com.framgia.project1.fps_2_project.util.Constant;
import com.framgia.project1.fps_2_project.util.VideoMaker;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MakeVideoActivity extends AppCompatActivity implements MyOnClickListener {
    private static final int MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE = 1;
    private ArrayList<PhotoModel> mImageSelected;
    private Toolbar mToolbar;
    private VideoMaker mVideoMaker;
    private String mVideoPath;
    private ProgressDialog mProgressDialog;
    private VideoView mVideoView;
    private boolean mHasWriteExternalPermission;
    private EditText mEditVideoName;
    private AlertDialog.Builder mVideoNameDialog;
    private MediaController mMediaController;
    private DataBaseRemote mDataBaseRemote;
    private RecyclerView mRecyclerView;
    private VideoViewAdapter mVideoViewAdapter;
    private List<Video> mListVideo = new ArrayList<>();
    private boolean mIsSaved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_video);
        init();
        findView();
    }

    private void init() {
        mImageSelected = getIntent().getParcelableArrayListExtra(Constant.INTENT_DATA);
        mHasWriteExternalPermission = false;
        mIsSaved = false;
        mDataBaseRemote = new DataBaseRemote(this);
    }

    private void findView() {
        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        mToolbar.setTitle(getString(R.string.make_video_title));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mVideoView = (VideoView) findViewById(R.id.video_view);
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                        mMediaController = new MediaController(MakeVideoActivity.this);
                        mVideoView.setMediaController(mMediaController);
                        mMediaController.setAnchorView(mVideoView);
                    }
                });
            }
        });
        showDialog();
    }

    private void showOldListVideo() {
        mRecyclerView = (RecyclerView) findViewById(R.id.video_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        try {
            mDataBaseRemote.openDataBase();
            mListVideo = mDataBaseRemote.getListVideo();
            mDataBaseRemote.closeDataBase();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        mVideoViewAdapter = new VideoViewAdapter(MakeVideoActivity.this, mListVideo);
        mVideoViewAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mVideoViewAdapter);
    }

    private void showDialog() {
        mVideoNameDialog = new AlertDialog.Builder(MakeVideoActivity.this);
        mVideoNameDialog.setTitle(getString(R.string.enter_video_name));
        mEditVideoName = new EditText(MakeVideoActivity.this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT);
        mEditVideoName.setLayoutParams(layoutParams);
        mVideoNameDialog.setView(mEditVideoName);
        mVideoNameDialog.setPositiveButton(getString(R.string
            .dialog_positive_button_name), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String videoName = mEditVideoName.getText().toString();
                try {
                    mDataBaseRemote.openDataBase();
                    if (mDataBaseRemote.searchVideo(videoName)) {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(),
                            R.string.video_exist, Toast.LENGTH_LONG).show();
                        showDialog();
                    } else {
                        File outPutFile =
                            new File(Environment.getExternalStorageDirectory(),
                                videoName + VideoMaker.VIDEO_OUTPUT_TYPE);
                        mVideoPath = outPutFile.getAbsolutePath();
                        mVideoMaker =
                            new VideoMaker(MakeVideoActivity.this, mImageSelected, videoName,
                                mVideoView, mProgressDialog);
                        mVideoMaker.makeVideo();
                        showOldListVideo();
                    }
                    mDataBaseRemote.closeDataBase();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
        mVideoNameDialog.setNegativeButton(getString(R.string
            .dialog_negative_button_name), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        mVideoNameDialog.show();
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(MakeVideoActivity.this, Manifest.permission
            .WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            mHasWriteExternalPermission = true;
            return;
        }
        if (ContextCompat.checkSelfPermission(MakeVideoActivity.this, Manifest.permission
            .WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MakeVideoActivity.this, new String[]
                    {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE
            && grantResults.length > 0
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mHasWriteExternalPermission = true;
            return;
        }
        if (requestCode == MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE) {
            mHasWriteExternalPermission = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_make_video, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.save_video && !mIsSaved) {
            checkPermission();
            try {
                mDataBaseRemote.openDataBase();
                Video video = new Video(mEditVideoName.getText().toString(), mVideoPath);
                mListVideo.add(video);
                mVideoViewAdapter.notifyItemChanged(mListVideo.size() - 1);
                mDataBaseRemote.insertVideo(video);
                mIsSaved = true;
                mDataBaseRemote.closeDataBase();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            Toast.makeText(getApplicationContext(), mHasWriteExternalPermission ? getString(R
                .string.save_success) : getString(R.string.write_permission_error), Toast
                .LENGTH_LONG).show();
        }
        if (id == android.R.id.home) {
            if (mVideoPath != null && !mIsSaved) {
                DeleteVideoAsynTask deleteVideoAsynTask = new DeleteVideoAsynTask();
                deleteVideoAsynTask.execute(mVideoPath);
            }
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(View view, int position) {
        mVideoView.setMediaController(null);
        mVideoView.setVideoPath(mListVideo.get(position).getVideoPath());
        mVideoView.start();
    }

    private class DeleteVideoAsynTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            File file = new File(params[0]);
            if (file.exists()) {
                file.delete();
            }
            return null;
        }
    }
}
