package com.gin.ngemart.baseui;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.bosnet.ngemart.libgen.Data;

/**
 * Created by luis on 8/29/2016.
 * Purpose :
 */
abstract class NgemartListItem extends LinearLayout {

    Data data;
    String Authorization = "";
    public NgemartListItem(Context context) {
        super(context);
    }

    public NgemartListItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NgemartListItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public NgemartListItem(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public  void setData(Data data)
    {
        this.data = data;
    }

}
