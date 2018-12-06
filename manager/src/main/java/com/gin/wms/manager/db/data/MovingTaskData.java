package com.gin.wms.manager.db.data;

import com.bosnet.ngemart.libgen.Contract;
import com.bosnet.ngemart.libgen.Data;
import com.gin.wms.manager.db.contract.MovingTaskContract;
import com.gin.wms.manager.db.data.enums.TaskStatusEnum;
import com.gin.wms.manager.db.data.enums.TaskTypeEnum;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class MovingTaskData extends Data implements Serializable{
    public String movingId;
    public String operatorId;
    public String clientId;
    public String clientLocationId;
    public Date startTime;
    public Date endTime;
    public int movingType;
    public int status;
    public String docRefUri;
    public String docRefId;
    public String stagingBinId;
    public String dockingId;
    public String palletNo;
    public List<MovingTaskDestItemData> destItemList;
    public List<MovingTaskSourceItemData> sourceItemList;

    @Override
    public Contract getContract() throws Exception {
        return new MovingTaskContract();
    }

    public TaskStatusEnum getStatus(){
        return TaskStatusEnum.init(status);
    }

    public TaskTypeEnum getTaskType(){
        return TaskTypeEnum.init(movingType);
    }

    @Override
    public String toString() {
        return "MovingTaskData{" +
                "movingId='" + movingId + '\'' +
                ", operatorId='" + operatorId + '\'' +
                ", clientId='" + clientId + '\'' +
                ", clientLocationId='" + clientLocationId + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", movingType=" + movingType +
                ", status=" + status +
                ", docRefUri='" + docRefUri + '\'' +
                ", docRefId='" + docRefId + '\'' +
                ", stagingBinId='" + stagingBinId + '\'' +
                ", dockingId='" + dockingId + '\'' +
                ", palletNo='" + palletNo + '\'' +
                ", destItemList=" + destItemList +
                ", sourceItemList=" + sourceItemList +
                '}';
    }
}
