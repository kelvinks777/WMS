package com.gin.wms.warehouse.operator.Docking;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.widget.TextView;

import com.bosnet.ngemart.libgen.DateUtil;
import com.gin.ngemart.baseui.AsyncTaskResult;
import com.gin.ngemart.baseui.component.NgemartCardView;
import com.gin.wms.manager.ProductManager;
import com.gin.wms.manager.db.data.CompUomData;
import com.gin.wms.manager.db.data.CompUomItemData;
import com.gin.wms.manager.db.data.ProductData;
import com.gin.wms.manager.db.data.StockLocationData;
import com.gin.wms.manager.db.data.helper.CompUomHelper;
import com.gin.wms.warehouse.R;
import com.gin.wms.warehouse.component.CompUomInterpreter;

public class MoveToDockingItemCard <T extends StockLocationData> extends NgemartCardView<T> {
    private TextView tvProductName, tvExpiredDate;
    //private TextView tvQty;
    private StockLocationData stockLocationData;
    private ProductManager productManager;
    private CompUomInterpreter compUomInterpreter;

    public MoveToDockingItemCard(Context context) {
        super(context);
    }

    public MoveToDockingItemCard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MoveToDockingItemCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setData(T data) {
        try{
            super.setData(data);
            this.stockLocationData = data;
            initObject();
            initComponent();
            new ShowDataToUITrhread().execute();
        }catch (Exception e){
            ShowError(e);
        }
    }

    private void initObject()throws Exception{
        productManager = new ProductManager(getContext());
    }

    private void initComponent(){
        tvProductName = findViewById(R.id.tvProductName);
        //tvQty = findViewById(R.id.tvQty);
        tvExpiredDate = findViewById(R.id.tvExpiredDate);
        compUomInterpreter = findViewById(R.id.compUomInterpreter);
    }

    private void setUpCompUomInterpreter(double palletConversionValue, CompUomData compUomData, double qty){
        compUomInterpreter.setPalletConversion(palletConversionValue);
        compUomInterpreter.setCompUomData(compUomData);
        compUomInterpreter.setQty(qty);
        compUomInterpreter.setEditButtonActive(false);
    }

    private void clearUi(){
        tvProductName.setText("");
        //tvQty.setText("");
        tvExpiredDate.setText("");
    }

    private void setDataToUi(ProductData productData){
        clearUi();
        CompUomHelper helper = new CompUomHelper(productData.getCompUom());
        CompUomItemData compUomItemData = helper.getUomTail();
        String strQty = String.valueOf((int)this.stockLocationData.qty);
        String qty = strQty + " " + compUomItemData.uomId;

        tvProductName.setText(productData.productName);
        //tvQty.setText(qty);

        String dateFormat = "dd-MM-yyyy";
        tvExpiredDate.setText(DateUtil.DateToStringFormat(dateFormat,this.stockLocationData.expiredDate));
        setUpCompUomInterpreter(productData.palletConversionValue, productData.getCompUom(), this.stockLocationData.qty);
    }

    private class ShowDataToUITrhread extends AsyncTask<String, Void, AsyncTaskResult<ProductData>> {
        @Override
        protected AsyncTaskResult<ProductData> doInBackground(String... strings) {
            AsyncTaskResult<ProductData> result = new AsyncTaskResult<>();
            try{
                if(productManager != null){
                    String productId = stockLocationData.productId;
                    String clientId = stockLocationData.clientId;

                    ProductData productData = productManager.getProduct(productId);

                    if(productData != null)
                        result.data = productData;
                    else
                       result.data = productManager.getProductFromServer(clientId, productId);
                }
            }catch (Exception e){
                result.isError = true;
                result.exception = e;
            }

            return result;
        }

        @Override
        protected void onPostExecute(AsyncTaskResult<ProductData> result) {
            if(result.isError){
                ShowError(result.exception);
            }else{
                setDataToUi(result.data);
            }
        }
    }
}
