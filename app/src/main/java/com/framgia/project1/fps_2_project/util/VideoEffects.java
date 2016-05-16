package com.framgia.project1.fps_2_project.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import java.util.ArrayList;

/**
 * Created by hacks_000 on 4/18/2016.
 */
public class VideoEffects {
    public static final int[] BLACK_COLOR_ARGB = {255, 0, 0, 0};
    private static final int INVALID_INDEX = -1;
    private static final int TIME_DELAY = 200;

    public static void translate(Canvas canvas, ArrayList<Bitmap> bitmaps, int currentIndex, int
        oldIndex, float x, float y, Paint paint, float screenWidth, float screenHeight) {
        canvas.drawARGB(BLACK_COLOR_ARGB[0], BLACK_COLOR_ARGB[1], BLACK_COLOR_ARGB[2],
            BLACK_COLOR_ARGB[3]);
        Matrix matrix = new Matrix();
        matrix.setTranslate(x, y);
        if (oldIndex != INVALID_INDEX) {
            canvas.drawBitmap(bitmaps.get(oldIndex), 0, 0, paint);
        }
        canvas.drawBitmap(bitmaps.get(currentIndex), matrix, paint);
    }

    public static int computeDeltaMove(float x0) {
        return (int) x0 / (VideoMaker.FRAMES_PER_SECOND * VideoMaker.SECOND_PER_IMAGE);
    }

    public static void scale(Canvas canvas, ArrayList<Bitmap> bitmaps, int currentIndex, int
        oldIndex, float deltaScale, Paint paint, float screenWidth, float screenHeight) {
        canvas.drawARGB(BLACK_COLOR_ARGB[0], BLACK_COLOR_ARGB[1], BLACK_COLOR_ARGB[2],
            BLACK_COLOR_ARGB[3]);
        Matrix matrix = new Matrix();
        matrix.setScale(deltaScale,
            deltaScale);
        if (oldIndex != INVALID_INDEX) {
            canvas.drawBitmap(bitmaps.get(oldIndex), 0, 0, paint);
        }
        canvas.drawBitmap(bitmaps.get(currentIndex), matrix, paint);
    }

    public static float computeDeltaScale(float x0) {
        return (float) x0 / (VideoMaker.FRAMES_PER_SECOND * VideoMaker.SECOND_PER_IMAGE);
    }

    public static void rotate(Canvas canvas, ArrayList<Bitmap> bitmaps, int currentIndex, int
        oldIndex, float deltaRotate, Paint paint, float screenWidth, float screenHeight) {
        canvas.drawARGB(BLACK_COLOR_ARGB[0], BLACK_COLOR_ARGB[1], BLACK_COLOR_ARGB[2],
            BLACK_COLOR_ARGB[3]);
        Matrix matrix = new Matrix();
        matrix.setRotate(deltaRotate);
        if (oldIndex != INVALID_INDEX) {
            canvas.drawBitmap(bitmaps.get(currentIndex), 0, 0, paint);
        }
        canvas.drawBitmap(bitmaps.get(oldIndex), matrix, paint);
    }

    public static float computeDeltaRotate(float x0) {
        return (float) x0 / (VideoMaker.FRAMES_PER_SECOND * VideoMaker.SECOND_PER_IMAGE);
    }
}
