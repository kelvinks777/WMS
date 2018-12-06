package com.gin.wms.manager;

import android.content.Context;

import com.bosnet.ngemart.libgen.Manager;
import com.bosnet.ngemart.libgen.NgContext;
import com.bosnet.ngemart.libgen.Setup;
import com.gin.wms.manager.config.ManagerSetup;
import com.gin.wms.manager.config.NgRouteUrl;
import com.gin.wms.manager.db.data.ManualMutationData;

public class ManualMutationManager extends Manager {

    public ManualMutationManager(Context context) throws Exception {
        super(context, new NgRouteUrl().getUrlDomainTask() + "ManualMutationOrder", new UserContext(context), new ManagerSetup());
    }

    public void CreateManualMutationOrderToServer(ManualMutationData manualMutationData) throws Exception{
        String function = "CreateManualMutationOrderData?manualMutationData";
        restClient.post(function, manualMutationData);
    }

}
