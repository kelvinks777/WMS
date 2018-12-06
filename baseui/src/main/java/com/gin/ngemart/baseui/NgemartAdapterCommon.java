package com.gin.ngemart.baseui;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.List;

public class NgemartAdapterCommon<T> extends BaseAdapter {

    protected final Context context;
    protected final int resource;
    protected final List<T> items;
    private INgemartAdapterHandler<T> ngemartAdapterListener;

    public NgemartAdapterCommon(Context context, int resource, List<T> items, INgemartAdapterHandler<T> ngemartAdapterListener)
    {
        this.resource = resource;
        this.context = context;
        this.items = items;
        this.ngemartAdapterListener = ngemartAdapterListener;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View convertView = null;
        try {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(resource, null);
            T data = (T)getItem(i);
            if (ngemartAdapterListener != null)
                convertView = ngemartAdapterListener.onGetView(data, convertView);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    public interface INgemartAdapterHandler<T>
    {
        View onGetView(T data, View detailView) throws Exception;
    }
}
