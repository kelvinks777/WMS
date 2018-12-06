package com.gin.wms.manager.db.data;

import com.bosnet.ngemart.libgen.Contract;
import com.bosnet.ngemart.libgen.Data;
import com.gin.wms.manager.db.contract.PutawayBookingOperatorContract;

/**
 * Created by manbaul on 2/23/2018.
 */

public class PutawayBookingOperatorData extends Data {
    public String taskId = "";
    public String policeNo = "";
    public String productId = "";
    public String id = "";
    public String name = "";
    public String policeNoBefore = "";
    public String taskIdBefore = "";


    public PutawayBookingOperatorData() {}

    public PutawayBookingOperatorData(String policeNo, String docNo, String productId, String id, String name, String policeNoBefore, String docNoBefore) {
        this.policeNo = policeNo;
        this.taskId = docNo;
        this.productId = productId;
        this.id = id;
        this.name = name;
        this.policeNoBefore = policeNoBefore;
        this.taskIdBefore = docNoBefore;
    }

    @Override
    public Contract getContract() throws Exception {
        return new PutawayBookingOperatorContract();
    }

    @Override
    public String toString() {
        return "PutawayBookingOperatorData{" +
                "policeNoBfr='" + policeNo + '\'' +
                "docNoBfr='" + taskIdBefore + '\'' +
                "policeNo='" + policeNo + '\'' +
                "taskId='" + taskId + '\'' +
                "productId='" + productId + '\'' +
                "id='" + id + '\'' +
                "name='" + name + '\'' +
                '}';
    }
}
