package com.gin.wms.manager.db.data;

import com.bosnet.ngemart.libgen.Contract;
import com.bosnet.ngemart.libgen.Data;
import com.gin.wms.manager.db.contract.MovingTaskSourceItemContract;

import java.io.Serializable;
import java.util.Date;

public class MovingTaskSourceItemData extends Data implements Serializable{
    public String movingId;
    public String productId;
    public String sourceBinId;
    public String palletNo;
    public Date expiredDate;
    public double qty;

    @Override
    public Contract getContract() throws Exception {
        return new MovingTaskSourceItemContract();
    }

    @Override
    public String toString() {
        return "MovingTaskSourceItemData{" +
                "movingId='" + movingId + '\'' +
                ", productId='" + productId + '\'' +
                ", sourceBinId='" + sourceBinId + '\'' +
                ", palletNo='" + palletNo + '\'' +
                ", dtmExpired=" + expiredDate +
                ", decQty=" + qty +
                '}';
    }
}
