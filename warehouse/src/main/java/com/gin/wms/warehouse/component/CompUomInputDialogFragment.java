package com.gin.wms.warehouse.component;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.bosnet.ngemart.libgen.Data;
import com.gin.wms.manager.db.data.CompUomData;
import com.gin.wms.manager.db.data.UomConversionData;
import com.gin.wms.warehouse.R;
import com.gin.wms.warehouse.base.WarehouseDialogFragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CompUomInputDialogFragment extends WarehouseDialogFragment implements View.OnClickListener{
    private CompUomData compUomData = null;
    private double palletConversion = 0.0;
    private static final String STATE_COM_UOM_DATA = "compUomData";
    private static final String STATE_PALLET_CONVERSION = "palletConversion";
    private static final String STATE_UOM_ID_LIST = "uomIdList";
    private static final String STATE_UOM_VALUE_LIST = "uomValueList";
    private static final String STATE_INPUT_LISTENER_VALUE_LIST = "uomInputListener";

    private LinearLayout linearLayoutCompUom;
    private ProgressBar progressBar;
    private UomInputListener inputListener;
    private List<TempUomItem> itemListStarter;

    public static CompUomInputDialogFragment newInstance(UomInputListener uomInputListener, CompUomData data, double palletConversion, String uomId, String uomValue){
        CompUomInputDialogFragment fragment = new CompUomInputDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(STATE_COM_UOM_DATA, data);
        args.putSerializable(STATE_INPUT_LISTENER_VALUE_LIST, uomInputListener);
        args.putDouble(STATE_PALLET_CONVERSION, palletConversion);
        args.putString(STATE_UOM_ID_LIST, uomId);
        args.putString(STATE_UOM_VALUE_LIST, uomValue);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null)
            getAllDataFromBundle(getArguments());

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        try {
            LayoutInflater inflater = this.activity.getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_compuom_input, null);
            initComponent(view);
            setUpCompUomToView(inflater);
            setProgressBarVisibility(false);

            return new AlertDialog.Builder(this.activity).setView(view).create();
        } catch (Exception e) {
            ShowErrorDialog(e);
            return null;
        }
    }

    private void getAllDataFromBundle(Bundle bundle){
        getInterfaceFromBundle(bundle);
        getCompUomFromBundle(bundle);
        getPalletConversionFromBundle(bundle);
        getDataList(bundle);
    }

    private void getInterfaceFromBundle(Bundle bundle){
        Serializable serializable = bundle.getSerializable(STATE_INPUT_LISTENER_VALUE_LIST);
        if (serializable != null)
            inputListener = (UomInputListener)serializable;
    }

    private void getDataList(Bundle bundle){
        String separator = compUomData.compUomId != null ? compUomData.separator : "/";
        String uomId = bundle.getString(STATE_UOM_ID_LIST);
        String uomValue = bundle.getString(STATE_UOM_VALUE_LIST);
        List<String> uomIdList = new ArrayList<>(Arrays.asList(uomId.split(separator)));
        List<String> uomValueList = new ArrayList<>(Arrays.asList(uomValue.split(separator)));

        this.itemListStarter = convertToTempUomItem(uomIdList, uomValueList);
    }

    private List<TempUomItem> convertToTempUomItem(List<String> uomIdList, List<String> uomValueList){
        List<TempUomItem> itemList = new ArrayList<>();
        for(int i=0; i < uomIdList.size(); i++){
            TempUomItem item = new TempUomItem();
            item.uomId = uomIdList.get(i);
            item.value = Double.valueOf(uomValueList.get(i));
            itemList.add(item);
        }

        return itemList;
    }

    private void getCompUomFromBundle(Bundle bundle){
        Serializable serializableData = bundle.getSerializable(STATE_COM_UOM_DATA);
        if(serializableData != null && serializableData instanceof Data)
            this.compUomData = (CompUomData)serializableData;
        else
            this.compUomData = new CompUomData();
    }

    private void getPalletConversionFromBundle(Bundle bundle){
        this.palletConversion = bundle.getDouble(STATE_PALLET_CONVERSION);
    }

    private void setUpCompUomToView(LayoutInflater inflater){
        for(TempUomItem data : itemListStarter){
            LinLayoutCompUom layout = (LinLayoutCompUom) inflater.inflate(R.layout.layout_comp_uom_input, null);
            layout.setData(data);
            linearLayoutCompUom.addView(layout);
        }
    }

    private void initComponent(View view){
        linearLayoutCompUom = view.findViewById(R.id.linearLayoutCompUom);
        progressBar = view.findViewById(R.id.progressBarComUom);
        Button btnProcess = view.findViewById(R.id.btnProcess);
        btnProcess.setOnClickListener(this);
    }

    private void setProgressBarVisibility(boolean showNow){
        if(showNow){
            if(progressBar.getVisibility() == View.GONE)
                progressBar.setVisibility(View.VISIBLE);
        }else{
            if(progressBar.getVisibility() == View.VISIBLE)
                progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnProcess:
                if(inputListener != null)
                    inputListener.onInputUomFinished(getNewInputQty());

                activity.HideSoftInputKeyboard();
                dismiss();
        }
    }

    private double getNewInputQty(){
        int total = 0;
        List<TempUomItem> itemList = getValueFromCompUomLayout();
        List<UomConversionData> conversionList = compUomData.uomConversions;

        for(TempUomItem item : itemList){
            for(UomConversionData conversionData : conversionList){
                if(conversionData.uomId.equals(item.uomId) && conversionData.toUomId.toLowerCase().equals("pcs")){
                    int conversionValue = (int)item.value * conversionData.value;
                    total = total + conversionValue;
                }
            }

            if(item.uomId.equals("Pallet" + (int)palletConversion))
                total = total + (int)item.value * (int)palletConversion;
        }

        return (double)total;
    }

    private List<TempUomItem> getValueFromCompUomLayout(){
        List<TempUomItem> resultList = new ArrayList<>();
        int childViews = linearLayoutCompUom.getChildCount();
        for(int i=0; i < childViews; i++){
            View view =  linearLayoutCompUom.getChildAt(i);
            if(view instanceof LinLayoutCompUom){
                LinLayoutCompUom compUomLayout = (LinLayoutCompUom)view;
                TempUomItem tempUomItem = compUomLayout.getData();
                resultList.add(tempUomItem);
            }
        }

        return resultList;
    }

    public interface UomInputListener extends Serializable{
        void onInputUomFinished(double inputValue);
    }

    public class TempUomItem{
        String uomId;
        double value;

        public TempUomItem(){
            this.uomId = "";
            this.value = 0;
        }
    }
}

