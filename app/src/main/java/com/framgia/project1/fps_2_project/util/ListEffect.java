package com.framgia.project1.fps_2_project.util;

import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;

/**
 * Created by nguyenxuantung on 03/05/2016.
 */
public class ListEffect {
    public static Bitmap createInvertedBitmap(Bitmap src) {
        ColorMatrix colorMatrix_Inverted =
            new ColorMatrix(new float[]{
                -1, 0, 0, 0, 255,
                0, -1, 0, 0, 255,
                0, 0, -1, 0, 255,
                0, 0, 0, 1, 0});
        ColorFilter ColorFilter_Sepia = new ColorMatrixColorFilter(
            colorMatrix_Inverted);
        Bitmap bitmap = Bitmap.createBitmap(src.getWidth(), src.getHeight(),
            Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColorFilter(ColorFilter_Sepia);
        canvas.drawBitmap(src, 0, 0, paint);
        return bitmap;
    }

    public static Bitmap highlightImage(Bitmap src) {
        // create new bitmap, which will be painted and becomes result image
        Bitmap bmOut =
            Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);
        // setup canvas for painting
        Canvas canvas = new Canvas(bmOut);
        // setup default color
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        // create a blur paint for capturing alpha
        Paint ptBlur = new Paint();
        ptBlur.setMaskFilter(new BlurMaskFilter(15, BlurMaskFilter.Blur.NORMAL));
        int[] offsetXY = new int[2];
        // capture alpha into a bitmap
        Bitmap bmAlpha = src.extractAlpha(ptBlur, offsetXY);
        // create a color paint
        Paint ptAlphaColor = new Paint();
        ptAlphaColor.setColor(0xFFFFFFFF);
        // paint color for captured alpha region (bitmap)
        canvas.drawBitmap(bmAlpha, offsetXY[0], offsetXY[1], ptAlphaColor);
        // free memory
        bmAlpha.recycle();
        // paint the image source
        canvas.drawBitmap(src, 0, 0, null);
        // return out final image
        return bmOut;
    }

    public static Bitmap grayImage(Bitmap src) {
        //Array to generate Gray-Scale image
        float[] GrayArray = {
            0.213f, 0.715f, 0.072f, 0.0f, 0.0f,
            0.213f, 0.715f, 0.072f, 0.0f, 0.0f,
            0.213f, 0.715f, 0.072f, 0.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f, 0.0f,
        };
        ColorMatrix colorMatrixGray = new ColorMatrix(GrayArray);
        int w = src.getWidth();
        int h = src.getHeight();
        Bitmap bitmapResult = Bitmap
            .createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvasResult = new Canvas(bitmapResult);
        Paint paint = new Paint();
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrixGray);
        paint.setColorFilter(filter);
        canvasResult.drawBitmap(src, 0, 0, paint);
        return bitmapResult;
    }

    public static Bitmap rotate(Bitmap bitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap
            .createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}