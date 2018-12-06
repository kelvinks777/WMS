package com.gin.ngemart.baseui.component;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luis on 3/21/2018.
 */

public class NgemartBindingView extends LinearLayout {
    public NgemartBindingView(Context context) {
        super(context);
    }

    public NgemartBindingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NgemartBindingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    List<View> listControl = new ArrayList<>();
    Object data;

    public void setData(Object data) throws Exception {

        this.data = data;

        refresh();
    }

    public void refresh() throws Exception {
        listControl.clear();

        fillController(this);
    }

    void fillController(ViewGroup view) throws Exception {
        int childCount = view.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View v = view.getChildAt(i);
            if (v instanceof ViewGroup){
                fillController((ViewGroup) v);
            }else {
                if ((v.getTag() != null) && !(v.getTag().equals(""))) {
                    listControl.add(v);
                    if (v instanceof EditText) {
                        EditText tView = ((EditText) v);
                        Field field = data.getClass().getField(tView.getTag().toString());
                        tView.setText((String) field.get(data));
                    }

                    if (v instanceof TextView) {
                        TextView tView = ((TextView) v);
                        Field field = data.getClass().getField(tView.getTag().toString());
                        tView.setText((String) field.get(data));
                    }
                }
            }
        }

    }

    public void commit() throws Exception {
        for (View view :
                listControl) {

            if ((view.getTag() != null) && !(view.getTag().equals(""))) {
                if (view instanceof EditText) {
                    EditText tView = ((EditText) view);
                    Field field = data.getClass().getField(tView.getTag().toString());
                    field.set(data, tView.getText().toString());
                }

                if (view instanceof TextView) {
                    TextView tView = ((TextView) view);
                    Field field = data.getClass().getField(tView.getTag().toString());
                    field.set(data, tView.getText().toString());
                }
            }
        }

    }

    public Object getData(){
        return  data;
    }
}
