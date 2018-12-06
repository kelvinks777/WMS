package com.gin.wms.manager.config;

import com.bosnet.ngemart.libgen.DbConfig;
import com.bosnet.ngemart.libgen.RestConfig;
import com.bosnet.ngemart.libgen.Setup;

/**
 * Created by manbaul on 2/19/2018.
 */

public class ManagerSetup implements Setup {

    private static RestConfig restConfig = null;
    private static DbConfig dbConfig = null;

    public static void doSetup(RestConfig config) {
        setRestConfig(config);
        setDbConfig(new DefaultDbConfig());
    }

    public static void doSetup(RestConfig config, DbConfig dbConfig) {
        setRestConfig(config);
        setDbConfig(dbConfig);
    }

    public RestConfig getRestConfig() {
        return restConfig;
    }

    private static void setRestConfig(RestConfig config) {
        restConfig = config;
    }

    public DbConfig getDbConfig() {
        return dbConfig;
    }

    private static void setDbConfig(DbConfig config) {
        dbConfig = config;
    }
}