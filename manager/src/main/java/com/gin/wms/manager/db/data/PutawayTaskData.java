package com.gin.wms.manager.db.data;


import com.bosnet.ngemart.libgen.Contract;
import com.bosnet.ngemart.libgen.Data;
import com.gin.wms.manager.db.contract.PutawayTaskContract;
import com.gin.wms.manager.db.data.enums.PutawayTypeEnum;
import com.gin.wms.manager.db.data.enums.TaskStatusEnum;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by manbaul on 4/9/2018.
 */

public class PutawayTaskData extends Data {
    public String id = "";
    public String destinationId = "";
    public String receivingNo = "";
    public int status = 0;
    public int taskType = 0;
    public boolean hasBeenStart;
    public TaskStatusEnum getStatus() {
        return TaskStatusEnum.init(status);
    }
    public PutawayTypeEnum getTaskType() {
        return PutawayTypeEnum.init(taskType);
    }

    public List<PutawayTaskItemData> items = new ArrayList<>();

    @Override
    public Contract getContract() throws Exception {
        return new PutawayTaskContract();
    }
}
