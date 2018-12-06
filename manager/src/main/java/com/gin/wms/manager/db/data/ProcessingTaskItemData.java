package com.gin.wms.manager.db.data;

import com.bosnet.ngemart.libgen.Contract;
import com.gin.wms.manager.db.contract.processing.ProcessingTaskItemContract;
import com.gin.wms.manager.db.data.base.ProcessingTaskItemBaseData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fernandes on 10/05/2018.
 */

public class ProcessingTaskItemData extends ProcessingTaskItemBaseData {
    public String taskId;

    public List<ProcessingTaskItemResultData> results = new ArrayList<>();

    @Override
    public Contract getContract() throws Exception {
        return new ProcessingTaskItemContract();
    }
}
