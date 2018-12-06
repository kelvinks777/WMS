package com.bosnet.ngemart.libgen;

import java.util.List;

/**
 * Created by luis on 7/4/2017.
 * Purpose : Setting config untuk dbClient
 */

public interface DbConfig {
    List<Contract> getListContract() throws Exception;

    String getDbName();

    int getDbVersion();

}
