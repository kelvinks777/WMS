package com.gin.wms.warehouse.operator.Moving;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bosnet.ngemart.libgen.DateUtil;
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

public class MovingDestItemCard <T extends MovingTaskDestItemData> extends NgemartCardView<T> {
    private TextView tvProductName, tvQty, tvExpiredDate;
    private MovingTaskDestItemData destItemData;
    private ProductManager productManager;
    private MovingTaskManager movingTaskManager;
    private ProgressBar progressbar;

    public MovingDestItemCard(Context context) {
        super(context);
    }

    public MovingDestItemCard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MovingDestItemCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setData(T data) {
        try{
            super.setData(data);
            this.destItemData = data;
            initObject();
            initComponent();
            new ShowDataToUITrhread().execute();
        }catch (Exception e){
            ShowError(e);
        }
    }

    private void initObject()throws Exception{
        productManager = new ProductManager(getContext());
        movingTaskManager = new MovingTaskManager(getContext());
    }

    private void initComponent(){
        tvProductName = findViewById(R.id.tvProductName);
        tvQty = findViewById(R.id.tvQty);
        tvExpiredDate = findViewById(R.id.tvExpiredDate);
        progressbar = findViewById(R.id.progressbar);
        setProgressBarVisibility(false);
    }

    private void setProgressBarVisibility(boolean showNow){
        if(showNow) {
            if(progressbar.getVisibility() == GONE)
                progressbar.setVisibility(VISIBLE);
        }else {
            if (progressbar.getVisibility() == VISIBLE)
                progressbar.setVisibility(GONE);
        }
    }

    private void clearUi(){
        tvProductName.setText("");
        tvQty.setText("");
        tvExpiredDate.setText("");
    }

    private class ShowDataToUITrhread extends AsyncTask<String, Void, AsyncTaskResult<ProductData>> {
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
        protected void onPostExecute(AsyncTaskResult<ProductData> result) {
            setProgressBarVisibility(false);
            if(result.isError){
                ShowError(result.exception);
            }else{
                if(result.data != null)
                    setDataToUi(result.data);
            }
        }
    }

    private void setDataToUi(ProductData productData){
        clearUi();
        CompUomHelper helper = new CompUomHelper(productData.getCompUom());
        CompUomItemData compUomItemData = helper.getUomTail();
        String strQty = String.valueOf((int)this.destItemData.qty);
        String qty = strQty + " " + compUomItemData.uomId;

        tvProductName.setText(productData.productName);
        tvQty.setText(qty);

        String dateFormat = "dd-MM-yyyy";
        tvExpiredDate.setText(DateUtil.DateToStringFormat(dateFormat,this.destItemData.expiredDate));
    }
}