package com.gin.wms.manager.db.data;

import com.bosnet.ngemart.libgen.Contract;
import com.bosnet.ngemart.libgen.Data;
import java.util.Date;

public class StockLocationData extends Data{
    public String productId;
    public String bin;
    public String palletNo;
    public String clientId;
    public String clientLocationId;
    public double qty;
    public double bookInQty;
    public double bookOutQty;
    public Date expiredDate;
    public double remainingQty;

    @Override
    public Contract getContract() throws Exception {
        return null;
    }

    @Override
    public String toString() {
        return "StockLocationData{" +
                "productId='" + productId + '\'' +
                ", bin='" + bin + '\'' +
                ", palletNo='" + palletNo + '\'' +
                ", clientId='" + clientId + '\'' +
                ", clientLocationId='" + clientLocationId + '\'' +
                ", qty=" + qty +
                ", bookInQty=" + bookInQty +
                ", bookOutQty=" + bookOutQty +
                ", expiredDate=" + expiredDate +
                ", remainingQty=" + remainingQty +
                '}';
    }
}
