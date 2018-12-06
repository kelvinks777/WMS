package com.gin.wms.manager;

import android.content.Context;

import com.bosnet.ngemart.libgen.Manager;
import com.gin.wms.manager.config.ManagerSetup;
import com.gin.wms.manager.config.NgRouteUrl;

/**
 * Created by manbaul on 3/26/2018.
 */

public class PutawayTaskManager extends Manager {
    public PutawayTaskManager(Context context) throws Exception {
        super(context, new NgRouteUrl().getUrlDomainTask() + "putawaytask", new UserContext(context), new ManagerSetup());
    }

    public void finishPutawayTask(String receivingDocumentId, String destBinId) throws Exception {
        restClient.post("finishPutawayTask?receivingDocumentId=" + receivingDocumentId + "&destBinId=" + destBinId);
    }
}
