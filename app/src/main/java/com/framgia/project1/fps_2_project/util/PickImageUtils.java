package com.framgia.project1.fps_2_project.util;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import com.framgia.project1.fps_2_project.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hacks_000 on 4/13/2016.
 */
public class PickImageUtils {
    public static List getImageFromGallery(Context context, Intent data) {
        List<String> listImagePath = new ArrayList<>();
        String[] projections = {MediaStore.Images.ImageColumns.DATA};
        if (data.getData() != null) {
            Uri dataUri = data.getData();
            Cursor cursor = context.getContentResolver().query(dataUri, projections, null,
                null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndexOrThrow(projections[0]);
            String imagePath = cursor.getString(columnIndex);
            listImagePath.add(imagePath);
            cursor.close();
        } else if (data.getClipData() != null) {
            ClipData clipData = data.getClipData();
            int dataSize = clipData.getItemCount();
            for (int i = 0; i < dataSize; i++) {
                ClipData.Item item = clipData.getItemAt(i);
                Uri uri = item.getUri();
                Cursor cusor = context.getContentResolver().query(uri, projections, null, null,
                    null);
                cusor.moveToFirst();
                int columnIndex = cusor.getColumnIndex(projections[0]);
                listImagePath.add(cusor.getString(columnIndex));
                cusor.close();
            }
        } else {
            Toast.makeText(context, R.string.not_pick_image, Toast.LENGTH_LONG).show();
        }
        return listImagePath;
    }
}
