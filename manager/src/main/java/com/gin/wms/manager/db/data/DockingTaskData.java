package com.gin.wms.manager.db.data;

import com.bosnet.ngemart.libgen.Contract;
import com.bosnet.ngemart.libgen.Data;
import com.gin.wms.manager.db.contract.DockingTaskContract;
import com.gin.wms.manager.db.data.enums.RefDocUriEnum;
import com.gin.wms.manager.db.data.enums.TaskStatusEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by manbaul on 4/7/2018.
 */

public class DockingTaskData extends Data {
    public String docId;
    public String policeNo;
    public int status;
    public String docRefUri;
    public String docRefId;

    public TaskStatusEnum getStatus() {
        return TaskStatusEnum.init(status);
    }

    private CheckerTaskOperatorData checker = new CheckerTaskOperatorData();

    public void setChecker(CheckerTaskOperatorData checker) {
        this.checker = checker;
    }

    public CheckerTaskOperatorData getChecker() {
        return checker;
    }

    public List<DockingTaskItemData> dockings = new ArrayList<>();

    @Override
    public Contract getContract() throws Exception {
        return new DockingTaskContract();
    }
}
