package com.gin.ngemart.baseui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.List;


public class NgemartAdapter<T> extends BaseAdapter {
    private final Context context;
    private final List<T> itemList;

    private String fieldTitle = "";
    private String fieldSubTitle = "";
    private String fieldImage = "";

    public NgemartAdapter(Context context, List<T> items) {
        this.context = context;
        this.itemList = items;
    }

    public void setFieldTitle(String title) {
        this.fieldTitle = title;
    }

    public void setFieldSubTitle(String subTitle) {
        this.fieldSubTitle = subTitle;
    }

    public void setFieldImage(String image) {
        this.fieldImage = image;
    }

    @SuppressLint("InflateParams")
    @SuppressWarnings("unchecked")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            ViewHolder viewHolder;
            Boolean isHeader = false;
            Field field;
            T data = (T) getItem(position);
            try {
                field = data.getClass().getField("isHeader");
                if (field != null) {
                    isHeader = field.getBoolean(data);
                }
            } catch (Exception e) {
                isHeader = false;
            }

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            if (isHeader) {
                convertView = inflater.inflate(R.layout.listview_header, null);
                viewHolder = new ViewHolder();
                viewHolder.title = convertView.findViewById(R.id.header_text);
                viewHolder.isHeader = true;
                convertView.setTag(viewHolder);

            } else {
                convertView = inflater.inflate(R.layout.listview_item, null);
                viewHolder = new ViewHolder();
                viewHolder.title = convertView.findViewById(R.id.listItemTitle);
                viewHolder.subTitle = convertView.findViewById(R.id.listItemSubTitle);
                viewHolder.imageView = convertView.findViewById(R.id.image_icon);
                viewHolder.imageView.setVisibility(View.GONE);
                viewHolder.isHeader = false;
                convertView.setTag(viewHolder);
            }

            if (isHeader) {
                field = data.getClass().getField(fieldTitle);
                viewHolder.title.setText((String) field.get(data));
            } else {
                if (!fieldTitle.equals("")) {
                    field = data.getClass().getField(fieldTitle);
                    viewHolder.title.setText((String) field.get(data));
                    viewHolder.title.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.title.setVisibility(View.GONE);
                }
                if (!fieldSubTitle.equals("")) {
                    field = data.getClass().getField(fieldSubTitle);
                    viewHolder.subTitle.setText((String) field.get(data));
                    viewHolder.subTitle.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.subTitle.setVisibility(View.GONE);
                }

                if (!fieldImage.equals("")) {
                    field = data.getClass().getField(fieldImage);
                    viewHolder.imageView.setImageBitmap((Bitmap) field.get(data));
                    viewHolder.imageView.setVisibility(View.VISIBLE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        Boolean isHeader = false;
        TextView title, subTitle;
        ImageView imageView;
    }
}
