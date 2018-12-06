package com.gin.ngemart.baseui;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;

/**
 * Created by luis on 5/4/2016.
 * Purpose :
 */
public class NgemartSwipeRefresh extends SwipeRefreshLayout {
    public NgemartSwipeRefresh(Context context) {
        super(context);
        setColor();
    }

    public NgemartSwipeRefresh(Context context, AttributeSet attrs) {
        super(context, attrs);
        setColor();
    }

    private void setColor() {
        this.setColorSchemeResources(
                android.R.color.holo_orange_light,
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_red_light);
    }
}
