package com.gin.wms.manager.db.data;

import com.bosnet.ngemart.libgen.Contract;
import com.bosnet.ngemart.libgen.Data;
import com.gin.wms.manager.db.contract.StockCountingOrderItemContract;

import java.io.Serializable;

public class StockCountingOrderItemData extends Data  {
    public String binId;
    public String operatorId;

    @Override
    public Contract getContract() throws Exception {
        return new StockCountingOrderItemContract();
    }

    @Override
    public String toString() {
        return "StockCountingOrderItemData{" +
                " binId='" + binId + '\'' +
                ", operatorId='" + operatorId + '\'' +
                '}';
    }
}
