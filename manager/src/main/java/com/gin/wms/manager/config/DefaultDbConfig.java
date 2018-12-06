package com.gin.wms.manager.config;

import com.bosnet.ngemart.libgen.Contract;
import com.bosnet.ngemart.libgen.DbConfig;

import java.util.List;

/**
 * Created by manbaul on 2/19/2018.
 */

public class DefaultDbConfig implements DbConfig {
    @Override
    public List<Contract> getListContract() throws Exception {
        return ContractManager.getListContract();
    }

    @Override
    public String getDbName() {
        return "wms";
    }

    @Override
    public int getDbVersion() {
        return 1;
    }
}
