package com.gin.wms.warehouse.component;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gin.wms.warehouse.R;
import com.gin.wms.warehouse.base.WarehouseDialogFragment;

import java.text.MessageFormat;

public class SingleInputDialogFragment extends WarehouseDialogFragment{
    private TextView tvInputTitle;
    private EditText txtInput;
    private Button btnInputAction;
    private InputManualInterface inputManualHandler;
    private String title = "";
    private String customTitle = "";
    private int TYPE_CODE = 0;
    private String previousValue = "";

    public static SingleInputDialogFragment newInstance(int typeCode, String title, String previousValue){
        SingleInputDialogFragment fragment = new SingleInputDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putInt("typeCode", typeCode);
        bundle.putString("previousValue", previousValue);
        fragment.setArguments(bundle);

        return fragment;
    }

    public static SingleInputDialogFragment newInstanceWithCustomTitle(int typeCode, String customTitle, String previousValue){
        SingleInputDialogFragment fragment = new SingleInputDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("typeCode", typeCode);
        bundle.putString("previousValue", previousValue);
        bundle.putString("customTitle", customTitle);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            inputManualHandler = (InputManualInterface)getActivity();
            Bundle bundle = getArguments();
            if(bundle != null){
                TYPE_CODE = bundle.getInt("typeCode");
                title = getArguments().getString("title");
                previousValue = getArguments().getString("previousValue");
                customTitle = getArguments().getString("customTitle");
            }
        } catch (Exception e){
            throw new ClassCastException("Calling fragment must implement InputPalletNumberInterface");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        try{
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_single_input, null);
            initComponent(view);

            return new AlertDialog.Builder(getActivity())
                    .setView(view)
                    .create();
        }catch (Exception ex){
            ShowErrorDialog(ex);
            return null;
        }
    }

    private void initComponent(View view){
        tvInputTitle = view.findViewById(R.id.tvInputTitle);
        txtInput = view.findViewById(R.id.txtInput);
        btnInputAction = view.findViewById(R.id.btnInput);
        initButtonOnClickListener();
        setDataFromConstructorToUi();
    }

    private void setDataFromConstructorToUi(){
        String strFirst = getResources().getString(R.string.input_pallet_number_manually);
        String strTitle = MessageFormat.format(strFirst,this.title);
        if(customTitle != null)
            tvInputTitle.setText(customTitle);
        else
            tvInputTitle.setText(strTitle);

        txtInput.setText(previousValue);
    }

    private void initButtonOnClickListener(){
        btnInputAction.setOnClickListener(v -> {
            String palletNumber = txtInput.getText().toString();
            if(palletNumber.equals("")){
                txtInput.setError(getResources().getString(R.string.error_cannot_input_text_empty));
            }else{
                inputManualHandler.onTextInput(TYPE_CODE, palletNumber);
                activity.HideSoftInputKeyboard();
                dismiss();
            }
        });
    }

    public interface InputManualInterface{
        void onTextInput(int typeCode, String inputText);
    }

}
