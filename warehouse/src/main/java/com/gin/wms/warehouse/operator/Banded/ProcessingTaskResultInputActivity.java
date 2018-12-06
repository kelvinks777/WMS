package com.gin.wms.warehouse.operator.Banded;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.bosnet.ngemart.libgen.barcode_scanner.integration.IntentIntegrator;
import com.bosnet.ngemart.libgen.barcode_scanner.integration.IntentResult;
import com.gin.wms.manager.ProcessingTaskManager;
import com.gin.wms.manager.db.data.ProcessingTaskItemData;
import com.gin.wms.manager.db.data.ProcessingTaskItemResultData;
import com.gin.wms.manager.db.data.enums.RefDocUriEnum;
import com.gin.wms.warehouse.R;
import com.gin.wms.warehouse.base.WarehouseActivity;
import com.gin.wms.warehouse.component.CompUomInterpreter;

import java.text.MessageFormat;

public class ProcessingTaskResultInputActivity extends WarehouseActivity implements View.OnClickListener {
    private ProcessingTaskManager processingTaskManager;
    private ProcessingTaskItemData processingTaskItemData;
    private EditText palletNo;
    private Button btnSave;
    private CompUomInterpreter compUomInterpreter;
    private ImageButton bntScanPallet, btnSearchPallet;
    private RefDocUriEnum refDocUri;

    //region Override Functions

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_processing_task_result_input);
            initComponent();
        } catch (Exception e) {
            showErrorDialog(e);
        }
    }

    @Override
    public void onClick(View v) {
        try{
            switch (v.getId()){
                case R.id.btnScanBandedPallet:
                    LaunchBarcodeScanner();
                    break;
                case R.id.btnSearchBandedPallet:
                    if (palletNo.getText().toString().isEmpty()) {
                        showErrorDialog("Pallet no tidak boleh kosong");
                        return;
                    }
                    searchPalletThread(palletNo.toString());
                    break;
                case R.id.btnAddBanded:
                    if (palletNo.getText().toString().isEmpty()) {
                        showErrorDialog("Pallet no tidak boleh kosong");
                        return;
                    }
                    if (compUomInterpreter.getQty() == 0.0) {
                        showErrorDialog("Anda belum mengisi Qty");
                        return;
                    }
                    if (compUomInterpreter.getQty() >  compUomInterpreter.getPalletConversion()) {
                        showErrorDialog("Jumlah barang lebih dari nilai per palet.");
                        return;
                    }
                    else {
                        saveCheckResultThreadConfirmation();
                    }
                    break;
            }
        }catch (Exception e){
            showErrorDialog(e);
        }
    }

    @Override
    public void onBackPressed() {
        moveBackToTaskProcessingActivity();
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null) {
                if (result.getContents() != null) {
                    String barcodeId = result.getContents();
                    palletNo.setText(barcodeId);

                    if (refDocUri == RefDocUriEnum.BANDED) {
                        searchPalletThread(barcodeId);
                    }
                }
            }
        } catch (Exception e) {
            showErrorDialog(e);
        }
    }
    //endregion

    //region Init
    private void initComponent() {
        setExternalBarcodeActive(true);
        bntScanPallet = findViewById(R.id.btnScanBandedPallet);
        btnSearchPallet = findViewById(R.id.btnSearchBandedPallet);
        btnSave = findViewById(R.id.btnAddBanded);
        palletNo = findViewById(R.id.etPalletNo);
        compUomInterpreter = findViewById(R.id.compUomInterpreterBanded);

        bntScanPallet.setOnClickListener(this);
        btnSearchPallet.setOnClickListener(this);
        btnSave.setOnClickListener(this);

        initToolbar();
    }

    private void initToolbar() {
        android.support.v7.widget.Toolbar toolbarTitle = findViewById(R.id.toolbarInputBanded);
        toolbarTitle.setTitle("Input Banded");
        setSupportActionBar(toolbarTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    //endregion

    //region Other Function
    private void moveBackToTaskProcessingActivity(){
        Intent intent = new Intent(this, ProcessingTaskCheckActivity.class);
        startActivity(intent);
        finish();
    }

    private DialogInterface.OnClickListener addThread(){
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    checkResultAddThread();
                } catch (Exception e) {
                    showErrorDialog(e);
                }
            }
        };
    }

    //endregion

    //region UI Functions

    private void clearUi() {
        palletNo.setText("");
        compUomInterpreter.setQty(processingTaskItemData.palletConversionValue);
        HideSoftInputKeyboard();

        if (refDocUri == RefDocUriEnum.BANDED) {
            btnSearchPallet.setVisibility(View.VISIBLE);
        } else {
            btnSearchPallet.setVisibility (View.GONE);
        }
    }

    //endregion

    //region Thread
    private void searchPalletThread(String palletNo) {
        ThreadStart(new ThreadHandler<Object>() {
            @Override
            public void onPrepare() throws Exception{
                startProgressDialog(getString(R.string.common_progress_content), ProgressType.SPINNER);
            }

            @Override
            public Object onBackground() throws Exception{
                return null;//checkerTaskManager.getReleaseCheckResultFromStockLocation(taskId, palletNo, productId);
            }

            @Override
            public void onError(Exception e){
                showErrorDialog(e);
            }

            @Override
            public void onSuccess(Object data) {
                //if (data != null) {
                //    palletNo.setText(data.palletNo);
                //compUomInterpreter.getQty() = data.qty
                //} else {
                 String message = getString(R.string.wrong_pallet);
                 showErrorDialog(MessageFormat.format(message, palletNo));
                //}
            }

            @Override
            public void onFinish() {
                dismissProgressDialog();
            }
        });
    }

    private void saveCheckResultThreadConfirmation() {
        DialogInterface.OnClickListener okListener = addThread();
        showAskDialog(getString(R.string.common_confirmation_title), "Apakah anda ingin menyimpan data pallet ini?", okListener, null);
    }

    private void checkResultAddThread()throws Exception{
        ThreadStart(new ThreadHandler <Object> () {
        @Override
            public void onPrepare() {
                //startProgressDialog(getString(R.string.common_progress_content), ProgressType.SPINNER);
            }

            @Override
            public Object onBackground()throws Exception{
                //double Qty = compUomInterpreter.qty;
                return null;//processingTaskManager.createResult(taskId, productId, clientLocationId, palletNo.getText().toString(), Qty);
            }

            @Override
            public void onError(Exception e) {
                showErrorDialog(e);
            }

            @Override
            public void onSuccess(Object data) throws Exception {
                //if (data!=null)
                    clearUi();
                //else
                    //showErrorDialog("No pallet sudah ada");
            }

            @Override
            public void onFinish() {
                dismissProgressDialog();
            }
        });
    }
    //endregion
}
