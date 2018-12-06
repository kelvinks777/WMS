package com.bosnet.ngemart.libgen;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.util.Base64;
import android.util.TypedValue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by luis on 10/13/2016.
 * Purpose :
 */

public class Base64Util {
    public static String convertByteToString64(byte[] imageByte) {
        return Base64.encodeToString(imageByte, Base64.DEFAULT);
    }

    public static byte[] ConvertString64ToByte(String base64String) {
        return Base64.decode(base64String.getBytes(), Base64.DEFAULT);
    }

    public static String ConvertBitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.WEBP, 30, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

   
    public static Bitmap ConvertByteToBitmap(byte[] imgByte) {
        return BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length);
    }

    public static Bitmap ConvertByteToBitmap(byte[] image64, String defaultImage64) throws Exception{
        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeByteArray(image64, 0, image64.length);

        }catch (OutOfMemoryError e) {
            e.printStackTrace();
            byte[] decodedByteDefault = ConvertString64ToByte(defaultImage64);
            bitmap = BitmapFactory.decodeByteArray(decodedByteDefault, 0, decodedByteDefault.length);
        }catch (Exception e) {
            e.printStackTrace();
            bitmap = null;
        }
        return bitmap;
    }

    public static Bitmap ConvertStringToBitmap(String image64, String defaultImage64) throws Exception{
        byte[] decodedByte = ConvertString64ToByte(image64);
        Bitmap bitmap;
        try{
            bitmap = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);

        }catch(OutOfMemoryError e){
            e.printStackTrace();
            byte[] decodedByteDefault = ConvertString64ToByte(defaultImage64);
            bitmap = BitmapFactory.decodeByteArray(decodedByteDefault, 0, decodedByteDefault.length);

        } catch (Exception e) {
            e.printStackTrace();
            bitmap = null;
        }
        return bitmap;
    }

    public static Bitmap ConvertStringToBitmap(String image64) {
        Bitmap bitmap;
        try {
            byte[] decodedByte = ConvertString64ToByte(image64);
            bitmap = BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);

        }catch (Exception e) {
            e.printStackTrace();
            bitmap=null;
        }

        return bitmap;
    }

    public static Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public static int dpToPixel(Context context,int dp) {
        Resources r = context.getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return Float.floatToIntBits(px);
    }

    public static Bitmap handleSamplingAndRotationBitmap(Context context, Uri selectedImage)
            throws IOException {
        int MAX_HEIGHT = 1024;
        int MAX_WIDTH = 1024;

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream imageStream = context.getContentResolver().openInputStream(selectedImage);
        BitmapFactory.decodeStream(imageStream, null, options);
        imageStream.close();

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        imageStream = context.getContentResolver().openInputStream(selectedImage);
        Bitmap img = BitmapFactory.decodeStream(imageStream, null, options);

        img = rotateImageIfRequired(context, img, selectedImage);
        return img;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
            // with both dimensions larger than or equal to the requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).

            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

    private static Bitmap rotateImageIfRequired(Context context, Bitmap img, Uri selectedImage) throws IOException {

        InputStream input = context.getContentResolver().openInputStream(selectedImage);
        ExifInterface ei;
        if (Build.VERSION.SDK_INT > 23)
            ei = new ExifInterface(input);
        else
            ei = new ExifInterface(selectedImage.getPath());

        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

    public static String GetBase64ImageFromIntent(Intent data) {
        return Base64.encodeToString(getArrayByteFromCapture(data), Base64.DEFAULT);
    }
    public static byte[] getArrayByteFromCapture(Intent data) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        byte[] byteArray = null;
        Bitmap bmp = (Bitmap) data.getExtras().get("data");
        if (bmp != null) {
            bmp.compress(Bitmap.CompressFormat.WEBP, 90, stream);
            byteArray = stream.toByteArray();
        }

        return byteArray;
    }
}
