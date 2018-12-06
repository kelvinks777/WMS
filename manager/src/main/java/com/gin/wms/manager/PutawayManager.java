package com.gin.wms.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.bosnet.ngemart.libgen.Manager;
import com.gin.wms.manager.config.ManagerSetup;
import com.gin.wms.manager.config.NgRouteUrl;
import com.gin.wms.manager.db.contract.PutawayTaskContract;
import com.gin.wms.manager.db.contract.PutawayTaskItemContract;
import com.gin.wms.manager.db.data.CheckerTaskData;
import com.gin.wms.manager.db.data.PutawayData;
import com.gin.wms.manager.db.data.PutawayTaskData;
import com.gin.wms.manager.db.data.PutawayTaskItemData;
import com.gin.wms.manager.db.data.enums.PutawayTypeEnum;
import com.gin.wms.manager.db.data.enums.TaskStatusEnum;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by manbaul on 3/26/2018.
 */

public class PutawayManager extends Manager {
    private static final String PUTAWAY_TYPE = "com.gin.wms.manager.putaway.type";
    private SharedPreferences sharedPref;
    private CheckerTaskManager checkerTaskManager;

    public PutawayManager(Context context) throws Exception {
        super(context, new NgRouteUrl().getUrlDomainTask() + "putawaytask", new UserContext(context), new ManagerSetup());
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        checkerTaskManager = new CheckerTaskManager(context);
    }

    public PutawayData.PutawayTaskData createPutAwayWithStaging(String operatorId, String SourceBinId, String ProductId, double Qty, String palletNo, String receivingDocumentId, Date expire) throws Exception {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        return restClient.get(PutawayData.PutawayTaskData.class, "CreatePutAwayWithStaging?operatorId=" + operatorId + "&SourceBinId=" + SourceBinId +
                "&ProductId=" + ProductId + "&Qty=" + Qty + "&palletNo=" + palletNo + "&receivingDocumentId=" + receivingDocumentId + "&Expire=" + df.format(expire));
    }

    public void startPutaway(PutawayTaskData putawayTaskData) throws Exception {
        restClient.post("startPutaway", putawayTaskData);
    }

    public PutawayTaskItemData getPutawayTaskItemDataByPalletNo(String refDocId, String palletNo) throws Exception {
        PutawayTaskItemData result = restClient.get(PutawayTaskItemData.class, "getPutawayTaskItem?refDocId=" + refDocId + "&palletNo=" + palletNo);
        DbExecuteWrite(dbClient -> {
            dbClient.DeleteAll(PutawayTaskItemData.class);
            if (result != null) {
                result.qtyCompUomValue = result.getHelper().getCompUomValueFromTotal(result.qty);
                dbClient.Save(result);
            }
        });
        return result;
    }

    public List<PutawayTaskData> findPutawayTasks() throws Exception {
        List<PutawayTaskData> results = restClient.getList(PutawayTaskData[].class, "FindPutawayTask");
        DbExecuteWrite(dbClient -> {
            for (PutawayTaskData putaway :
                    results) {
                dbClient.Save(putaway);
                for (PutawayTaskItemData item :
                        putaway.items) {
                    item.qtyCompUomValue = item.getHelper().getCompUomValueFromTotal(item.qty);
                    dbClient.Save(item);
                }
            }
        });
        return results;
    }

    public PutawayTaskData findPutawayTask() throws Exception {
        PutawayTaskData results = restClient.get(PutawayTaskData.class, "FindPutawayTask");
        DbExecuteWrite(dbClient -> {
            dbClient.Save(results);
            for (PutawayTaskItemData item :
                    results.items) {
                item.qtyCompUomValue = item.getHelper().getCompUomValueFromTotal(item.qty);
                dbClient.Save(item);
            }
        });
        return results;
    }

    public void startMovingToStaging(PutawayTaskData putawayTaskData) throws Exception {
        CheckerTaskData checkerTaskData = checkerTaskManager.getLocalTaskByUnloader();
        putawayTaskData.receivingNo = checkerTaskData.refDocId;
        putawayTaskData.status = TaskStatusEnum.PROGRESS.getValue();
        putawayTaskData.destinationId = checkerTaskData.stagingId;
        restClient.post("startMovingToStaging", putawayTaskData);
        savePutawayTaskData(putawayTaskData);
    }

    public void savePutawayTaskData(PutawayTaskData putawayTaskData) throws Exception {
        DbExecuteWrite(dbClient -> {
            dbClient.Save(putawayTaskData);
            for (PutawayTaskItemData item :
                    putawayTaskData.items) {
                item.qtyCompUomValue = item.getHelper().getCompUomValueFromTotal(item.qty);
                dbClient.Save(item);
            }
        });
    }

    public PutawayTaskData getOnProgressPutawayTaskByRefId(String refId) throws Exception {
        PutawayTaskData result = restClient.get(PutawayTaskData.class, "getOnProgressPutawayTaskByRefId?refId=" + refId);
        DbExecuteWrite(dbClient -> {
            if (result != null) {
                dbClient.DeleteAll(PutawayTaskData.class);
                dbClient.DeleteAll(PutawayTaskItemData.class);
                savePutawayTaskData(result);
            }
        });
        return result;
    }

    public PutawayTaskData getLocalPutawayTask() throws Exception {
        return DbExecuteRead(dbClient -> {
            List<PutawayTaskData> results = dbClient.Query(PutawayTaskData.class, PutawayTaskContract.Query.getSelectAll());
            if (results.size() > 0) {
                results.get(0).items = dbClient.Query(PutawayTaskItemData.class, PutawayTaskItemContract.Query.getSelectAll());
                return results.get(0);
            } else
                return null;
        });
    }

    public void finishPutaway(PutawayTaskData putawayTaskData) throws Exception {
        restClient.post("finishPutaway", putawayTaskData);
        CheckerTaskManager checkerTaskManager = new CheckerTaskManager(context);
        List<CheckerTaskData> checkerTaskDataList = checkerTaskManager.getListOfLocalCheckerTaskData();
        DbExecuteWrite(dbClient -> {
            for (CheckerTaskData checkerTaskData :
                    checkerTaskDataList) {
                checkerTaskManager.removeCheckerTask( checkerTaskData.taskId);
            }
            dbClient.DeleteAll(PutawayTaskData.class);
            dbClient.DeleteAll(PutawayTaskItemData.class);
        });
    }

    public void finishMovingToStaging(PutawayTaskData putawayTaskData) throws Exception {
        restClient.post("FinishMovingToStaging", putawayTaskData);
        DbExecuteWrite(dbClient -> {
            dbClient.DeleteAll(PutawayTaskData.class);
            dbClient.DeleteAll(PutawayTaskItemData.class);
        });
    }

    public void cancelMovingToStagingAndBackToUnloading() throws Exception {
        restClient.post("cancelMovingToStagingAndBackToUnloading");
    }

    public void finishMovingToStagingAndBackToUnloading(PutawayTaskData putawayTaskData) throws Exception {
        restClient.post("FinishMovingToStagingAndBackToUnloading", putawayTaskData);
        CheckerTaskManager checkerTaskManager = new CheckerTaskManager(context);
        List<CheckerTaskData> checkerTaskDataList = checkerTaskManager.getListOfLocalCheckerTaskData();
        DbExecuteWrite(dbClient -> {
            for (CheckerTaskData checkerTaskData :
                    checkerTaskDataList) {
                checkerTaskManager.removeCheckerTask( checkerTaskData.taskId);
            }
            dbClient.DeleteAll(PutawayTaskData.class);
            dbClient.DeleteAll(PutawayTaskItemData.class);
        });
    }

    public void setPutawayType(PutawayTypeEnum typeEnum) throws Exception {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(PUTAWAY_TYPE, typeEnum.getValue());
        editor.apply();
    }

    public PutawayTypeEnum getPutawayType() throws Exception {
        int value = sharedPref.getInt(PUTAWAY_TYPE, 0);
        return PutawayTypeEnum.init(value);
    }
}
