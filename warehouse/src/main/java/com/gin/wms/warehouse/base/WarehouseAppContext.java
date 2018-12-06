package com.gin.wms.warehouse.base;

import com.bosnet.ngemart.libgen.NgLytic;
import com.gin.ngemart.baseui.NgemartAppContext;
import com.gin.ngemart.baseui.component.ErrorListener;
import com.gin.ngemart.baseui.component.NgemartAlert;
import com.gin.wms.manager.config.ManagerSetup;
import com.gin.wms.warehouse.BuildConfig;


/**
 * Created by manbaul on 2/19/2018.
 */

public class WarehouseAppContext extends NgemartAppContext {
    public WarehouseAppContext() throws Exception {
        super();
        if (BuildConfig.DEBUG) {
            ManagerSetup.doSetup(new DebugRestConfig());
        } else {
            ManagerSetup.doSetup(new ReleaseRestConfig());
//            NgemartAlert.setListener(NgLytic::trackException);
        }

    }
}
