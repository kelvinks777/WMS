package com.gin.wms.manager;

import android.content.Context;

import com.bosnet.ngemart.libgen.DbClient;
import com.bosnet.ngemart.libgen.Manager;
import com.gin.wms.manager.config.ManagerSetup;
import com.gin.wms.manager.config.NgRouteUrl;
import com.gin.wms.manager.db.contract.MovingTaskContract;
import com.gin.wms.manager.db.contract.MovingTaskDestItemContract;
import com.gin.wms.manager.db.contract.MovingTaskSourceItemContract;
import com.gin.wms.manager.db.data.MovingTaskData;
import com.gin.wms.manager.db.data.MovingTaskDestItemData;
import com.gin.wms.manager.db.data.MovingTaskSourceItemData;

import java.util.List;

public class MovingTaskManager extends Manager {

    public MovingTaskManager(Context context) throws Exception {
        super(context, new NgRouteUrl().getUrlDomainTask() + "movingTask", new UserContext(context), new ManagerSetup());
    }

    public MovingTaskData getMovingTaskFromServerByOperatorId()throws Exception{
        String function = "GetMovingTaskByOperatorId";
        MovingTaskData movingTaskData = restClient.get(MovingTaskData.class, function);
        if(movingTaskData != null)
            saveMovingTaskData(movingTaskData);

        return movingTaskData;
    }

    public MovingTaskData getMovingTaskFromServerById(String movingTaskId)throws Exception{
        String function = "GetMovingTask?movingTaskId=" + movingTaskId;
        return restClient.get(MovingTaskData.class, function);
    }

    public MovingTaskData getLastMovingTaskFromLocal()throws Exception{
        return DbExecuteRead(dbClient -> {
            MovingTaskData movingTaskData = null;
            String queryMovingTask = "SELECT * FROM " + new MovingTaskContract().getTableName() + " LIMIT 1";
            List<MovingTaskData> movingTaskDataList = dbClient.Query(MovingTaskData.class, queryMovingTask);
            if(movingTaskDataList.size() > 0){
                movingTaskData = movingTaskDataList.get(0);
                String queryMovingSourceItem = "SELECT * FROM " + new MovingTaskSourceItemContract().getTableName()
                        + " WHERE " + MovingTaskSourceItemContract.Column.MOVING_ID + " = '" + movingTaskData.movingId + "'";
                String queryMovingDestItem = "SELECT * FROM " + new MovingTaskDestItemContract().getTableName()
                        + " WHERE " + MovingTaskDestItemContract.Column.MOVING_ID + " = '" + movingTaskData.movingId + "'";

                List<MovingTaskSourceItemData> sourceItemDataList = dbClient.Query(MovingTaskSourceItemData.class, queryMovingSourceItem);
                List<MovingTaskDestItemData> destItemDataList = dbClient.Query(MovingTaskDestItemData.class, queryMovingDestItem);
                movingTaskData.sourceItemList = sourceItemDataList;
                movingTaskData.destItemList = destItemDataList;
            }

            return movingTaskData;
        });
    }

    public MovingTaskData createMovingTask(String stagingBinId, String dockingNo, String palletNo, String operatorId, String releaseOrderId)throws Exception{
        String functionName = "createMovingTask?stagingBinId=" + stagingBinId
                + "&dockingNo=" + dockingNo
                + "&palletNo=" + palletNo
                + "&operatorId=" + operatorId
                + "&releaseOrderId=" + releaseOrderId;

        MovingTaskData movingTaskData = restClient.get(MovingTaskData.class, functionName);
        saveMovingTaskData(movingTaskData);

        return movingTaskData;
    }

    public void updatePalletNoForMutationOrder(String movingTaskId, String palletNo, String productId) throws Exception{
        String functionName = "UpdatePalletNoForMutationOrder?movingTaskId=" + movingTaskId
                + "&palletNo=" + palletNo
                + "&productId=" + productId;
        restClient.post(MovingTaskData.class, functionName);
    }

    public void saveMovingTaskData(MovingTaskData movingTaskData)throws Exception{
        DbExecuteWrite(dbClient -> {
            dbClient.Delete(MovingTaskContract.TABLE_NAME
                    , new String[]{MovingTaskContract.Column.MOVING_ID}
                    , new String[]{movingTaskData.movingId});

            dbClient.Delete(MovingTaskSourceItemContract.TABLE_NAME
                    , new String[]{MovingTaskSourceItemContract.Column.MOVING_ID}
                    , new String[]{movingTaskData.movingId});

            dbClient.Delete(MovingTaskDestItemContract.TABLE_NAME
                    , new String[]{MovingTaskDestItemContract.Column.MOVING_ID}
                    , new String[]{movingTaskData.movingId});

            for(MovingTaskDestItemData destItemData : movingTaskData.destItemList)
                dbClient.Save(destItemData);

            for(MovingTaskSourceItemData sourceItemData : movingTaskData.sourceItemList)
                dbClient.Save(sourceItemData);

            dbClient.Save(movingTaskData);
        });
    }

    public MovingTaskData startMovingData(MovingTaskData movingTaskData)throws Exception{
        String functionName = "StartMovingTask?movingTaskId=" + movingTaskData.movingId;
        MovingTaskData result = restClient.post(MovingTaskData.class, functionName);
        saveMovingTaskData(result);

        return result;
    }

    public void finishMovingTask(String movingTaskId) throws Exception {
        String functionName = "FinishMovingTask?id=" + movingTaskId;
        restClient.post(functionName);
    }

    public void finishMovingTaskAndBackToLoadingTask(String movingTaskId)throws Exception{
        String functionName = "finishMovingTaskAndBackToLoadingTask?id=" + movingTaskId;
        restClient.post(functionName);
    }

    public void finishMovingTaskAndBackToMovingToDockingTask(String movingTaskId)throws Exception{
        String functionName = "finishMovingTaskAndBackToMovingToDockingTask?id=" + movingTaskId;
        restClient.post(functionName);
    }

    public void deleteLocalMovingTask() throws Exception{
        DbExecuteWrite(dbClient -> dbClient.DeleteAll(MovingTaskData.class));
    }

    public void cancelMovingTask()throws Exception{
        String functionName = "CancelMovingTaskAndBackToLoading";
        restClient.post(functionName);
    }

    public MovingTaskData getReplenishData()throws Exception{
        String functionName = "GetReplenishData";
        MovingTaskData movingTaskData = restClient.get(MovingTaskData.class, functionName);
        saveMovingTaskData(movingTaskData);

        return movingTaskData;
    }

    public MovingTaskData getDestructionData()throws Exception{
        String functionName = "GetDestructionData";
        MovingTaskData movingTaskData = restClient.get(MovingTaskData.class, functionName);
        saveMovingTaskData(movingTaskData);

        return movingTaskData;
    }

    public MovingTaskData getMutationData()throws Exception{
        String functionName = "GetMutationData";
        MovingTaskData movingTaskData = restClient.get(MovingTaskData.class, functionName);
        saveMovingTaskData(movingTaskData);

        return movingTaskData;
    }

    public MovingTaskData getMutationManualData()throws Exception{
        String functionName = "GetMutationManualData";
        MovingTaskData movingTaskData=restClient.get(MovingTaskData.class, functionName);
        saveMovingTaskData(movingTaskData);

        return movingTaskData;
    }

    public  MovingTaskData getBandedData() throws  Exception{
        String functionName = "GetBandedData";
        MovingTaskData movingTaskData = restClient.get(MovingTaskData.class, functionName);
        saveMovingTaskData(movingTaskData);

        return movingTaskData;
    }
}
