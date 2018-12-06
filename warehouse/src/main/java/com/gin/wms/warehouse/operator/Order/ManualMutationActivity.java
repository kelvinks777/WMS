package com.gin.wms.warehouse.operator.Order;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.bosnet.ngemart.libgen.barcode_scanner.integration.IntentIntegrator;
import com.bosnet.ngemart.libgen.barcode_scanner.integration.IntentResult;
import com.gin.wms.manager.ManualMutationManager;
import com.gin.wms.manager.MovingTaskManager;
import com.gin.wms.manager.db.data.ManualMutationData;
import com.gin.wms.manager.db.data.MovingTaskDestItemData;
import com.gin.wms.manager.db.data.MovingTaskSourceItemData;
import com.gin.wms.warehouse.R;
import com.gin.wms.warehouse.base.WarehouseActivity;
import com.gin.wms.warehouse.component.CompUomInterpreter;
import com.gin.wms.warehouse.component.SingleInputDialogFragment;

import java.util.Date;

public class ManualMutationActivity extends WarehouseActivity
        implements View.OnClickListener, SingleInputDialogFragment.InputManualInterface{

    private static int SCAN_SOURCE_PALLET_CODE = 2345;
    private static int SCAN_DEST_PALLET_CODE = 5432;
    //public static final String MANUAL_MUTATION_DATA_CODE = "manualMutationData";
    public static final String MANUAL_MUTATION_DATA_CODE = "mutationData";
    private Toolbar toolbar;
    private MovingTaskManager movingTaskManager;
    private CompUomInterpreter compUomInterpreterManualMutation;
    private MovingTaskSourceItemData movingTaskSourceItemData;
    private MovingTaskDestItemData movingTaskDestItemData;
    private ManualMutationData manualMutationData;
    private ManualMutationManager manualMutationManager;
    private EditText edtProductId,edtSoucePallete,edtSouceBin, edtDestPallete, edtDestBin, edtQty;
    private Button btnMovingManualMutation;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try{
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if(result != null){
                if(resultCode == Activity.RESULT_OK)
                    validateInputAndShowToUi(requestCode, result.getContents());
            }
        }catch (Exception e){
            showErrorDialog(e);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_order_manual_mutation);
            init();
        }catch (Exception e){
            showErrorDialog(e);
        }
    }

    @Override
    public void onClick(View v) {
            switch (v.getId()){
                case R.id.btnSoucePallete:
                    LaunchBarcodeScanner(SCAN_SOURCE_PALLET_CODE);
                    break;
                case R.id.btnDestPallete:
                    LaunchBarcodeScanner(SCAN_DEST_PALLET_CODE);
                    break;
                case R.id.btnMovingManualMutation:
//                        showConfirmationToStartTask();
                    try {
                        if(validateInputTextAlreadyFilled()){
                            manualMutationData=prepareManualMutationData();
                            finishMutationManualTaskThread(manualMutationData);
                        }

                    } catch (Exception e) {
                        dismissProgressDialog();
                    }
                    break;
            }
    }

    @Override
    public void onTextInput(int typeCode, String inputText) {
        try{
            validateInputAndShowToUi(typeCode, inputText);
        }catch (Exception e){
            showErrorDialog(e);
        }
    }

    @Override
    public void onBackPressed() {
        showInfoDialog(getResources().getString(R.string.common_information_title),getResources().getString(R.string.warning_must_complete_the_task));
    }

    private void validateInputAndShowToUi(int typeCode, String inputText) throws Exception{
        if(typeCode==SCAN_SOURCE_PALLET_CODE){
            if(validateInputText(typeCode, inputText)){
                edtSoucePallete.setText(inputText);
            }else{
                String pallet_warning = getResources().getString(R.string.source_pallete_is_not_match);
                showErrorDialog(pallet_warning);
                edtSoucePallete.setText("");
            }
        }
        else if (typeCode == SCAN_DEST_PALLET_CODE){
            if(validateInputText(typeCode, inputText)){
                edtDestPallete.setText(inputText);
            }else{
                String dest_warning = getResources().getString(R.string.dest_pallete_is_not_match);
                showErrorDialog(dest_warning);
                edtDestPallete.setText("");
            }
        }
        else{
            String default_warning = getResources().getString(R.string.manual_mutation_mtypeCode_is_not_match);
            throw new ClassNotFoundException(default_warning);
        }
    }

    private void init()throws Exception{
        initObjectManager();
        initComponent();
        initToolbar();
    }

    private void initObjectManager()throws Exception{
        movingTaskManager = new MovingTaskManager(getApplicationContext());
        manualMutationManager = new ManualMutationManager(getApplicationContext());
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbarMovingManualMutationStart);
        toolbar.setTitle(getResources().getString(R.string.title_manual_mutation_start));
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

//    private void showConfirmationToStartTask(){
//        String title = getResources().getString(R.string.common_confirmation_title);
//        String body = getResources().getString(R.string.ask_to_finish_manual_mutation);
//        showAskDialog(title, body,
//                (dialog, which) -> finishMutationManualTaskThread(),
//                (dialog, which) -> dialog.dismiss());
//    }

    private boolean validateInputText(int typeCode, String inputText){
        boolean isCorrect = false;
        if(typeCode == SCAN_SOURCE_PALLET_CODE){
            if(inputText.toLowerCase().equals(movingTaskSourceItemData.palletNo.toLowerCase()))
                isCorrect = true;
        }
        else if(typeCode == SCAN_DEST_PALLET_CODE){
            if(inputText.toLowerCase().equals(movingTaskDestItemData.palletNo.toLowerCase()))
                isCorrect = true;
        }
        return isCorrect;
    }

    private void initComponent(){
        edtProductId=findViewById(R.id.edtProductId);
        edtQty=findViewById(R.id.edtQty);
        edtSoucePallete=findViewById(R.id.edtSoucePallete);
        edtSouceBin=findViewById(R.id.edtSouceBin);
        edtDestPallete=findViewById(R.id.edtDestPallete);
        edtDestBin=findViewById(R.id.edtDestBin);
        btnMovingManualMutation=findViewById(R.id.btnMovingManualMutation);


        ImageButton btnScanSource= findViewById(R.id.btnSoucePallete);
        ImageButton btnScanDest = findViewById(R.id.btnDestPallete);

        btnMovingManualMutation.setOnClickListener(this);
        btnScanSource.setOnClickListener(this);
        btnScanDest.setOnClickListener(this);

    }

    private boolean validateInputTextAlreadyFilled(){
        String warning = getResources().getString(R.string.warning_data_should_be_filled);
        boolean hasil=true;
        if(edtProductId.getText().toString().equals("")){
            edtProductId.requestFocus();
            edtProductId.setError(warning);
            hasil= false;
        }
        if(edtQty.getText().toString().equals("")){
            edtQty.requestFocus();
            edtQty.setError(warning);
            hasil= false;
        }
        if(edtSoucePallete.getText().toString().equals("")){
            edtSoucePallete.requestFocus();
            edtSoucePallete.setError(warning);
            hasil= false;
        }
        if(edtSouceBin.getText().toString().equals("")){
            edtSouceBin.requestFocus();
            edtSouceBin.setError(warning);
            hasil= false;
        }
        if(edtDestPallete.getText().toString().equals("")){
            edtDestPallete.requestFocus();
            edtDestPallete.setError(warning);
            hasil= false;
        }
        if(edtDestBin.getText().toString().equals("")){
            edtDestBin.requestFocus();
            edtDestBin.setError(warning);
            hasil= false;
        }
//        if (compUomInterpreterManualMutation.getQty() == 0.0) {
//            showErrorDialog("Anda belum mengisi Qty");
//            hasil= false;
//        }
//        if (compUomInterpreterManualMutation.getQty() >  compUomInterpreterManualMutation.getPalletConversion()) {
//            showErrorDialog("Jumlah barang lebih dari nilai per palet.");
//            hasil= false;
//        }
        return hasil;

    }

    private ManualMutationData prepareManualMutationData(){
        ManualMutationData preparedData=new ManualMutationData();

        preparedData.recOrderNo=" ";
        preparedData.trxTypeId="DocumentNumber.NO_1";
        preparedData.docStatus="1";
        preparedData.created=new Date();
        preparedData.lastUpdated=new Date();
        preparedData.orderType=2;
        preparedData.operator=1;
        preparedData.productId=edtProductId.getText().toString();
        preparedData.qty=Double.parseDouble(edtQty.getText().toString());
        preparedData.sourcePalletNo=edtSoucePallete.getText().toString();
        preparedData.sourceBinId=edtSouceBin.getText().toString();
        preparedData.destPalletNo=edtDestPallete.getText().toString();
        preparedData.destBinId=edtDestBin.getText().toString();
        preparedData.uOMId="Pcs";
        preparedData.clientId="BKS.01";
        preparedData.clientLocationId="BKS.01";
        preparedData.expiredDate=new Date();
        preparedData.refDocUri=" ";
        preparedData.finished=0;


        return preparedData;
    }

    private void finishMutationManualTaskThread(ManualMutationData manualMutationData) throws Exception{
        ThreadStart(new ThreadHandler<Boolean>() {

            @Override
            public void onPrepare() throws Exception {
                startProgressDialog(getString(R.string.progress_finish_manual_mutation), ProgressType.SPINNER);
            }

            @Override
            public Boolean onBackground() throws Exception {
                manualMutationManager.CreateManualMutationOrderToServer(manualMutationData);
                return true;
            }

            @Override
            public void onError(Exception e) {
                dismissProgressDialog();
                showErrorDialog(e);

            }

            @Override
            public void onSuccess(Boolean data) throws Exception {
                showInfoSnackBar(getResources().getString(R.string.manual_mutation_succesfully_saved));
            }

            @Override
            public void onFinish() {
                dismissProgressDialog();
            }
        });
        }

}
