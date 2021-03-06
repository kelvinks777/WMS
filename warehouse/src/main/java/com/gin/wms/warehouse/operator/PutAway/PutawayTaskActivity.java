package com.gin.wms.warehouse.operator.PutAway;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bosnet.ngemart.libgen.barcode_scanner.integration.IntentIntegrator;
import com.bosnet.ngemart.libgen.barcode_scanner.integration.IntentResult;
import com.gin.wms.manager.BinManager;
import com.gin.wms.manager.ProductManager;
import com.gin.wms.manager.PutawayManager;
import com.gin.wms.manager.db.data.ProductData;
import com.gin.wms.manager.db.data.PutawayData;
import com.gin.wms.warehouse.R;
import com.gin.wms.warehouse.base.WarehouseActivity;

import java.util.Calendar;
import java.util.Date;

public class PutawayTaskActivity extends WarehouseActivity {
    Button btnScanLocation, btnScanPallet;
    EditText txt_location, txt_product, txt_noPallet, txt_qty;
    LinearLayout btnContinue;

    private DatePickerDialog datePickerDialog;
    private Date selectedDate;


    public final static int SCAN_LOCATION = 1;
    public final static int SCAN_PALLET = 2;


    PutawayTaskData putAwayTaskDat;
    PutawayManager putawayManager;
    ProductManager productManager;
    BinManager binManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operator_main);

        initLayout();

        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, final int year, final int monthOfYear, final int dayOfMonth) {
                selectedDate = new Date(year, monthOfYear, dayOfMonth);
            }
        };

        datePickerDialog = new DatePickerDialog(this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        setExternalBarcodeActive(true);


        //        getPutawayTaskData();
//        setDataToUI();
    }



    private void getPutawayTaskData() {
//        todo : aslinya cuma menampilkan dummy data
//        putAwayTaskDat = putawaytaskMgr.getPutAwayTaskData();
    }

    private void initLayout() {
        btnScanLocation = (Button) findViewById(R.id.scan_barcode_button);
        btnScanPallet = (Button) findViewById(R.id.btn_scan_pallet);
        btnScanPallet.setEnabled(false);
        btnContinue = (LinearLayout) findViewById(R.id.btnContinue);
        txt_location = (EditText) findViewById(R.id.result);
        txt_location.setEnabled(false);
        txt_product = (EditText) findViewById(R.id.txt_product);
        txt_product.setEnabled(false);
        txt_noPallet = (EditText) findViewById(R.id.txt_noPallet);
        txt_noPallet.setEnabled(false);
        txt_qty = (EditText) findViewById(R.id.txt_qty);
        txt_qty.setEnabled(false);

        btnScanLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LaunchBarcodeScanner(SCAN_LOCATION);
            }
        });

        btnScanPallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LaunchBarcodeScanner(SCAN_PALLET);
            }
        });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    CreatePutAwayWithStaging();
                } catch (Exception e) {
                    showErrorDialog(e);
                }
            }
        });
    }


    private void CreatePutAwayWithStaging() throws Exception {
        ThreadStart(new ThreadHandler<PutawayData.PutawayTaskData>() {
            @Override
            public void onPrepare() throws Exception {

            }

            @Override
            public PutawayData.PutawayTaskData onBackground() throws Exception {
                ProductData productData = productManager.getProduct(txt_product.getText().toString(),"");
                double qty = productData.getHelper().getTotalFromCompUomValue(txt_qty.getText().toString());
                putawayManager.createPutAwayWithStaging("0001",txt_location.getText().toString(),txt_product.getText().toString(),qty,txt_noPallet.getText().toString(),"refdoc07", selectedDate);
                return null;
            }

            @Override
            public void onError(Exception e) {
                showErrorDialog(e);
            }

            @Override
            public void onSuccess(PutawayData.PutawayTaskData data) throws Exception {
                Intent intent = new Intent(PutawayTaskActivity.this, PutawayActivity.class);
                intent.putExtra("operatorId", "operat01");
                intent.putExtra("putawayId", data.PutawayId);
                intent.putExtra("Dest", data.DestBinId);
                startActivity(intent);

            }

            @Override
            public void onFinish() {

            }
        });
    }

    private void setDataToUI() {
        txt_location.setText(putAwayTaskDat.location);
    }

    private void validateLocationWithBarcodeValue(String barcodeId) {
        if(barcodeId.equals(putAwayTaskDat.location)){
            Toast.makeText(this, "Location verified as true ", Toast.LENGTH_SHORT).show();
            btnScanPallet.setEnabled(true);
        }else{
            Toast.makeText(this, "Location verified as false", Toast.LENGTH_SHORT).show();
        }
    }

    private void validatePalletWithBarcodeValue() {
        txt_product.setText(putAwayTaskDat.pallet.product);
        txt_noPallet.setText(putAwayTaskDat.pallet.palletNo);
        txt_qty.setText(String.valueOf(putAwayTaskDat.pallet.qty));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null) {
                if (result.getContents() != null) {
                    String barcodeId = result.getContents();
                    if (requestCode == SCAN_PALLET) {
                        validatePalletWithBarcodeValue();
                    } else if (requestCode == SCAN_LOCATION) {
                        validateLocationWithBarcodeValue(barcodeId);
                    }
                }
            }
        } catch (Exception e) {
            showErrorDialog(e);
        }
    }
}
