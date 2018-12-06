package com.gin.wms.manager.db.data;

import com.bosnet.ngemart.libgen.Contract;
import com.gin.wms.manager.db.contract.processing.ProcessingTaskContract;
import com.gin.wms.manager.db.data.base.ProcessingTaskBaseData;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fernandes on 10/05/2018.
 */

public class ProcessingTaskData extends ProcessingTaskBaseData implements Serializable {
    public String taskId;
    public List<ProcessingTaskItemData> lstProduct = new ArrayList<>();

    @Override
    public Contract getContract() throws Exception {
        return new ProcessingTaskContract();
    }
}
