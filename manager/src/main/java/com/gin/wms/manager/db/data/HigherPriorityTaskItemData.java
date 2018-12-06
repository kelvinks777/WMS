package com.gin.wms.manager.db.data;

import com.bosnet.ngemart.libgen.Contract;
import com.gin.wms.manager.db.contract.checker.HigherPriorityTaskItemContract;
import com.gin.wms.manager.db.data.base.CheckerTaskItemBaseData;

/**
 * Created by manbaul on 3/9/2018.
 */

public class HigherPriorityTaskItemData extends CheckerTaskItemBaseData {
    public String refDocUri;
    public String refDocId;

    @Override
    public Contract getContract() throws Exception {
        return new HigherPriorityTaskItemContract();
    }
}
