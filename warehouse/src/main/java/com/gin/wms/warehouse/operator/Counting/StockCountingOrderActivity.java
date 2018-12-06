package com.gin.wms.warehouse.operator.Counting;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.gin.wms.manager.StockcountingOrderManager;
import com.gin.wms.manager.db.data.StockCountingOrderItemData;
import com.gin.wms.warehouse.R;
import com.gin.wms.warehouse.base.WarehouseActivity;

import java.io.Serializable;
import java.util.List;

public class StockCountingOrderActivity extends WarehouseActivity  implements View.OnClickListener{
    private EditText edtBinId,edtOperatorId;
    private StockcountingOrderManager stockcountingOrderManager;
    private StockCountingOrderItemData stockCountingOrderItemData;
    private List<StockCountingOrderItemData> listStockCountingItemData;
    private List<StockCountingOrderItemData> listStockCountingItemData1;
    private Button btnAddStockcountingOrderItem,btnListStockcountingOrderItem;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_order_manual_stockcounting);
            initManager();
            initComponent();
        }catch (Exception e){
            showErrorDialog(e);
        }
    }

    private void initManager() throws Exception{
        stockcountingOrderManager= new StockcountingOrderManager(getApplicationContext());
    }

    private void initComponent(){
        initToolbar();
        justBindingProperties();
        initPropertyHandler();
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbarStockcountingOrderItemManual);
        toolbar.setTitle(getResources().getString(R.string.receiving_stockcounting_order_item_title));
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void justBindingProperties(){
        edtBinId=findViewById(R.id.edtBinId);
        edtOperatorId=findViewById(R.id.edtOperatorId);
        btnAddStockcountingOrderItem=findViewById(R.id.btnAddStockcountingOrderItem);
        btnListStockcountingOrderItem=findViewById(R.id.btnListStockcountingOrderItem);
    }

    private boolean validateStockcountingOrderItem(){
        String warning=getResources().getString(R.string.warning_data_should_be_filled_stockcountingorder);
        boolean isValid= true;
        if(edtBinId.getText().toString().equals("")){
            edtBinId.setError(warning);
            isValid=false;
        }
        return isValid;
    }

    private void initPropertyHandler(){
        btnAddStockcountingOrderItem.setOnClickListener(this);
        btnListStockcountingOrderItem.setOnClickListener(this);
    }

    private void clearUI(){
        edtBinId.setText("");
        edtOperatorId.setText("");
        edtBinId.requestFocus();
    }

    private StockCountingOrderItemData prepareStockCountingOrderItemData(){
        StockCountingOrderItemData preparedData= new StockCountingOrderItemData();
        preparedData.binId=edtBinId.getText().toString();
        preparedData.operatorId=edtOperatorId.getText().toString();
        return preparedData;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnAddStockcountingOrderItem:
                try{
                    if(validateStockcountingOrderItem()){
                        stockCountingOrderItemData=prepareStockCountingOrderItemData();
                        saveLocalStockcountingOrderItemData(stockCountingOrderItemData);
                    }
                }catch (Exception e) {
                    dismissProgressDialog();
                }
                break;
            case R.id.btnListStockcountingOrderItem:
                try{
                    showToListStockCountingOrder();
                }catch (Exception e){
                    dismissProgressDialog();
                }
                break;
        }
    }

    private void saveLocalStockcountingOrderItemData(StockCountingOrderItemData stockCountingOrderItemData) throws Exception{
        ThreadStart(new ThreadHandler<Boolean>() {

            @Override
            public void onPrepare() throws Exception {
                startProgressDialog(getString(R.string.stockcounting_order_item_saving), ProgressType.SPINNER);
            }

            @Override
            public Boolean onBackground() throws Exception {
                stockcountingOrderManager.saveStockCountingOrderItemDataLocal(stockCountingOrderItemData);
                return true;
            }

            @Override
            public void onError(Exception e) {
                progressDialog.dismiss();
                showErrorDialog(e);
            }

            @Override
            public void onSuccess(Boolean data) throws Exception {
                showInfoSnackBar(getResources().getString(R.string.stockcounting_order_item_succesfully_saved));
                clearUI();
            }

            @Override
            public void onFinish() {
                dismissProgressDialog();
            }
        });
    }

    private void moveToListStockCountingOrder() throws Exception{
        ThreadStart(new ThreadHandler<Boolean>() {
            @Override
            public void onPrepare() throws Exception {
                startProgressDialog(getString(R.string.stockcounting_order_item_list),ProgressType.SPINNER);
            }

            @Override
            public Boolean onBackground() throws Exception {
                listStockCountingItemData1=stockcountingOrderManager.getStockCountingOrderItemDataLocal();
                return true;
            }

            @Override
            public void onError(Exception e) {
                progressDialog.dismiss();
                showErrorDialog(e);
            }

            @Override
            public void onSuccess(Boolean data) throws Exception {
//                showToListStockCountingOrder(listStockCountingItemData);
            }

            @Override
            public void onFinish() {
                dismissProgressDialog();
            }
        });
    }

    private void showToListStockCountingOrder(){
        try {
            listStockCountingItemData1=stockcountingOrderManager.getStockCountingOrderItemDataLocal();
            Intent intent=new Intent(this,StockcountingOrderItemListActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(StockcountingOrderItemListActivity.LIST_STOCKCOUNTING_ORDER_ITEM, (Serializable) listStockCountingItemData1);
            intent.putExtras(bundle);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
