package com.gin.wms.manager;

import android.content.Context;

import com.bosnet.ngemart.libgen.Manager;
import com.gin.wms.manager.config.ManagerSetup;
import com.gin.wms.manager.config.NgRouteUrl;
import com.gin.wms.manager.db.data.StockLocationData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StockLocationManager extends Manager {

    public StockLocationManager(Context context) throws Exception {
        super(context, new NgRouteUrl().getUrlDomainSL() + "stockLocations", new UserContext(context), new ManagerSetup());
    }

    public List<StockLocationData> getStockLocationByBinPallet(String binId, String palletNo)throws Exception{
        String function = "GetStockLocationByBinPallet?binId=" + binId + "&palletNo=" + palletNo;
        List<StockLocationData> dataList = restClient.getList(StockLocationData[].class, function);
        List<StockLocationData> resultList = new ArrayList<>();
        for(StockLocationData data : dataList){
            data.updated = new Date();
            resultList.add(data);
        }

        return resultList;
    }
}
