package com.framgia.project1.fps_2_project.util;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by hacks_000 on 3/30/2016.
 */
public class ImageUtils {
    private static final String IMAGE_EXTENSION = ".jpg";
    private static final String SELECTION_TAG = "=?";
    private static final String SPLIT_ID = ":";
    private static final int IMAGE_QUALITY = 85;

    public static int calculateInSampleSize(
        BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight
                && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public static Bitmap decodeSampleBitmapFromResource(Resources res, int resId,
                                                        int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static Bitmap decodeBitmapFromPath(String pathName, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(pathName, options);
    }

    public static float computeImageScaleFitScreen(Bitmap bitmap, float screenWidth, float
        screenHeight) {
        float scaleWidth = screenWidth / bitmap.getWidth();
        float scaleHeight = screenHeight / bitmap.getHeight();
        float scaleRate = (scaleWidth < scaleHeight) ? scaleWidth : scaleHeight;
        return scaleRate;
    }

    public static boolean saveImage(Context context, Bitmap imageBitmap, String imageName) {
        boolean isSuccess = false;
        try {
            OutputStream outputStream = null;
            File file = new File(Environment.getExternalStorageDirectory() + "/" + imageName +
                IMAGE_EXTENSION);
            outputStream = new FileOutputStream(file);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, IMAGE_QUALITY, outputStream);
            outputStream.flush();
            outputStream.close();
            MediaStore.Images.Media.insertImage(context.getContentResolver(), file
                .getAbsolutePath(), file.getName(), file.getName());
            isSuccess = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    public static String getDataColumn(Context context, Intent data) {
        Uri selectedImage = data.getData();
        Cursor cursor = null;
        String filePath = null;
        String docId = DocumentsContract.getDocumentId(selectedImage);
        String[] split = docId.split(SPLIT_ID);
        String[] selectionArgs = new String[]{
            split[1]
        };
        String[] projection = {MediaStore.Images.Media.DATA};
        String selection = MediaStore.Images.Media._ID + SELECTION_TAG;
        cursor = context.getContentResolver()
            .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, selection,
                selectionArgs,
                null);
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndexOrThrow(projection[0]);
            filePath = cursor.getString(columnIndex);
            cursor.close();
        }
        return filePath;
    }
}