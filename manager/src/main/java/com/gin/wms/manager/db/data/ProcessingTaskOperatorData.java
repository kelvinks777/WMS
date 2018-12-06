package com.gin.wms.manager.db.data;

import com.bosnet.ngemart.libgen.Contract;
import com.gin.wms.manager.db.contract.processing.ProcessingTaskOperatorContract;
import com.gin.wms.manager.db.data.base.OperatorBaseData;

/**
 * Created by Fernandes on 10/05/2018.
 */

public class ProcessingTaskOperatorData extends OperatorBaseData {
    public String taskId;

    public ProcessingTaskOperatorData(){
        super();
    }
    public ProcessingTaskOperatorData(String taskId, String id, String name) throws Exception {
        super(id, name);
        this.taskId = taskId;
    }

    @Override
    public Contract getContract() throws Exception {
        return new ProcessingTaskOperatorContract();
    }

    @Override
    public String toString() {
        return "OperatorData{" +
                "taskId='" + taskId + '\'' +
                "id='" + id + '\'' +
                "name='" + name + '\'' +
                '}';
    }
}
