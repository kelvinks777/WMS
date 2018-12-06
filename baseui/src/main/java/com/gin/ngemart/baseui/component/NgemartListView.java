package com.gin.ngemart.baseui.component;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import java.util.List;

/**
 * Created by luis on 3/14/2018.
 */

public class NgemartListView<T> extends ListView {

    List<T> dataList;
    int layoutId;

    public NgemartListView(Context context) {
        super(context);
    }

    public NgemartListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NgemartListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void initiate(List<T> dataList, int layoutId){
        this.dataList = dataList;
        this.layoutId = layoutId;
        setAdapter(new NgemartAdapterItemSelfHandler<>(getContext(), layoutId, dataList));
    }


}
