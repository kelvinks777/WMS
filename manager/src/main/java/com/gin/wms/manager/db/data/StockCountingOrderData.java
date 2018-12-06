package com.gin.wms.manager.db.data;

import com.bosnet.ngemart.libgen.Contract;
import com.bosnet.ngemart.libgen.Data;
import com.gin.wms.manager.db.contract.StockCountingOrderContract;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StockCountingOrderData extends Data {
    public String Id;
    public String clientId;
    public Date docDate;
    public String longInfo;
    public boolean isFinished;
    public int Status;
    public int docType;
    public String sourceId;
    public String docRefId;
    public String docRefUri;
    public List<StockCountingOrderItemData> items = new ArrayList<>();

    @Override
    public Contract getContract() throws Exception {
        return new StockCountingOrderContract();
    }

    @Override
    public String toString() {
        return "StockCountingOrderData{"+
                "Id='"+ Id + '\'' +
                ", clientId='" + clientId + '\'' +
                ", docDate=" + docDate +
                ", longInfo='" + longInfo + '\'' +
                ", isFinished=" + isFinished +
                ", Status=" + Status +
                ", docType=" + docType +
                ", sourceId='" + sourceId + '\'' +
                ", docRefId='" + docRefId + '\'' +
                ", docRefUri='" + docRefUri + '\'' +
                ", items=" + items +
                '}';
    }
}
