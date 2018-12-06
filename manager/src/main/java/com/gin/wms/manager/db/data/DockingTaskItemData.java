package com.gin.wms.manager.db.data;

import com.bosnet.ngemart.libgen.Contract;
import com.bosnet.ngemart.libgen.Data;
import com.gin.wms.manager.db.contract.DockingTaskItemContract;

/**
 * Created by manbaul on 4/7/2018.
 */

public class DockingTaskItemData extends Data {
    public String docId;
    public String dockingId;

    @Override
    public Contract getContract() throws Exception {
        return new DockingTaskItemContract();
    }
}
