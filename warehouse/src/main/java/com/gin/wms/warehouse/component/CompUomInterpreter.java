package com.gin.wms.warehouse.component;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gin.ngemart.baseui.NgemartActivity;
import com.gin.wms.manager.db.data.CompUomData;
import com.gin.wms.manager.db.data.helper.CompUomHelper;
import com.gin.wms.warehouse.R;

/**
 * Created by bintang on 6/6/2018.
 */

public class CompUomInterpreter extends LinearLayout implements View.OnClickListener, CompUomInputDialogFragment.UomInputListener {
    private CompUomData compUomData = null;
    private double palletConversion = 0.0;
    private double qty = 0.0;

    private final String STATE_SUPER_CLASS = "SuperClass";
    private final String STATE_COMP_UOM = "CompUom";
    private final String STATE_PALLET_CONVERSION = "PalletConversion";
    private final String STATE_PALLET_QTY = "PalletQty";

    private TextView tvCompUomId, tvCompUomValue;
    private ImageButton btnInputDialog;
    private NgemartActivity activity;

    private String strCompUomId = "";
    private String comUomValue = "";
    private CompUomInputDialogFragment.UomInputListener uomInputListener;

    public CompUomInterpreter(Context context) {
        super(context);
        init(context);
    }

    public CompUomInterpreter(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CompUomInterpreter(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        initContext(context);
        initComponent(context);
    }

    private void initContext(Context context){
        this.activity = (NgemartActivity)context;
        this.uomInputListener = this;
    }

    private void initComponent(Context context){
        inflate(context, R.layout.component_comp_uom_interpreter, this);
        this.tvCompUomId = findViewById(R.id.tvCompUomId);
        this.tvCompUomValue = findViewById(R.id.tvCompUomValue);
        this.btnInputDialog = findViewById(R.id.btnInputDialog);
        btnInputDialog.setOnClickListener(this);
    }

    public void setPalletConversion(double palletConversion){
        this.palletConversion = palletConversion;
    }

    public double getPalletConversion(){
        return this.palletConversion;
    }

    public void setCompUomData(CompUomData compUomData){
        this.compUomData = compUomData;
    }

    public void setQty(double totalQty){
        this.qty = totalQty;
        String palletId = "Pallet" + (int)palletConversion;
        strCompUomId = "" + palletId;
        double modValueFromPalletUom = totalQty % palletConversion;

        if(compUomData != null)
            strCompUomId = palletId + "/" + compUomData.compUomId;

        if(compUomData != null)
            comUomValue  = new CompUomHelper(this.compUomData).getCompUomValueFromTotal(modValueFromPalletUom);

        String palletUom = getPalletUomStr(totalQty);
        comUomValue = palletUom + comUomValue;

        this.tvCompUomId.setText(strCompUomId);
        this.tvCompUomValue.setText(comUomValue);
    }

    public void setEditButtonActive(boolean enable){
        if(enable)
            btnInputDialog.setColorFilter(ContextCompat.getColor(activity.getApplicationContext(), R.color.colorClickable));
        else
            btnInputDialog.setColorFilter(ContextCompat.getColor(activity.getApplicationContext(), R.color.colorClickableDisabled));

        btnInputDialog.setEnabled(enable);
    }

    public double getQty(){
        return qty;
    }

    private String getPalletUomStr(double totalQty){
        StringBuilder strResult = new StringBuilder();
        if(totalQty >= palletConversion){
            double modResult = totalQty % palletConversion;
            double divResult = (totalQty - modResult) / palletConversion;
            strResult.append(String.format("%03d", (int) divResult));
        }else{
            strResult.append(String.format("%03d", (int) 0));
        }

        if(compUomData != null)
            strResult.append(compUomData.separator);

        return strResult.toString();
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(STATE_SUPER_CLASS, super.onSaveInstanceState());
        bundle.putSerializable(STATE_COMP_UOM, compUomData);
        bundle.putDouble(STATE_PALLET_CONVERSION, palletConversion);
        bundle.putDouble(STATE_PALLET_QTY, qty);

        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if(state instanceof Bundle){
            super.onRestoreInstanceState(((Bundle) state).getParcelable(STATE_SUPER_CLASS));
            this.compUomData = (CompUomData)((Bundle) state).getSerializable(STATE_COMP_UOM);
            this.palletConversion = ((Bundle) state).getDouble(STATE_PALLET_CONVERSION);
            this.qty = ((Bundle) state).getDouble(STATE_PALLET_QTY);
        }else{
            super.onRestoreInstanceState(state);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnInputDialog:
                FragmentManager fm = activity.getSupportFragmentManager();
                CompUomInputDialogFragment dialogFragment = CompUomInputDialogFragment.newInstance(uomInputListener, compUomData, palletConversion, strCompUomId, comUomValue);
                dialogFragment.show(fm, "CompUomInputDialog");
                break;
        }
    }

    @Override
    public void onInputUomFinished(double inputValue) {
        setQty(inputValue);
    }
}
