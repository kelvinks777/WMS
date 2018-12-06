package com.gin.wms.warehouse.operator.PutAway;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bosnet.ngemart.libgen.barcode_scanner.integration.IntentIntegrator;
import com.bosnet.ngemart.libgen.barcode_scanner.integration.IntentResult;
import com.gin.wms.manager.PutawayTaskManager;
import com.gin.wms.manager.db.data.PutawayData;
import com.gin.wms.warehouse.R;
import com.gin.wms.warehouse.base.WarehouseActivity;

public class PutawayActivity extends WarehouseActivity {
    Button btnScanLocation, btnFinish ;
    EditText txt_location;
    TextView btnBack;

    PutawayData putawayDat;
    PutawayTaskManager putawayTaskManager;

    String operatorId, SourceBinId, ProductId, palletNo, ReceivingDocumentId, destBin, putawayId;
    Integer Qty;
//    private PutawayInterface putawaymgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_putaway);
            putawayTaskManager = new PutawayTaskManager(this);
            initLayout();
            getdatafromintent();
            setExternalBarcodeActive(true);
            getPutawayData();
            setDataToUI();
        } catch (Exception e) {
            showErrorDialog(e);
        }
    }

    private void initLayout() {
        btnScanLocation = (Button) findViewById(R.id.scan_barcode_button1);
        btnFinish = (Button) findViewById(R.id.btn_finish);
        btnFinish.setEnabled(false);
        btnBack = (TextView) findViewById(R.id.btnback);
        txt_location = (EditText) findViewById(R.id.result1);
//        putawaymgr = ObjectFactory.createObject(PutawayInterface.class);

        btnScanLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LaunchBarcodeScanner();
            }
        });

        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    FinishPutawayTask();
                } catch (Exception e) {
                    showErrorDialog(e);
                }

            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void FinishPutawayTask() throws Exception {
        ThreadStart(new ThreadHandler<Object>() {
            @Override
            public void onPrepare() throws Exception {
                startProgressDialog(getString(R.string.common_progress_content), ProgressType.SPINNER);
            }

            @Override
            public Object onBackground() throws Exception {
                putawayTaskManager.finishPutawayTask("refdoct003","Jakarta");
                return null;
            }

            @Override
            public void onError(Exception e) {
                dismissProgressDialog();
                showErrorDialog(e);
            }

            @Override
            public void onSuccess(Object data) throws Exception {
                dismissProgressDialog();
                DismissActivity();
            }

            @Override
            public void onFinish() {

            }
        });
    }

    private void getPutawayData(){
//        putawayDat = putawaymgr.getPutAwayData();
    }


    private void setDataToUI() {

        txt_location.setText(putawayDat.location);
    }

    private void validateLocationWithBarcodeValue(String barcodeId) {
        if(barcodeId.equals(putawayDat.location)){
            Toast.makeText(this, "Location verified as true ", Toast.LENGTH_SHORT).show();
            btnFinish.setEnabled(true);
        }else{
            Toast.makeText(this, "Location verified as false", Toast.LENGTH_SHORT).show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null) {
                if (result.getContents() != null) {
                    String barcodeId = result.getContents();
                    validateLocationWithBarcodeValue(barcodeId);
                }
            }
        } catch (Exception e) {
            showErrorDialog(e);
        }
    }

    private void getdatafromintent() {
        Intent i = getIntent();
        operatorId  = (String) i.getSerializableExtra("operatorId");
        putawayId = (String) i.getSerializableExtra("putawayId");
        destBin = (String) i.getSerializableExtra("Dest");
    }
}
