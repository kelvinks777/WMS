package com.gin.ngemart.baseui.component;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;

import com.gin.ngemart.baseui.NgemartActivity;
import com.gin.ngemart.baseui.R;

/**
 * Created by manbaul on 6/8/2017.
 */

public class Base64Image {
    private NgemartActivity activity;

    public Base64Image(NgemartActivity activity) {
        this.activity = activity;
    }

    public Bitmap ToBitmap(String source, int res) throws Exception {
        if (source == null || source.isEmpty()) {
            Drawable myDrawable ;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                myDrawable = activity.getResources().getDrawable(res, null);
            } else {
                myDrawable = activity.getResources().getDrawable(res);
            }
            return ((BitmapDrawable) myDrawable).getBitmap();
        }
        else
            return ToBitmap(source);
    }

    public Bitmap ToBitmap(String source, String defaultImage) throws Exception {
        if (source == null || source.isEmpty())
            return ToBitmap(defaultImage);
        else
            return ToBitmap(source);
    }

    public Bitmap ToBitmap(String source) {
        byte[] decodedString = Base64.decode(source, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
}
