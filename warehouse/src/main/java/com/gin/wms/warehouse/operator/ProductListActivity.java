package com.gin.wms.warehouse.operator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.gin.ngemart.baseui.component.NgemartSimpleListView;
import com.gin.wms.manager.CheckerTaskManager;
import com.gin.wms.manager.db.contract.base.ProductBaseContract;
import com.gin.wms.manager.db.data.CheckerTaskItemData;
import com.gin.wms.manager.db.data.ProductData;
import com.gin.wms.warehouse.R;
import com.gin.wms.warehouse.base.WarehouseActivity;

import java.util.ArrayList;
import java.util.List;

public class ProductListActivity extends WarehouseActivity {
    public final static int REQ_CODE = 1001;
    public final static String ARG_SELECTED_PRODUCT = "ARG_SELECTED_PRODUCT";
    public final static String ARG_SELECTED_CLIENT_LOCATION = "ARG_SELECTED_CLIENT_LOCATION";

    private NgemartSimpleListView<CheckerTaskItemData> lvProduct;

    ProgressBar pbSpinner;
    List<CheckerTaskItemData> productList ;
    CheckerTaskManager checkerTaskManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_product_list);

            initObject();
            initComponent();
            initData();
        } catch (Exception e) {
            showErrorDialog(e);
        }
    }


    private void initObject() throws Exception {
        checkerTaskManager = new CheckerTaskManager(this);
    }

    private void initComponent() throws Exception {
        initToolbar();
        pbSpinner = findViewById(R.id.pbSpinner);
        lvProduct = findViewById(R.id.lvProduct);
        productList = new ArrayList<>();
        lvProduct.initiate(productList, ProductBaseContract.Column.PRODUCT_NAME, ProductBaseContract.Column.PRODUCT_ID, "");
        lvProduct.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = new Intent();
            CheckerTaskItemData checkerTaskItemData = productList.get(i);
            intent.putExtra(ARG_SELECTED_PRODUCT, checkerTaskItemData.productId);
            intent.putExtra(ARG_SELECTED_CLIENT_LOCATION, checkerTaskItemData.clientLocationId);
            setResult(RESULT_OK, intent);
            DismissActivity();
        });
    }

    private void initToolbar() throws Exception {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Choose Product");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initData() throws Exception {
        ThreadStart(new ThreadHandler<List<CheckerTaskItemData>>() {
            @Override
            public void onPrepare() throws Exception {
                pbSpinner.setVisibility(View.VISIBLE);
            }

            @Override
            public List<CheckerTaskItemData> onBackground() throws Exception {
                return checkerTaskManager.getLocalTaskItemByUnloader();
            }

            @Override
            public void onError(Exception e) {
                showErrorDialog(e);
            }

            @Override
            public void onSuccess(List<CheckerTaskItemData> data) throws Exception {
                productList.clear();
                productList.addAll(data);
                lvProduct.notifyDataSetChanged();
            }

            @Override
            public void onFinish() {
                pbSpinner.setVisibility(View.GONE);
            }
        });
    }
}
