package com.gin.wms.warehouse.operator.Counting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.widget.Button;

import com.gin.ngemart.baseui.adapter.NgemartRecyclerView;
import com.gin.ngemart.baseui.adapter.NgemartRecyclerViewAdapter;
import com.gin.wms.manager.StockcountingOrderManager;
import com.gin.wms.manager.db.data.StockCountingOrderItemData;
import com.gin.wms.warehouse.R;
import com.gin.wms.warehouse.base.WarehouseActivity;

import java.util.ArrayList;
import java.util.List;

public class StockcountingOrderItemListActivity extends WarehouseActivity {
    private Button btnCreateOrder;
    private NgemartRecyclerView recyclerView;
    private NgemartRecyclerViewAdapter<List<StockCountingOrderItemData>> adapter;
    private List<StockCountingOrderItemData> stockCountingOrderItemDataList = new ArrayList<>();
    private StockcountingOrderManager stockcountingOrderManager;
    private Toolbar toolbar;

    public static final String LIST_STOCKCOUNTING_ORDER_ITEM = "stockCountingOrderItem";

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_stockcounting_order);
            getIntentData();
            init();
        }catch (Exception e){
            showErrorDialog(e);
        }
    }

    private void init()throws Exception{
        initToolbar();
        justBindingProperties();
        initObjectManager();
        initAdapter();
        initRecyclerAdapter();
    }

    private void initObjectManager()throws Exception{
        stockcountingOrderManager = new StockcountingOrderManager(getApplicationContext());
    }

    private void initAdapter(){
        adapter=  new NgemartRecyclerViewAdapter(this, R.layout.card_stockcounting_order_item, stockCountingOrderItemDataList);
        adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);
    }

    private void initRecyclerAdapter(){
        int numColumn = 1;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, numColumn, GridLayoutManager.VERTICAL, false);
        recyclerView.setHasFixedSize(true);
        recyclerView.SetDefaultDecoration();
        recyclerView.setLayoutManager(gridLayoutManager);
    }

    private void justBindingProperties(){
        btnCreateOrder=findViewById(R.id.btnCreateOrder);
        recyclerView=findViewById(R.id.rvStockCountingOrder);
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbarStockcountingOrder);
        toolbar.setTitle(getResources().getString(R.string.receiving_stockcounting_order_title));
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void getIntentData(){
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            stockCountingOrderItemDataList=(List<StockCountingOrderItemData>)bundle.getSerializable(LIST_STOCKCOUNTING_ORDER_ITEM);
        }
    }


}
