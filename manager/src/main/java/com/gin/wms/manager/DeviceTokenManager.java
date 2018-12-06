package com.gin.wms.manager;

import android.content.Context;

import com.bosnet.ngemart.libgen.Manager;
import com.gin.wms.manager.config.ManagerSetup;
import com.gin.wms.manager.config.NgRouteUrl;

/**
 * Created by manbaul on 2/19/2018.
 */

public class DeviceTokenManager extends Manager {

    public DeviceTokenManager(Context context) throws Exception {
        super(context, new NgRouteUrl().getUrlDomainMart() + "DeviceToken", new UserContext(context), new ManagerSetup());
    }

    public void SendToken(String token) throws Exception {
        if (token == null || token.equals(""))
            throw new NullPointerException("device token is null");

        restClient.post("SendToken" , token);

    }
}
