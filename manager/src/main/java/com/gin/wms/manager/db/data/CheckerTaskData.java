package com.gin.wms.manager.db.data;

import com.bosnet.ngemart.libgen.Contract;
import com.gin.wms.manager.db.contract.checker.CheckerTaskContract;
import com.gin.wms.manager.db.data.base.CheckerTaskBaseData;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by manbaul on 2/19/2018.
 */

public class CheckerTaskData extends CheckerTaskBaseData implements Serializable {
    public String taskId;
    public List<CheckerTaskItemData> lstProduct = new ArrayList<>();

    @Override
    public Contract getContract() throws Exception {
        return new CheckerTaskContract();
    }
}
