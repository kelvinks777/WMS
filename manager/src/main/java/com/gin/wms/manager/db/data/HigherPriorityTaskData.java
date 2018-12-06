package com.gin.wms.manager.db.data;

import com.bosnet.ngemart.libgen.Contract;
import com.gin.wms.manager.db.contract.checker.HigherPriorityTaskContract;
import com.gin.wms.manager.db.data.base.CheckerTaskBaseData;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by manbaul on 3/13/2018.
 */

public class HigherPriorityTaskData extends CheckerTaskBaseData implements Serializable {

    public List<HigherPriorityTaskItemData> lstProduct = new ArrayList<>();
    @Override
    public Contract getContract() throws Exception {
        return new HigherPriorityTaskContract();
    }
}
