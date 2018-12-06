package com.gin.wms.warehouse.operator.Moving;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gin.ngemart.baseui.AsyncTaskResult;
import com.gin.ngemart.baseui.component.NgemartCardView;
import com.gin.wms.manager.MovingTaskManager;
import com.gin.wms.manager.ProductManager;
import com.gin.wms.manager.db.data.CompUomItemData;
import com.gin.wms.manager.db.data.MovingTaskData;
import com.gin.wms.manager.db.data.MovingTaskDestItemData;
import com.gin.wms.manager.db.data.ProductData;
import com.gin.wms.manager.db.data.helper.CompUomHelper;
import com.gin.wms.warehouse.R;

public class ProductListMutationItemCard extends NgemartCardView<MovingTaskDestItemData>{
    private TextView tvProduct, tvQty;
    private MovingTaskDestItemData destItemData;
    private ProductManager productManager;
    private MovingTaskManager movingTaskManager;
    private ProgressBar progressbar;

    public ProductListMutationItemCard(Context context){
        super(context);
    }

    public ProductListMutationItemCard(Context context, AttributeSet attr){
        super(context, attr);
    }

    public ProductListMutationItemCard(Context context, AttributeSet attr, int defStyleAttr){
        super(context, attr, defStyleAttr);
    }

    @Override
    public void setData(MovingTaskDestItemData data) {
        try {
            super.setData(data);
            this.destItemData = data;

            initObject();
            initComponent();

            onClickListener();
            new ShowDataToUIThread().execute();
        }catch (Exception e){
            ShowError(e);
        }
    }

    private void onClickListener() {
        setOnClickListener(v -> cardListener.onCardClick(getVerticalScrollbarPosition(),v,data));
    }

    private void initComponent() {
        tvProduct = findViewById(R.id.tvProductName);
        tvQty = findViewById(R.id.tvQuantity);
        progressbar = findViewById(R.id.progressbarProductList);
        setProgressBarVisibility(false);
    }

    private void setProgressBarVisibility(boolean show){
        if(show){
            if(progressbar.getVisibility() == GONE)
                progressbar.setVisibility(VISIBLE);
        }else {
            if (progressbar.getVisibility() == VISIBLE)
                progressbar.setVisibility(GONE);
        }
    }

    private void initObject() throws Exception{
        productManager = new ProductManager(getContext());
        movingTaskManager = new MovingTaskManager(getContext());
    }

    private class ShowDataToUIThread extends AsyncTask<String, Void, AsyncTaskResult<ProductData>> {
        @Override
        protected void onPreExecute() {
            setProgressBarVisibility(true);
        }

        @Override
        protected AsyncTaskResult<ProductData> doInBackground(String... strings) {
            AsyncTaskResult<ProductData> result = new AsyncTaskResult<>();
            try{
                if(productManager != null){
                    String productId = destItemData.productId;
                    String movingId = destItemData.movingId;
                    MovingTaskData movingTaskData = movingTaskManager.getMovingTaskFromServerById(movingId);

                    result.data = productManager.getProductFromServer(movingTaskData.clientId, productId);
                }
            }catch (Exception e){
                result.isError = true;
                result.exception = e;
            }
            return result;
        }

        @Override
        protected void onPostExecute(AsyncTaskResult<ProductData> productDataAsyncTaskResult) {
            setProgressBarVisibility(false);
            if(productDataAsyncTaskResult.isError){
                ShowError(productDataAsyncTaskResult.exception);
            }else{
                if(productDataAsyncTaskResult.data != null)
                    setDataToUi(productDataAsyncTaskResult.data);
            }
        }
    }

    private void setDataToUi(ProductData data) {
        clearUi();
        CompUomHelper helper = new CompUomHelper(data.getCompUom());
        CompUomItemData compUomItemData = helper.getUomTail();
        String strQty = String.valueOf((int)this.destItemData.qty);
        String qty = strQty + " " + compUomItemData.uomId;

        tvProduct.setText(data.productName);
        tvQty.setText(qty);
    }

    private void clearUi() {
        tvProduct.setText("");
        tvQty.setText("");
    }
}
