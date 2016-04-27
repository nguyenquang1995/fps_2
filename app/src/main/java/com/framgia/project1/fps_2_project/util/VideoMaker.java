package com.framgia.project1.fps_2_project.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.view.Surface;
import android.view.WindowManager;

import com.framgia.project1.fps_2_project.data.model.PhotoModel;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Created by hacks_000 on 4/14/2016.
 */
public class VideoMaker {
    private static final String MIME_TYPE = "video/avc";
    private static final int BIT_RATE = 2000000;
    public static final int FRAMES_PER_SECOND = 30;
    private static final int IFRAME_INTERVAL = 5;
    private static final int TIME_OUT_USEC = 2500;
    private static final int NUM_EFFECT = 3;
    private static final float MAX_DELTA_SCALE = 0.5f;
    private static final float ONE_ROUND = 360;
    private static final String VIDEO_OUTPUT_TYPE = ".mp4";
    private static final String EXCEPTION_FORMAT = "format changed twice";
    private static final String EXCEPTION_NULL = "data was null";
    private static final String EXCEPTION_NOT_START = "muxer has not started";
    private Context mContext;
    private MediaCodec.BufferInfo mBufferInfo;
    private MediaCodec mEncoder;
    private MediaMuxer mMuxer;
    private Surface mInputSurface;
    public ArrayList<Bitmap> mBitmaps;
    private ArrayList<PhotoModel> mListImage ;
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

    public VideoMaker(Context context, ArrayList<PhotoModel> listImage, String videoName) {
        mContext = context;
        mListImage = listImage;
        try {
            init(videoName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String makeVideo() {
        try {
            int maxFrame = FRAMES_PER_SECOND * mListImage.size();
            for (int i = 0; i < maxFrame; i++) {
                encode(false);
                while (!mInitFinish) {
                    initFrame(i);
                }
                mInitFinish = false;
            }
            encode(true);
        } finally {
            release();
        }
        return mOutPutFile.getAbsolutePath();
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
                    mFakePts += 1000000L / FRAMES_PER_SECOND;
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
        if (position % FRAMES_PER_SECOND == FRAMES_PER_SECOND - 1) {
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
            mDeltaRotate += VideoEffects.computeDeltaRotate(ONE_ROUND);
        }
        mInputSurface.unlockCanvasAndPost(canvas);
        mInitFinish = true;
    }
}
