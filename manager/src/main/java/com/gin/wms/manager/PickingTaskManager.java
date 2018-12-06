package com.gin.wms.manager;

import android.content.Context;
import com.bosnet.ngemart.libgen.Manager;
import com.gin.wms.manager.config.ManagerSetup;
import com.gin.wms.manager.config.NgRouteUrl;
import com.gin.wms.manager.db.contract.PickingTaskContract;
import com.gin.wms.manager.db.contract.PickingTaskDestItemContract;
import com.gin.wms.manager.db.contract.PickingTaskSourceItemContract;
import com.gin.wms.manager.db.data.PickingTaskData;
import com.gin.wms.manager.db.data.PickingTaskDestItemData;
import com.gin.wms.manager.db.data.PickingTaskSourceItemData;

import java.util.List;

/**
 * Created by bintang on 4/26/2018.
 */

public class PickingTaskManager extends Manager {
    public PickingTaskManager(Context context) throws Exception {
        super(context, new NgRouteUrl().getUrlDomainTask() + "pickingtask", new UserContext(context), new ManagerSetup());
    }

    public PickingTaskData findAvailablePickingTask()throws Exception{
        String uriFunction = "findAvailablePickingTask";
        PickingTaskData pickingTaskData = restClient.get(PickingTaskData.class, uriFunction);
        deleteAllPickingTask();
        savePickingTaskData(pickingTaskData);

        return pickingTaskData;
    }

    public void startPickingTask(String pickingTaskId)throws Exception{
        String function = "StartPickingTask?pickingTaskId=" + pickingTaskId;
        restClient.post(function);
    }

    public void finishPickingTask(String pickingTaskId)throws Exception{
        String function = "FinishPickingTask?pickingTaskId=" + pickingTaskId;
        restClient.post(function);
    }

    public void updatePickingSourceQty(PickingTaskSourceItemData sourceItemData)throws Exception{
        restClient.post("UpdatePickingSourceQty", sourceItemData);
    }

    public void updatePickingDestQty(PickingTaskDestItemData destItemData)throws Exception{
        restClient.post("UpdatePickingDestQty", destItemData);
    }

    public void updateDestPalletNumber(String pickingTaskId, String palletNumber)throws Exception{
        String function = "UpdatePalletDestinationNumber?pickingTaskId=" + pickingTaskId + "&palletNoUseByPicker=" + palletNumber;
        restClient.post(function);
    }

    private void savePickingTaskData(PickingTaskData pickingTaskData)throws Exception{
        if(pickingTaskData != null){
            DbExecuteWrite(dbClient -> {
                dbClient.Save(pickingTaskData);
                for(PickingTaskSourceItemData sourceItemData : pickingTaskData.sourceBin)
                    dbClient.Save(sourceItemData);

                for(PickingTaskDestItemData destItemData : pickingTaskData.destBin)
                    dbClient.Save(destItemData);
            });
        }
    }

    public PickingTaskData getPickingTaskFromLocal(String pickingTaskId)throws Exception{
        return DbExecuteRead(dbClient -> {
            PickingTaskData result = dbClient.Find(PickingTaskData.class, pickingTaskId);
            result.sourceBin = justGetPickSourceItemData(pickingTaskId);
            result.destBin = justGetPickDestItemData(pickingTaskId);
            return result;
        });
   }

    private List<PickingTaskSourceItemData> justGetPickSourceItemData(String pickingTaskId)throws Exception{
        String query = "SELECT * FROM " + new PickingTaskSourceItemContract().getTableName() +
                " WHERE " + PickingTaskSourceItemContract.Column.PICKING_TASK_ID  +
                " = '" + pickingTaskId + "'" ;
        return DbExecuteRead(dbClient -> dbClient.Query(PickingTaskSourceItemData.class, PickingTaskSourceItemContract.Query.getSelectAll()));
    }

    private List<PickingTaskDestItemData> justGetPickDestItemData(String pickingTaskId)throws Exception{
        String query = "SELECT * FROM " +
                new PickingTaskDestItemContract().getTableName() +
                " WHERE " +PickingTaskDestItemContract.Column.PICKING_TASK_ID  +
                " = '" + pickingTaskId + "'" ;
        return DbExecuteRead(dbClient -> dbClient.Query(PickingTaskDestItemData.class, PickingTaskDestItemContract.Query.getSelectAll()));
    }
    
    private void deleteAllPickingTask()throws Exception{
        DbExecuteWrite(dbClient -> {
            dbClient.DeleteAll(PickingTaskData.class);
            dbClient.DeleteAll(PickingTaskDestItemData.class);
            dbClient.DeleteAll(PickingTaskSourceItemData.class);
        });
    }
}
