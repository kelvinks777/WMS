package com.gin.ngemart.baseui.component;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.bosnet.ngemart.libgen.Data;

/**
 * Created by luis on 3/2/2018.
 */

public class NgemartGridMenuItemLayout<T extends Data> extends LinearLayout {
    public NgemartGridMenuItemLayout(Context context) {
        super(context);
    }

    public NgemartGridMenuItemLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NgemartGridMenuItemLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setData(T data) {

    }

}
