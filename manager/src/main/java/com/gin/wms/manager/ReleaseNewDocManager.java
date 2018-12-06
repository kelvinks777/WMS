package com.gin.wms.manager;

import android.content.Context;

import com.bosnet.ngemart.libgen.Manager;
import com.gin.wms.manager.config.ManagerSetup;
import com.gin.wms.manager.config.NgRouteUrl;
import com.gin.wms.manager.db.data.ReassignOperatorData;

import java.util.List;

/**
 * Created by manbaul on 3/23/2018.
 */

public class ReleaseNewDocManager extends Manager {
    public ReleaseNewDocManager(Context context) throws Exception {
        super(context, new NgRouteUrl().getUrlDomainTask() + "ReleaseNewDoc", new UserContext(context), new ManagerSetup());
    }

    public void reassignOperators(List<ReassignOperatorData> operators) throws Exception {
        restClient.post("releasenewdoc", operators);
        CheckerTaskManager checkerTaskManager = new CheckerTaskManager(context);
        for (ReassignOperatorData operator:
             operators) {
            checkerTaskManager.removeOperator(operator.docNoBfr , operator.id);
        }
    }
}
