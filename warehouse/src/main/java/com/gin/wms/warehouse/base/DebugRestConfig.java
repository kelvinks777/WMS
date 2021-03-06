package com.gin.wms.warehouse.base;

import com.bosnet.ngemart.libgen.RestConfig;

/**
 * Created by bintang on 3/27/2018.
 */

public class DebugRestConfig implements RestConfig{
    private String apiHost = "http://192.168.2.132:9999";

    @Override
    public String GetApiHost() throws Exception {
        return apiHost;
    }

    @Override
    public String GetAppId() throws Exception {
        return GenConstants.APP_ID;
    }

    @Override
    public String GetPrincipalId() throws Exception {
        return "";
    }

    @Override
    public void setApiHost(String host) throws Exception {
        this.apiHost = host;
    }
}
