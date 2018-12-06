package com.gin.wms.manager;

import android.content.Context;

import com.bosnet.ngemart.libgen.Manager;
import com.gin.wms.manager.config.ManagerSetup;
import com.gin.wms.manager.config.NgRouteUrl;
import com.gin.wms.manager.db.data.WarehouseProblemData;

public class WarehouseProblemManager extends Manager {
    public WarehouseProblemManager(Context context) throws Exception {
        super(context, new NgRouteUrl().getUrlDomainTask() + "WarehouseProblem", new UserContext(context), new ManagerSetup());
    }

    public void CreateWarehouseProblemToServer(WarehouseProblemData warehouseProblemData) throws Exception{
        String function = "CreateWarehouseProblemData?warehouseProblemData";
        restClient.post(function, warehouseProblemData);
    }
}
