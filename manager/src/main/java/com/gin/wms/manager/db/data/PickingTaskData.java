package com.gin.wms.manager.db.data;

import com.bosnet.ngemart.libgen.Contract;
import com.bosnet.ngemart.libgen.Data;
import com.gin.wms.manager.db.contract.PickingTaskContract;
import com.gin.wms.manager.db.data.enums.TaskStatusEnum;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by bintang on 4/26/2018.
 */

public class PickingTaskData extends Data {
    public String pickingTaskId = "";
    public String operatorId = "";
    public String productId = "";
    public String clientId = "";
    public String docUri = "";
    public String docRefUri = "";
    public double qty = 0;
    public Date startTime = DateTime.now().toDate();
    public Date endTime = DateTime.now().toDate();
    public int status = 0;
    public int type = 0;
    public List<PickingTaskSourceItemData> sourceBin = new ArrayList<>();
    public List<PickingTaskDestItemData> destBin = new ArrayList<>();

    @Override
    public Contract getContract() throws Exception {
        return new PickingTaskContract();
    }

    public TaskStatusEnum getStatus(){
        return TaskStatusEnum.init(status);
    }

    @Override
    public String toString() {
        return "PickingTaskData{" +
                "pickingTaskId='" + pickingTaskId + '\'' +
                ", operatorId='" + operatorId + '\'' +
                ", productId='" + productId + '\'' +
                ", clientId='" + clientId + '\'' +
                ", docUri='" + docUri + '\'' +
                ", docRefUri='" + docRefUri + '\'' +
                ", qty=" + qty +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", status=" + status +
                ", type=" + type +
                ", sourceBin=" + sourceBin +
                ", destBin=" + destBin +
                '}';
    }
}