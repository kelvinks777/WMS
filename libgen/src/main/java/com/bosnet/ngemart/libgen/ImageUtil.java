package com.bosnet.ngemart.libgen;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Size;

import java.io.ByteArrayOutputStream;

/**
 * Created by manbaul on 12/8/2017.
 */

public class ImageUtil {
    public static byte[] convertBitmapToByte(Bitmap bitmap) throws Exception {
        return convertBitmapToByte(bitmap, Bitmap.CompressFormat.WEBP, 100);
    }

    public static byte[] convertBitmapToByte(Bitmap bitmap, Bitmap.CompressFormat format, int quality) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(format, quality, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return byteArray;
    }

    public static Bitmap convertByteToBitmap(byte[] imgByte) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
        return bitmap;
    }

    public static Bitmap resizeBitmap(Bitmap bitmap, int newWidth, int newHeight ) {
        Bitmap result = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, false);
        return result;
    }
}

