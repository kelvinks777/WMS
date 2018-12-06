package com.gin.wms.manager;

import android.content.Context;

import com.bosnet.ngemart.libgen.DbClient;
import com.bosnet.ngemart.libgen.Manager;
import com.gin.wms.manager.config.ManagerSetup;
import com.gin.wms.manager.db.data.TokenLocalData;

import java.util.Date;

/**
 * Created by manbaul on 2/19/2018.
 */

public class TokenLocalManager extends Manager {
    final String id = "001";
    public TokenLocalManager(Context context) throws Exception {
        super(context, null, new ManagerSetup());
    }

    public String GetNgToken() throws Exception {
        TokenLocalData tokenLocalData = DbExecuteRead(new IDbExecuteRead<TokenLocalData>() {
            @Override
            public TokenLocalData DoAction(DbClient dbClient) throws Exception {
                return dbClient.Find(TokenLocalData.class, id);
            }
        });

        if (tokenLocalData != null) {
            return tokenLocalData.ngToken;
        }

        return "";
    }

    String GetAppId() throws Exception {

        TokenLocalData tokenLocalData = DbExecuteRead(new IDbExecuteRead<TokenLocalData>() {
            @Override
            public TokenLocalData DoAction(DbClient dbClient) throws Exception {
                return dbClient.Find(TokenLocalData.class, id);
            }
        });

        if (tokenLocalData != null) {
            return tokenLocalData.appId;
        }

        return "";
    }

    public TokenLocalData Get() throws Exception {
        DbClient dbClient = GetDbClient();
        TokenLocalData tokenLocalData = new TokenLocalData();
        try {
            tokenLocalData = dbClient.Find(TokenLocalData.class, id);
        } finally {
            dbClient.ReleaseConnection();
        }
        return tokenLocalData;
    }

    public void SaveNgToken(String ngToken, String appId) throws Exception {
        DbClient dbClient = GetDbClient();

        try {
            TokenLocalData tokenLocalData = Get();
            if (tokenLocalData == null) {
                tokenLocalData = new TokenLocalData();
                tokenLocalData.id = id;
            }
            tokenLocalData.ngToken = ngToken;
            tokenLocalData.appId = appId;
            tokenLocalData.updated = new Date();

            dbClient.BeginTransaction();
            dbClient.Save(tokenLocalData);
            dbClient.CommitTransaction();
        } finally {
            dbClient.ReleaseConnection();
        }
    }

}