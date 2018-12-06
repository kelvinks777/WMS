package com.gin.wms.manager;

import android.content.Context;

import com.bosnet.ngemart.libgen.Manager;
import com.gin.wms.manager.config.ManagerSetup;
import com.gin.wms.manager.config.NgRouteUrl;
import com.gin.wms.manager.db.contract.DockingTaskContract;
import com.gin.wms.manager.db.data.CheckerTaskOperatorData;
import com.gin.wms.manager.db.data.DockingTaskItemData;
import com.gin.wms.manager.db.data.DockingTaskData;
import com.gin.wms.manager.db.data.enums.RefDocUriEnum;

import java.util.List;

/**
 * Created by manbaul on 3/20/2018.
 */

public class DockingTaskManager extends Manager {
    public DockingTaskManager(Context context) throws Exception {
        super(context, new NgRouteUrl().getUrlDomainTask() + "docking", new UserContext(context), new ManagerSetup());
    }

    public void releaseDockingListWithInterrupt(List<CheckerTaskOperatorData> operators) throws Exception {
        restClient.post("ReleaseDockingListWithInterrupt", operators);
    }

    public DockingTaskData getDockingTask(RefDocUriEnum refDocUriEnum) throws Exception {
        DockingTaskData result = restClient.get(DockingTaskData.class, "GetDockingTask?refDocUri=" + refDocUriEnum.getValue());
        DbExecuteWrite(dbClient -> {
            dbClient.DeleteAll(DockingTaskData.class);
            dbClient.DeleteAll(DockingTaskItemData.class);

            for (DockingTaskItemData docking :
                    result.dockings)
                dbClient.Save(docking);
            dbClient.Save(result.getChecker());
            dbClient.Save(result);
        });
        return result;
    }


    private DockingTaskData getDockingTaskData() throws Exception {
        return DbExecuteRead(dbClient -> {
            List<DockingTaskData> results = dbClient.Query(DockingTaskData.class, DockingTaskContract.Query.SELECT_ALL);
            if (results.size() == 0)
                return null;
            else
                return results.get(0);
        });
    }

    public void startDockingTask() throws Exception {
        DockingTaskData dockingTaskData = getDockingTaskData();
        restClient.post("StartDocking?id=" + dockingTaskData.docId);
    }

    public DockingTaskData getDockingTaskDataByOperator(String refDocUri)throws Exception{
        String functionName = "GetDockingTask?refDocUri=" + refDocUri;
        DockingTaskData dockingTaskData = restClient.get(DockingTaskData.class, functionName);
        SaveDockingTaskData(dockingTaskData);

        return dockingTaskData;
    }

    private void SaveDockingTaskData(DockingTaskData dockingTaskData)throws Exception{
        DbExecuteWrite(dbClient -> {
            dbClient.Save(dockingTaskData);
            for(DockingTaskItemData item : dockingTaskData.dockings)
                dbClient.Save(item);
        });
    }

}
