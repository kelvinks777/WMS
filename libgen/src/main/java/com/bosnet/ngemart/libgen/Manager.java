package com.bosnet.ngemart.libgen;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by luis on 9/19/2016.
 * Purpose :
 */
public abstract class Manager {
    private static final long sleepTime = 5;
    private static final Object lockObject = new Object();
    private static DbClient dbClient = null;
    protected RestClient restClient;
    protected Context context;
    private NgContext userContext;
    protected Setup managerSetup;

    public Manager(Context context, NgContext userContext, Setup managerSetup) throws Exception {
        this.context = context;
        restClient = new RestClient(managerSetup.getRestConfig());
        this.userContext = userContext;
        this.managerSetup = managerSetup;
    }

    public Manager(Context context, String baseUrl, NgContext userContext, Setup managerSetup) throws Exception {
        this.context = context;
        this.userContext = userContext;
        this.managerSetup = managerSetup;
        restClient = new RestClient(managerSetup.getRestConfig());
        restClient.setAuthorization(this.userContext.GetNgToken(), baseUrl);

    }

    public static DbClient GetInstanceDbClient(Context context, Setup managerSetup) {
        return new DbClient(context, managerSetup.getDbConfig());
    }

    protected DbClient GetInstanceDbClient() throws Exception {
        return GetInstanceDbClient(context, managerSetup);
    }

    protected void validateData(Data data) throws IllegalAccessException {
        DbClient.ValidateData(data);
    }

    protected DbClient GetDbClient() throws Exception {

        if (DbClient.IsEqualThreadId(dbClient)) {
            if (DbClient.IsRelease(dbClient)) {
                return WaitForNewTx();
            }

            dbClient.OpenConnection();
            return dbClient;
        }

        return WaitForNewTx();
    }

    private DbClient WaitForNewTx() throws Exception {
        while (true) {
            Thread.sleep(sleepTime);

            synchronized (lockObject) {
                if (dbClient == null) {
                    dbClient = GetInstanceDbClient(context, managerSetup);
                    dbClient.OpenConnection();
                    return dbClient;
                } else {
                    if (DbClient.IsRelease(dbClient)) {
                        dbClient = GetInstanceDbClient(context, managerSetup);
                        dbClient.OpenConnection();
                        return dbClient;
                    }

                }
            }
        }
    }

    protected <T> T DbExecuteRead(IDbExecuteRead<T> dbExecute) throws Exception {
        DbClient dbClient = GetInstanceDbClient(context, managerSetup);
        return dbExecute.DoAction(dbClient);
    }

    protected void DbExecuteWrite(IDbExecuteWrite dbExecute) throws Exception {
        DbClient dbClient = GetDbClient();
        try {
            dbClient.BeginTransaction();

            dbExecute.DoAction(dbClient);

            dbClient.CommitTransaction();

        } finally {
            dbClient.ReleaseConnection();
        }

    }

    public interface IDbExecuteRead<T> {
        T DoAction(DbClient dbClient) throws Exception;
    }

    public interface IDbExecuteWrite {
        void DoAction(DbClient dbClient) throws Exception;
    }
}
