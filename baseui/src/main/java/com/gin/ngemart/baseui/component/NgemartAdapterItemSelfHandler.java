package com.gin.ngemart.baseui.component;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gin.ngemart.baseui.NgemartAdapterCommon;

import java.util.List;

/**
 * Created by luis on 3/14/2018.
 */

public class NgemartAdapterItemSelfHandler<T> extends NgemartAdapterCommon<T> {
    public NgemartAdapterItemSelfHandler(Context context, int resource, List<T> items) {
        super(context, resource, items, null);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        NgemartListItemSelfHandler<T> convertView = null;
        try {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = (NgemartListItemSelfHandler<T>) inflater.inflate(resource, null);
            T data = (T)getItem(i);
             convertView.setData(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;

    }
}
