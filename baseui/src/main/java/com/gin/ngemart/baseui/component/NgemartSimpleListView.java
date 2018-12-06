package com.gin.ngemart.baseui.component;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import com.gin.ngemart.baseui.NgemartAdapter;

import java.util.List;

/**
 * Created by luis on 3/12/2018.
 */

public class NgemartSimpleListView<T> extends ListView {
    public NgemartSimpleListView(Context context) {
        super(context);
    }

    public NgemartSimpleListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NgemartSimpleListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void initiate(List<T> listData, String title, String subTitle, String image) {
        NgemartAdapter<T> ngemartAdapter = new NgemartAdapter<>(getContext(),listData);
        ngemartAdapter.setFieldTitle(title);
        ngemartAdapter.setFieldSubTitle(subTitle);
        ngemartAdapter.setFieldImage(image);
        setAdapter(ngemartAdapter);

    }

    public void notifyDataSetChanged() {
        NgemartAdapter<T> ngemartAdapter = (NgemartAdapter<T>) getAdapter();
        ngemartAdapter.notifyDataSetChanged();
    }

}
