package com.gin.wms.manager.db.data;

import com.bosnet.ngemart.libgen.Contract;
import com.gin.wms.manager.db.contract.checker.CheckerTaskOperatorContract;
import com.gin.wms.manager.db.data.base.OperatorBaseData;

/**
 * Created by manbaul on 2/23/2018.
 */

public class CheckerTaskOperatorData extends OperatorBaseData {
    public String taskId;

    public CheckerTaskOperatorData(){
        super();
    }
    public CheckerTaskOperatorData(String taskId, String id, String name) throws Exception {
        super(id, name);
        this.taskId = taskId;
    }

    @Override
    public Contract getContract() throws Exception {
        return new CheckerTaskOperatorContract();
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
