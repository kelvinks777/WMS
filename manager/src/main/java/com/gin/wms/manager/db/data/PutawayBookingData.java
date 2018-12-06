package com.gin.wms.manager.db.data;

import com.bosnet.ngemart.libgen.Contract;
import com.bosnet.ngemart.libgen.Data;
import com.gin.wms.manager.db.contract.PutawayBookingContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by manbaul on 3/20/2018.
 */

public class PutawayBookingData extends Data {
    public String policeNo;
    public String taskId;
    public String productId;
    public int palletQty;
    public String receivingNo;
    public String clientId;

    private List<PutawayBookingOperatorData> operators = new ArrayList<>();

    public List<PutawayBookingOperatorData> getOperators() throws Exception {
        return operators;
    }

    public void setOperators(List<PutawayBookingOperatorData> operators) throws Exception {
        this.operators = operators;
    }

    @Override
    public Contract getContract() throws Exception {
        return new PutawayBookingContract();
    }
}
