package com.gin.wms.manager;

import android.content.Context;

import com.bosnet.ngemart.libgen.Manager;
import com.gin.wms.manager.config.ManagerSetup;
import com.gin.wms.manager.config.NgRouteUrl;
import com.gin.wms.manager.db.data.BinData;

import java.util.List;

/**
 * Created by manbaul on 3/26/2018.
 */

public class BinManager extends Manager {
    public BinManager(Context context) throws Exception {
        super(context, new NgRouteUrl().getUrlDomainMs() + "bin", new UserContext(context), new ManagerSetup());
    }

    public List<BinData> getAllBin() throws Exception {
        return restClient.getList(BinData[].class, "GetAllBin");
    }

}
