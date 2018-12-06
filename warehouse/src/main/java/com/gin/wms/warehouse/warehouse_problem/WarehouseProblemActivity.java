package com.gin.wms.warehouse.warehouse_problem;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gin.ngemart.baseui.NgemartActivity;
import com.gin.wms.manager.WarehouseProblemManager;
import com.gin.wms.manager.db.data.WarehouseProblemData;
import com.gin.wms.manager.db.data.enums.WarehouseProblemActionEnum;
import com.gin.wms.manager.db.data.enums.WarehouseProblemStatusEnum;
import com.gin.wms.manager.db.data.enums.WarehouseProblemTypeEnum;
import com.gin.wms.warehouse.R;

import com.gin.wms.warehouse.base.WarehouseActivity;

import org.joda.time.DateTime;

import java.util.Date;

public class WarehouseProblemActivity extends WarehouseActivity implements View.OnClickListener{
    private Button btnReport;
    private EditText edBinIdWarPro,edPalNoWarPro,edProIdWarPro,edOpeIdWarPro,edOpeNamWarPro;
    private WarehouseProblemManager warehouseProblemManager;
    private Toolbar toolbar;
    private WarehouseProblemData warehouseProblemData;
    private WarehouseProblemTypeEnum warehouseProblemTypeEnum;
    private WarehouseProblemStatusEnum warehouseProblemStatusEnum;
    private WarehouseProblemActionEnum warehouseProblemActionEnum;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_warehouse_problem);
            initManager();
            initComponent();
        }
        catch (Exception e){
            showErrorDialog(e);
        }
    }

    private void initManager() throws Exception{
        warehouseProblemManager= new WarehouseProblemManager(getApplicationContext());
    }

    private void initComponent(){
        initToolbar();
        justBindingProperties();
        initPropertyHandler();
    }

    private void justBindingProperties(){
        btnReport=findViewById(R.id.btnReport);
        edBinIdWarPro=findViewById(R.id.edBinIdWarPro);
        edPalNoWarPro=findViewById(R.id.edPalNoWarPro);
        edProIdWarPro=findViewById(R.id.edProIdWarPro);
        edOpeIdWarPro=findViewById(R.id.edOpeIdWarPro);
        edOpeNamWarPro=findViewById(R.id.edOpeNamWarPro);
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbarWarehouseProblem);
        toolbar.setTitle(getResources().getString(R.string.receiving_warehouse_title));
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initPropertyHandler(){
        btnReport.setOnClickListener(this);
    }


    //setonclick listener for create data
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnReport:
                    try {
                        if(validateWarehouseProblem()){
                            warehouseProblemData=prepareWarehouseProblemData();
                            JustCreateWarehouseProblem(warehouseProblemData);
                        }
                    } catch (Exception e) {
                        dismissProgressDialog();
                    }
                break;
        }

    }

    private void JustCreateWarehouseProblem(WarehouseProblemData warehouseProblemData) throws Exception {
        ThreadStart(new ThreadHandler<Boolean>() {
            @Override
            public void onPrepare() throws Exception {
                startProgressDialog(getString(R.string.warehouse_problem_saving), ProgressType.SPINNER);
            }

            @Override
            public Boolean onBackground() throws Exception {
                warehouseProblemManager.CreateWarehouseProblemToServer(warehouseProblemData);
                return true;
            }

            @Override
            public void onError(Exception e) {
                progressDialog.dismiss();
                showErrorDialog(e);
            }

            @Override
            public void onSuccess(Boolean data) throws Exception {
                showInfoSnackBar(getResources().getString(R.string.warehouse_problem_succesfully_saved));
            }

            @Override
            public void onFinish() {
                dismissProgressDialog();
            }
        });
    }

    private boolean validateWarehouseProblem(){
        String warningText = getResources().getString(R.string.warning_data_should_be_filled_warehouse_problem);
        boolean isValid= true;
            if (edBinIdWarPro.getText().toString().equals("")) {
                edBinIdWarPro.setError(warningText);
                isValid=false;
            }

            if (edPalNoWarPro.getText().toString().equals("")) {
                edPalNoWarPro.setError(warningText);
                isValid=false;
            }

            if (edProIdWarPro.getText().toString().equals("")) {
                edProIdWarPro.setError(warningText);
                isValid=false;
            }

            if (edOpeIdWarPro.getText().toString().equals("")) {
                edOpeIdWarPro.setError(warningText);
                isValid=false;
            }

            if (edOpeNamWarPro.getText().toString().equals("")) {
                edOpeNamWarPro.setError(warningText);
                isValid=false;
            }
        return isValid;

    }

    private WarehouseProblemData prepareWarehouseProblemData(){
        WarehouseProblemData preparedData=new WarehouseProblemData();

        preparedData.binId=edBinIdWarPro.getText().toString();;
        preparedData.palletNo=edPalNoWarPro.getText().toString();
        preparedData.productId=edProIdWarPro.getText().toString();
        preparedData.operatorId=edOpeIdWarPro.getText().toString();
        preparedData.operatorName=edOpeNamWarPro.getText().toString();
        preparedData.Status= 1;
        preparedData.Action= 0;
        preparedData.Type= 0;
        preparedData.InputTime=new Date();

        return preparedData;
    }
}
