package com.gin.wms.manager.db.data;

import com.bosnet.ngemart.libgen.Contract;
import com.gin.wms.manager.db.contract.HigherPriorityTaskOperatorContract;
import com.gin.wms.manager.db.contract.checker.CheckerTaskOperatorContract;
import com.gin.wms.manager.db.data.base.OperatorBaseData;

/**
 * Created by manbaul on 2/23/2018.
 */

public class HigherPriorityTaskOperatorData extends OperatorBaseData {
    public String refDocUri;
    public String refDocId;

    public HigherPriorityTaskOperatorData(String refDocUri, String refDocId, String id, String name) throws Exception {
        super(id, name);
        this.refDocUri = refDocUri;
        this.refDocId = refDocId;
    }

    @Override
    public Contract getContract() throws Exception {
        return new HigherPriorityTaskOperatorContract();
    }

}
