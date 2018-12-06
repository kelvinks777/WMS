package com.gin.wms.manager.db.data;

import com.bosnet.ngemart.libgen.Contract;
import com.bosnet.ngemart.libgen.Data;
import com.gin.wms.manager.db.contract.MovingTaskDestItemContract;

import java.io.Serializable;
import java.util.Date;

public class MovingTaskDestItemData extends Data implements Serializable{
    public String movingId;
    public String productId;
    public String destBinId;
    public String palletNo;
    public Date expiredDate;
    public double qty;

    @Override
    public Contract getContract() throws Exception {
        return new MovingTaskDestItemContract();
    }

    @Override
    public String toString() {
        return "MovingTaskDestItemData{" +
                "movingId='" + movingId + '\'' +
                ", productId='" + productId + '\'' +
                ", destBinId='" + destBinId + '\'' +
                ", palletNo='" + palletNo + '\'' +
                ", dtmExpired=" + expiredDate +
                ", decQty=" + qty +
                '}';
    }
}
