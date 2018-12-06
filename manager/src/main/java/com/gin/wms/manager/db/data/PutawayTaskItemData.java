package com.gin.wms.manager.db.data;

import com.bosnet.ngemart.libgen.Contract;
import com.gin.wms.manager.db.contract.PutawayTaskItemContract;
import com.gin.wms.manager.db.data.base.ProductBaseData;

import org.joda.time.DateTime;

import java.util.Date;

/**
 * Created by manbaul on 4/23/2018.
 */

public class PutawayTaskItemData extends ProductBaseData {
    public String palletNo = "";
    public double qty = 0;
    public String qtyCompUomValue = "";
    public String sourceId = "";
    public Date expiredDate = DateTime.now().toDate();

    @Override
    public Contract getContract() throws Exception {
        return new PutawayTaskItemContract();
    }
}
