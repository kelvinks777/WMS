package com.gin.ngemart.baseui.component;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by luis on 3/14/2018.
 */

public class NgemartListItemSelfHandler<T> extends LinearLayout {
    public NgemartListItemSelfHandler(Context context) {
        super(context);
    }

    public NgemartListItemSelfHandler(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NgemartListItemSelfHandler(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setData(T data){

    }

}
