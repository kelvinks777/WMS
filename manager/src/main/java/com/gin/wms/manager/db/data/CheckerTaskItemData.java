package com.gin.wms.manager.db.data;

import com.bosnet.ngemart.libgen.Contract;
import com.gin.wms.manager.db.contract.checker.CheckerTaskItemContract;
import com.gin.wms.manager.db.data.base.CheckerTaskItemBaseData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by manbaul on 3/9/2018.
 */

public class CheckerTaskItemData extends CheckerTaskItemBaseData {
    public String taskId;

    public List<CheckerTaskItemResultData> results = new ArrayList<>();

    @Override
    public Contract getContract() throws Exception {
        return new CheckerTaskItemContract();
    }
}
