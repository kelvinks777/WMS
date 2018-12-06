package com.gin.wms.warehouse.component;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bosnet.ngemart.libgen.Common;
import com.gin.ngemart.baseui.NgemartActivity;
import com.gin.wms.warehouse.R;
import com.gin.wms.warehouse.component.CompUomInputDialogFragment.TempUomItem;

public class LinLayoutCompUom extends LinearLayout {
    private TextView tvUomTitle;
    private EditText txtValue;
    private TempUomItem tempUomItem;
    private NgemartActivity activity;

    public LinLayoutCompUom(Context context) {
        super(context);
        if(context instanceof NgemartActivity)
            this.activity = (NgemartActivity)context;
        else
            throw new ClassCastException("Must be an inheritance from NgemartActivity");
    }

    public LinLayoutCompUom(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        if(context instanceof NgemartActivity)
            this.activity = (NgemartActivity)context;
        else
            throw new ClassCastException("Must be an inheritance from NgemartActivity");
    }

    public LinLayoutCompUom(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if(context instanceof NgemartActivity)
            this.activity = (NgemartActivity)context;
        else
            throw new ClassCastException("Must be an inheritance from NgemartActivity");
    }

    public LinLayoutCompUom(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        if(context instanceof NgemartActivity)
            this.activity = (NgemartActivity)context;
        else
            throw new ClassCastException("Must be an inheritance from NgemartActivity");
    }

    public void setData(TempUomItem data){
        this.tempUomItem = data;
        init();
        setDataToUi(this.tempUomItem);
    }

    public TempUomItem getData(){
        return this.tempUomItem;
    }

    private void init(){
        initComponent();
        setComponentListener();
    }

    private void initComponent(){
        tvUomTitle = findViewById(R.id.tvUomTitle);
        txtValue = findViewById(R.id.txtValue);
    }

    private void setComponentListener(){
        textWatcherListener();
        focusChangeListener();
    }

    private void textWatcherListener(){
        txtValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                try{
                    if(s.length() > 0 )
                        tempUomItem.value = Common.GetNumberWithoutFractionFormat().parse(s.toString()).intValue();
                    else
                        tempUomItem.value = 0;
                }catch (Exception e){
                    activity.showErrorSnackBar(e);
                }
            }
        });
    }

    private void focusChangeListener(){
        txtValue.setOnFocusChangeListener((v, hasFocus) -> {
            if(!hasFocus){
                if(txtValue.getText().length() == 0)
                    txtValue.setText("0");
            }
        });
    }

    private void setDataToUi(TempUomItem tempUomItem){
        tvUomTitle.setText(tempUomItem.uomId);
        txtValue.setText(String.valueOf((int)tempUomItem.value));
    }
}
