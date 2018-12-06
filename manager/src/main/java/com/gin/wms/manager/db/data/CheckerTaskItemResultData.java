package com.gin.wms.manager.db.data;

import com.bosnet.ngemart.libgen.Contract;
import com.bosnet.ngemart.libgen.Data;
import com.gin.wms.manager.db.contract.checker.CheckerTaskItemResultContract;
;import org.joda.time.DateTime;

import java.util.Date;

import static com.bosnet.ngemart.libgen.DateUtil.GetMinDate;

/**
 * Created by manbaul on 4/26/2018.
 */

public class CheckerTaskItemResultData extends Data {
    public String taskId = "";
    public String productId = "";
    public String clientLocationId = "";
    public String palletNo = "";
    public Date expiredDate = new Date(GetMinDate());

    public double goodQty = 0;
    public double badQty = 0;
    public String goodCompUomValue = "";
    public String badCompUomValue = "";

    public boolean alreadyUsed = false;

    @Override
    public Contract getContract() throws Exception {
        return new CheckerTaskItemResultContract();
    }
}
