package com.gin.wms.manager.db.data;

import com.bosnet.ngemart.libgen.Contract;
import com.bosnet.ngemart.libgen.Data;
import com.gin.wms.manager.db.contract.WarehouseProblemContract;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Date;

public class WarehouseProblemData extends Data implements Serializable {
    public String binId;
    public String palletNo;
    public String productId;
    public String operatorId;
    public String operatorName;
    public int Type;
    public int Status;
    public int Action;
    public Date InputTime;

    @Override
    public Contract getContract() throws Exception {
        return new WarehouseProblemContract();
    }

    @Override
    public String toString() {
        return "warehouseProblemData{"+
                "binId='"+ binId + '\'' +
                ", palletNo='" + palletNo + '\'' +
                ", productId='" + productId + '\'' +
                ", operatorId='" + operatorId + '\'' +
                ", operatorName='" + operatorName + '\'' +
                ", Type=" + Type +
                ", Status=" + Status +
                ", Action=" + Action +
                ", InputTime=" + InputTime +
                '}';
    }

}
