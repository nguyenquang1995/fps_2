package com.framgia.project1.fps_2_project.util;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
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
    private static final String EXTERNAL_STORAGE = "com.android.externalstorage.documents";
    private static final String DOWNLOAD_DOCUMENT = "com.android.providers.downloads.documents";
    private static final String MEDIA_DOCUMENT = "com.android.providers.media.documents";
    private static final String GOOGLE_PHOTO = "com.google.android.apps.photos.content";
    private static final String PRIMARY_IMAGE = "primary";
    private static final String DOWNLOAD_CONTENT = "content://downloads/public_downloads";
    private static final String ID_SELECTION = "_id=?";
    private static final String IMAGE_TYPE = "image";
    private static final String AUDIO_TYPE = "audio";
    private static final String VIDEO_TYPE = "video";
    private static final String FILE_TYPE = "file";
    private static final String CONTENT_TYPE = "content";

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

    public static String getPath(Context context, Intent data) {
        Uri uri = data.getData();
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri) && isExternalStorageDocument
            (uri)) {
            final String docId = DocumentsContract.getDocumentId(uri);
            final String[] split = docId.split(SPLIT_ID);
            final String type = split[0];
            if (PRIMARY_IMAGE.equalsIgnoreCase(type)) {
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else return null;
        }
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri) && isDownloadsDocument(uri)) {
            final String id = DocumentsContract.getDocumentId(uri);
            final Uri contentUri = ContentUris
                .withAppendedId(Uri.parse(DOWNLOAD_CONTENT),
                    Long.valueOf(id));
            return getDataColumn(context, contentUri, null, null);
        }
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri) && isMediaDocument(uri)) {
            final String docId = DocumentsContract.getDocumentId(uri);
            final String[] split = docId.split(SPLIT_ID);
            final String type = split[0];
            Uri contentUri = null;
            if (IMAGE_TYPE.equals(type)) {
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            } else if (VIDEO_TYPE.equals(type)) {
                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            } else if (AUDIO_TYPE.equals(type)) {
                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            }
            final String[] selectionArgs = new String[]{split[1]};
            return getDataColumn(context, contentUri, ID_SELECTION, selectionArgs);
        }
        if (CONTENT_TYPE.equalsIgnoreCase(uri.getScheme()) && isGooglePhotosUri(uri)) {
            return uri.getLastPathSegment();
        }
        if (CONTENT_TYPE.equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        if (FILE_TYPE.equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {
        Cursor cursor = null;
        final String[] projection = {MediaStore.Images.Media.DATA};
        try {
            cursor =
                context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return EXTERNAL_STORAGE.equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return DOWNLOAD_DOCUMENT.equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return MEDIA_DOCUMENT.equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return GOOGLE_PHOTO.equals(uri.getAuthority());
    }
}