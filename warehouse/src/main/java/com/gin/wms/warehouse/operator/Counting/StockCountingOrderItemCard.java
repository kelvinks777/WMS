package com.gin.wms.warehouse.operator.Counting;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gin.ngemart.baseui.AsyncTaskResult;
import com.gin.ngemart.baseui.component.NgemartCardView;
import com.gin.wms.manager.StockcountingOrderManager;
import com.gin.wms.manager.db.data.StockCountingOrderItemData;
import com.gin.wms.warehouse.R;

public class StockCountingOrderItemCard <T extends StockCountingOrderItemData>  extends NgemartCardView<T> {
    private TextView tvBinId, tvOperatorId;
    private Button btnEditItem, btnRemoveItem;
    private StockcountingOrderManager stockcountingOrderManager;
    private ProgressBar progressbar;

    public StockCountingOrderItemCard(Context context) {
        super(context);
    }

    public StockCountingOrderItemCard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StockCountingOrderItemCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setData(T data) {
        try{
            super.setData(data);
            initComponent();
            tvBinId.setText(data.binId);
            tvOperatorId.setText(data.operatorId);
//            setDataToUi(data);
        }catch (Exception e){
            ShowError(e);
        }
    }

    private void initObject()throws Exception{
        stockcountingOrderManager = new StockcountingOrderManager(getContext());
    }

    private void initComponent(){
        tvBinId = findViewById(R.id.tvBinId);
        tvOperatorId = findViewById(R.id.tvOperatorId);
        btnEditItem = findViewById(R.id.btnEditItem);
        btnRemoveItem = findViewById(R.id.btnRemoveItem);
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
        tvBinId.setText("");
        tvOperatorId.setText("");
    }

    private void setDataToUi(StockCountingOrderItemData stockCountingOrderItemDatas){
        clearUi();
//        String bin =stockCountingOrderItemData.binId;
//        String operators =stockCountingOrderItemData.operatorId;
        tvBinId.setText(stockCountingOrderItemDatas.binId);
        tvOperatorId.setText(stockCountingOrderItemDatas.operatorId);
    }

}
