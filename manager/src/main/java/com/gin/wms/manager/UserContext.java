package com.gin.wms.manager;

import android.content.Context;

import com.bosnet.ngemart.libgen.NgContext;

/**
 * Created by manbaul on 2/19/2018.
 */

public class UserContext implements NgContext {
    private static String ngToken = "";
    private static String appId = "";
    private Context context;

    public UserContext(Context context) throws Exception {
        this.context = context;
        GetNgTokenFromDb();
        GetAppIdFromDb();
    }

    private void GetAppIdFromDb() throws Exception {
        if (appId.equals("")) {
            appId = new TokenLocalManager(context).GetAppId();
        }
    }

    private void GetNgTokenFromDb() throws Exception {
        if (ngToken.equals("")) {
            ngToken = new TokenLocalManager(context).GetNgToken();
        }
    }

    public String GetNgToken() throws Exception {
        if (ngToken.equals("")) {
            GetNgTokenFromDb();
        }
        return ngToken;
    }

    public String GeAppIdToken() throws Exception {
        if (appId.equals("")) {
            GetAppIdFromDb();
        }
        return appId;
    }

    public void Clear() {
        ngToken = "";
        appId = "";
    }
}