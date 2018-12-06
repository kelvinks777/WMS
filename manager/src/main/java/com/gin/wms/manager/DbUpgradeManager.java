package com.gin.wms.manager;

import android.content.Context;

import com.bosnet.ngemart.libgen.DbClient;
import com.bosnet.ngemart.libgen.Manager;
import com.gin.wms.manager.config.ManagerSetup;
import com.gin.wms.manager.db.data.TokenLocalData;

/**
 * Created by manbaul on 2/19/2018.
 */

public class DbUpgradeManager {
    protected final Context context;
    protected final String appId;
    protected DbClient dbClient;

    public DbUpgradeManager(final Context context, final String appId) throws Exception {
        this.context = context;
        this.appId = appId;
        dbClient = Manager.GetInstanceDbClient(context, new ManagerSetup());
    }

    public boolean isNeedUpgrade() {
        return dbClient.isNeedUpgrade();
    }

    public void doUpgrade() throws Exception {
        TokenLocalManager tokenLocalManager = new TokenLocalManager(context);
        TokenLocalData tokenLocalData = tokenLocalManager.Get();
        dbClient.doUpgrade();
        if (tokenLocalData != null) {
            tokenLocalManager.SaveNgToken(tokenLocalData.ngToken, tokenLocalData.appId);
        }
        UserManager userManager = new UserManager(context);
        userManager.Get();
    }
}