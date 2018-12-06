package com.gin.ngemart.baseui.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

import com.bosnet.ngemart.libgen.Data;

import java.util.List;

/**
 * Created by luis on 3/2/2018.
 */

public class NgemartGridMenu<T extends  Data> extends GridView {

    public NgemartGridMenu(Context context) {
        super(context);
    }

    public NgemartGridMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NgemartGridMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void initiate(List<T> dataList, int layoutId ) {
        NgemartGridMenuAdapter<T> ngemartGridMenuAdapter = new NgemartGridMenuAdapter<>(this.getContext(), dataList, layoutId);
        this.setAdapter(ngemartGridMenuAdapter);

    }

    class NgemartGridMenuAdapter <Y extends Data> extends BaseAdapter {
        private Context mContext;
        private int layoutId;
        List<Y> dataList;

        NgemartGridMenuAdapter(Context context, List<Y> dataList, int layoutId) {
            mContext = context;
           this.dataList = dataList;
           this.layoutId = layoutId;
        }

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup parent) {
            NgemartGridMenuItemLayout<Y> gridViewAndroid;
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {
                 gridViewAndroid = (NgemartGridMenuItemLayout<Y>) inflater.inflate(layoutId, null);
                 gridViewAndroid.setData(dataList.get(i));

            } else {
                gridViewAndroid = (NgemartGridMenuItemLayout<Y>) convertView;
            }

            return gridViewAndroid;
        }
    }

}
