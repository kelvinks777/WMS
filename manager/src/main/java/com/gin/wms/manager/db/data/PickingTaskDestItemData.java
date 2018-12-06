package com.gin.wms.manager.db.data;

import com.bosnet.ngemart.libgen.Contract;
import com.bosnet.ngemart.libgen.Data;
import com.gin.wms.manager.db.contract.PickingTaskDestItemContract;
import com.gin.wms.manager.db.data.base.ProductBaseData;

import org.joda.time.DateTime;

import java.util.Date;

/**
 * Created by bintang on 4/26/2018.
 */

public class PickingTaskDestItemData extends ProductBaseData {
    public String pickingTaskId = "";
    public String destBinId = "";
    public String palletNo = "";
    public double qty = 0;

    @Override
    public Contract getContract() throws Exception {
        return new PickingTaskDestItemContract();
    }

    @Override
    public String toString() {
        return "PickingTaskDestItemData{" +
                "pickingTaskId='" + pickingTaskId + '\'' +
                ", destBinId='" + destBinId + '\'' +
                ", palletNo='" + palletNo + '\'' +
                ", qty=" + qty +
                '}';
    }
}
