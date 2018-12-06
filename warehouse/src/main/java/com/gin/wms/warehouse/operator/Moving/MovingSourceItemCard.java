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
import com.gin.wms.manager.db.data.MovingTaskSourceItemData;
import com.gin.wms.manager.db.data.ProductData;
import com.gin.wms.manager.db.data.helper.CompUomHelper;
import com.gin.wms.warehouse.R;

public class MovingSourceItemCard <T extends MovingTaskSourceItemData> extends NgemartCardView<T> {
    private TextView tvProductName, tvQty, tvExpiredDate;
    private MovingTaskSourceItemData sourceItemData;
    private ProductManager productManager;
    private MovingTaskManager movingTaskManager;
    private ProgressBar progressbar;

    public MovingSourceItemCard(Context context) {
        super(context);
    }

    public MovingSourceItemCard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MovingSourceItemCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setData(T data) {
        try{
            super.setData(data);
            this.sourceItemData = data;
            initObject();
            initComponent();
            new ShowDataToUIThread().execute();
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

    private void clearUi(){
        tvProductName.setText("");
        tvQty.setText("");
        tvExpiredDate.setText("");
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
                    String productId = sourceItemData.productId;
                    String movingId = sourceItemData.movingId;
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
        String strQty = String.valueOf((int)this.sourceItemData.qty);
        String qty = strQty + " " + compUomItemData.uomId;

        tvProductName.setText(productData.productName);
        tvQty.setText(qty);

        String dateFormat = "dd-MM-yyyy";
        tvExpiredDate.setText(DateUtil.DateToStringFormat(dateFormat,this.sourceItemData.expiredDate));
    }

}
