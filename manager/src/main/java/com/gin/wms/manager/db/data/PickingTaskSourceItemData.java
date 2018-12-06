package com.gin.wms.manager.db.data;

import com.bosnet.ngemart.libgen.Contract;
import com.bosnet.ngemart.libgen.Data;
import com.gin.wms.manager.db.contract.PickingTaskSourceItemContract;
import com.gin.wms.manager.db.data.base.ProductBaseData;

import org.joda.time.DateTime;

import java.util.Date;

/**
 * Created by bintang on 4/26/2018.
 */

public class PickingTaskSourceItemData extends ProductBaseData {
    public String pickingTaskId = "";
    public String sourceBinId = "";
    public String palletNo = "";
    public Date expiredData = DateTime.now().toDate();
    public double qty = 0;

    @Override
    public Contract getContract() throws Exception {
        return new PickingTaskSourceItemContract();
    }

    @Override
    public String toString() {
        return "PickingTaskSourceItemData{" +
                "pickingTaskId='" + pickingTaskId + '\'' +
                ", sourceBinId='" + sourceBinId + '\'' +
                ", palletNo='" + palletNo + '\'' +
                ", expiredData=" + expiredData +
                ", qty=" + qty +
                '}';
    }
}
