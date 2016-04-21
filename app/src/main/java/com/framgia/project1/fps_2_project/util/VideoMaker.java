package com.framgia.project1.fps_2_project.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.VideoView;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.Container;
import com.framgia.project1.fps_2_project.R;
import com.framgia.project1.fps_2_project.data.model.PhotoModel;
import com.googlecode.mp4parser.FileDataSourceImpl;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.CroppedTrack;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by hacks_000 on 4/14/2016.
 */
public class VideoMaker {
    private static final String MIME_TYPE = "video/avc";
    private static final int BIT_RATE = 2000000;
    public static final int FRAMES_PER_SECOND = 30;
    public static final int SECOND_PER_IMAGE = 5;
    private static final int IFRAME_INTERVAL = 5;
    private static final int TIME_OUT_USEC = 2500;
    private static final int NUM_EFFECT = 3;
    private static final float MAX_DELTA_SCALE = 0.5f;
    private static final float HALF_ROUND = 180;
    private static final long PRESENTATION_TIME = 1000000L;
    public static final String VIDEO_OUTPUT_TYPE = ".mp4";
    private static final String EXCEPTION_FORMAT = "format changed twice";
    private static final String EXCEPTION_NULL = "data was null";
    private static final String EXCEPTION_NOT_START = "muxer has not started";
    private static final String MUSIC_NAME = "my_music.m4a";
    private Context mContext;
    private MediaCodec.BufferInfo mBufferInfo;
    private MediaCodec mEncoder;
    private MediaMuxer mMuxer;
    private Surface mInputSurface;
    public ArrayList<Bitmap> mBitmaps;
    private ArrayList<PhotoModel> mListImage;
    private File mOutPutFile;
    private int mTrackIndex;
    private boolean mMuxerStarted;
    private long mFakePts;
    private Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
    private float x;
    private float y;
    private float mDeltaScale;
    private float mDeltaRotate;
    private float mScreenWidth;
    private float mScreenHeight;
    private int mCurrentIndex;
    private int mOldIndex;
    private boolean mInitFinish;
    private String mVideoName;
    private ProgressDialog mProgressDialog;
    private VideoView mVideoView;

    public VideoMaker(Context context, ArrayList<PhotoModel> listImage, String videoName,
                      VideoView videoView, ProgressDialog progressDialog) {
        mContext = context;
        mListImage = listImage;
        mVideoName = videoName;
        mVideoView = videoView;
        mProgressDialog = progressDialog;
        try {
            init(videoName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void makeVideo() {
        MakeVideoAsynTask makeVideoAsynTask = new MakeVideoAsynTask();
        makeVideoAsynTask.execute();
    }

    private void init(String videoName) throws IOException {
        mCurrentIndex = 0;
        mOldIndex = -1;
        mDeltaScale = 1 - MAX_DELTA_SCALE;
        mDeltaRotate = 0;
        mInitFinish = false;
        WindowManager windowManager =
            (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mScreenWidth = windowManager.getDefaultDisplay().getWidth();
        mScreenHeight = windowManager.getDefaultDisplay().getHeight() / 2;
        x = 0 - mScreenWidth;
        y = 0;
        mBitmaps = new ArrayList<>();
        mOutPutFile = new File(mContext.getFilesDir(), videoName + VIDEO_OUTPUT_TYPE);
        int count = mListImage.size();
        for (int i = 0; i < count; i++) {
            Bitmap bitmap = ImageUtils.decodeBitmapFromPath(mListImage.get(i).getPath(), (int)
                    mScreenWidth,
                (int) mScreenHeight);
            mBitmaps.add(bitmap);
        }
        mBufferInfo = new MediaCodec.BufferInfo();
        MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE, (int) mScreenWidth,
            (int) mScreenHeight);
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities
            .COLOR_FormatSurface);
        format.setInteger(MediaFormat.KEY_BIT_RATE, BIT_RATE);
        format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAMES_PER_SECOND);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL);
        mEncoder = MediaCodec.createEncoderByType(MIME_TYPE);
        mEncoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mInputSurface = mEncoder.createInputSurface();
        mEncoder.start();
        mMuxer = new MediaMuxer(mOutPutFile.toString(), MediaMuxer.OutputFormat
            .MUXER_OUTPUT_MPEG_4);
        mTrackIndex = -1;
        mMuxerStarted = false;
    }

    private void release() {
        if (mEncoder != null) {
            mEncoder.stop();
            mEncoder.release();
            mEncoder = null;
        }
        if (mInputSurface != null) {
            mInputSurface.release();
            mInputSurface = null;
        }
        if (mMuxer != null) {
            mMuxer.stop();
            mMuxer.release();
            mMuxer = null;
        }
    }

    private void encode(boolean isEnd) {
        if (isEnd) {
            mEncoder.signalEndOfInputStream();
        }
        ByteBuffer[] byteBuffers = mEncoder.getOutputBuffers();
        while (true) {
            int encoderStatus = mEncoder.dequeueOutputBuffer(mBufferInfo, TIME_OUT_USEC);
            if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                if (!isEnd) {
                    break;
                }
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                byteBuffers = mEncoder.getOutputBuffers();
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                if (mMuxerStarted) {
                    throw new RuntimeException(EXCEPTION_FORMAT);
                }
                MediaFormat newFormat = mEncoder.getOutputFormat();
                mTrackIndex = mMuxer.addTrack(newFormat);
                mMuxer.start();
                mMuxerStarted = true;
            } else if (encoderStatus >= 0) {
                ByteBuffer encodedData = byteBuffers[encoderStatus];
                if (encodedData == null) {
                    throw new RuntimeException(EXCEPTION_NULL);
                }
                if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                    mBufferInfo.size = 0;
                }
                if (mBufferInfo.size != 0) {
                    if (!mMuxerStarted) {
                        throw new RuntimeException(EXCEPTION_NOT_START);
                    }
                    encodedData.position(mBufferInfo.offset);
                    encodedData.limit(mBufferInfo.offset + mBufferInfo.size);
                    mBufferInfo.presentationTimeUs = mFakePts;
                    mFakePts += PRESENTATION_TIME / FRAMES_PER_SECOND;
                    mMuxer.writeSampleData(mTrackIndex, encodedData, mBufferInfo);
                }
                mEncoder.releaseOutputBuffer(encoderStatus, false);
                if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    break;
                }
            }
        }
    }

    private void initFrame(int position) {
        Canvas canvas = mInputSurface.lockCanvas(null);
        if (position % (FRAMES_PER_SECOND * SECOND_PER_IMAGE) ==
            (FRAMES_PER_SECOND * SECOND_PER_IMAGE) - 1) {
            mOldIndex = mCurrentIndex;
            mCurrentIndex++;
            if (mCurrentIndex == mListImage.size()) {
                mInitFinish = true;
                return;
            }
            x = 0 - mScreenWidth;
            y = 0;
            mDeltaScale = 1 - MAX_DELTA_SCALE;
            mDeltaRotate = 0;
        }
        int typeEffect = mCurrentIndex % NUM_EFFECT;
        if (typeEffect == 0) {
            VideoEffects.translate(canvas, mBitmaps, mCurrentIndex, mOldIndex, x, y, paint,
                mScreenWidth, mScreenHeight);
            x += VideoEffects.computeDeltaMove(mScreenWidth);
        } else if (typeEffect == 1) {
            VideoEffects.scale(canvas, mBitmaps, mCurrentIndex, mOldIndex, mDeltaScale, paint,
                mScreenWidth, mScreenHeight);
            mDeltaScale += VideoEffects.computeDeltaScale(MAX_DELTA_SCALE);
        } else if (typeEffect == 2) {
            VideoEffects.rotate(canvas, mBitmaps, mCurrentIndex, mOldIndex, mDeltaRotate, paint,
                mScreenWidth, mScreenHeight);
            mDeltaRotate += VideoEffects.computeDeltaRotate(HALF_ROUND);
        }
        mInputSurface.unlockCanvasAndPost(canvas);
        mInitFinish = true;
    }

    private String addAudio(String videoSource, String audioSource, String outputFile)
        throws IOException {
        File output = new File(outputFile);
        Movie originalMovie = MovieCreator.build(videoSource);
        Movie audio = MovieCreator.build(new FileDataSourceImpl(new File(audioSource)));
        IsoFile isoFile = new IsoFile(videoSource);
        double lengthInSeconds = (double)
            isoFile.getMovieBox().getMovieHeaderBox().getDuration() /
            isoFile.getMovieBox().getMovieHeaderBox().getTimescale();
        Track track = originalMovie.getTracks().get(0);
        Track audioTrack = audio.getTracks().get(0);
        double startTime1 = 0;
        double endTime1 = lengthInSeconds;
        if (audioTrack.getSyncSamples() != null && audioTrack.getSyncSamples().length > 0) {
            startTime1 = correctTimeToSyncSample(audioTrack, startTime1, false);
            endTime1 = correctTimeToSyncSample(audioTrack, endTime1, true);
        }
        long currentSample = 0;
        double currentTime = 0;
        double lastTime = -1;
        long startSample1 = -1;
        long endSample1 = -1;
        for (int i = 0; i < audioTrack.getSampleDurations().length; i++) {
            long delta = audioTrack.getSampleDurations()[i];
            if (currentTime > lastTime && currentTime <= startTime1) {
                startSample1 = currentSample;
            }
            if (currentTime > lastTime && currentTime <= endTime1) {
                endSample1 = currentSample;
            }
            lastTime = currentTime;
            currentTime += (double) delta / (double) audioTrack.getTrackMetaData().getTimescale();
            currentSample++;
        }
        CroppedTrack cropperAacTrack = new CroppedTrack(audioTrack, startSample1, endSample1);
        Movie movie = new Movie();
        movie.addTrack(track);
        movie.addTrack(cropperAacTrack);
        Container mp4file = new DefaultMp4Builder().build(movie);
        FileChannel fc = new FileOutputStream(output).getChannel();
        mp4file.writeContainer(fc);
        fc.close();
        return output.getAbsolutePath();
    }

    private static double correctTimeToSyncSample(Track track, double cutHere, boolean next) {
        double[] timeOfSyncSamples = new double[track.getSyncSamples().length];
        long currentSample = 0;
        double currentTime = 0;
        for (int i = 0; i < track.getSampleDurations().length; i++) {
            long delta = track.getSampleDurations()[i];
            if (Arrays.binarySearch(track.getSyncSamples(), currentSample + 1) >= 0) {
                timeOfSyncSamples[Arrays.binarySearch(track.getSyncSamples(), currentSample + 1)] =
                    currentTime;
            }
            currentTime += (double) delta / (double) track.getTrackMetaData().getTimescale();
            currentSample++;
        }
        double previous = 0;
        for (double timeOfSyncSample : timeOfSyncSamples) {
            if (timeOfSyncSample > cutHere) {
                if (next) {
                    return timeOfSyncSample;
                } else {
                    return previous;
                }
            }
            previous = timeOfSyncSample;
        }
        return timeOfSyncSamples[timeOfSyncSamples.length - 1];
    }

    private String getAudioFile(String musicName, int rawId) throws IOException {
        File musicSource = new File(Environment.getExternalStorageDirectory(), musicName);
        if (!musicSource.exists()) {
            InputStream in = mContext.getResources().openRawResource(rawId);
            FileOutputStream out =
                new FileOutputStream(musicSource);
            byte[] buff = new byte[1024];
            int read = 0;
            try {
                while ((read = in.read(buff)) > 0) {
                    out.write(buff, 0, read);
                }
            } finally {
                in.close();
                out.close();
            }
        }
        return musicSource.getAbsolutePath();
    }

    private class MakeVideoAsynTask extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(mContext);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(0);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage(mContext.getString(R.string.exporting_video));
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                int maxFrame = FRAMES_PER_SECOND * mListImage.size() * SECOND_PER_IMAGE;
                for (int i = 0; i < maxFrame; i++) {
                    encode(false);
                    while (!mInitFinish) {
                        initFrame(i);
                    }
                    mInitFinish = false;
                    publishProgress(i * 100 / maxFrame);
                }
                encode(true);
            } finally {
                release();
            }
            String videoFile = "";
            try {
                File outPutFile =
                    new File(Environment.getExternalStorageDirectory(),
                        mVideoName + VIDEO_OUTPUT_TYPE);
                videoFile =
                    addAudio(mOutPutFile.getAbsolutePath(),
                        getAudioFile(MUSIC_NAME, R.raw.my_music),
                        outPutFile.getAbsolutePath());
                mOutPutFile.delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return videoFile;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            mProgressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            mProgressDialog.dismiss();
            mVideoView.setVideoPath(s);
            mVideoView.start();
        }
    }
}
