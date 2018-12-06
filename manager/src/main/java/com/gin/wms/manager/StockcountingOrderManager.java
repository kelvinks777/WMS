package com.gin.wms.manager;

import android.content.Context;

import com.bosnet.ngemart.libgen.Manager;
import com.gin.wms.manager.config.ManagerSetup;
import com.gin.wms.manager.config.NgRouteUrl;
import com.gin.wms.manager.db.contract.StockCountingOrderItemContract;
import com.gin.wms.manager.db.data.StockCountingOrderData;
import com.gin.wms.manager.db.data.StockCountingOrderItemData;

import java.util.List;

public class StockcountingOrderManager extends Manager {

    public StockcountingOrderManager(Context context) throws Exception {
        super(context, new NgRouteUrl().getUrlDomainTask() + "StockCountingOrder", new UserContext(context), new ManagerSetup());
    }

    public void CreateStockCountingOrder(StockCountingOrderData stockCountingOrderData) throws Exception{
        String function="CreateStockCountingOrder?stockCountingOrderData";
        restClient.post(function, stockCountingOrderData);
    }

    public void saveStockCountingOrderItemDataLocal(StockCountingOrderItemData stockCountingOrderItemData) throws Exception {
        DbExecuteWrite(dbClient -> {
            dbClient.Save(stockCountingOrderItemData);
        });
    }


    public StockCountingOrderItemData getAllStockCountingOrderItemDataLocal() throws Exception{
        return DbExecuteRead(dbClient -> {
            StockCountingOrderItemData stockCountingOrderItemData=null;
            String stockCountingOrderItemTask = "SELECT * FROM " + new StockCountingOrderItemContract().getTableName() + " LIMIT 1";
            List<StockCountingOrderItemData> stockCountingOrderItemData1 = dbClient.Query(StockCountingOrderItemData.class, stockCountingOrderItemTask);
            if(stockCountingOrderItemData1.size() > 0){
                stockCountingOrderItemData=stockCountingOrderItemData1.get(0);
                String queryBinId = "SELECT * FROM " + new StockCountingOrderItemContract().getTableName()
                        + " WHERE " + StockCountingOrderItemContract.Column.BINID + " = '" + stockCountingOrderItemData.binId + "'";
                String queryOperatorId= "SELECT * FROM " + new StockCountingOrderItemContract().getTableName()
                        + " WHERE " + StockCountingOrderItemContract.Column.OPERATORID + " = '" + stockCountingOrderItemData.operatorId + "'";
                stockCountingOrderItemData.binId=queryBinId;
                stockCountingOrderItemData.operatorId=queryOperatorId;

            }
            return  stockCountingOrderItemData;
        });
    }

    public List<StockCountingOrderItemData> getStockCountingOrderItemDataLocal() throws Exception{
        List<StockCountingOrderItemData> results= DbExecuteRead(dbClient ->
            dbClient.Query(StockCountingOrderItemData.class, StockCountingOrderItemContract.Query.SELECT_ALL));
            if(results.size() == 0){
                return null;
            }else{
                return results;
            }

//        return DbExecuteRead(dbClient -> {
//                List<StockCountingOrderItemData> results=dbClient.Query(StockCountingOrderItemData.class, StockCountingOrderItemContract.Query.SELECT_ALL);
//                if(results.size() == 0){
//                    return null;
//                }
//                else{
//                    return results;
//                }
//        });
    }





}
