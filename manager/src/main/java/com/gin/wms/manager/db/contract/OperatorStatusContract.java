package com.gin.wms.manager.db.contract;

import com.bosnet.ngemart.libgen.Contract;

/**
 * Created by manbaul on 3/6/2018.
 */

public class OperatorStatusContract extends Contract {
    private static final String TABLE_NAME = "operator_contract";

    public OperatorStatusContract() throws Exception {
        super();
    }

    @Override
    public String getTableName() {
        return null;
    }

    @Override
    public String[] getListColumn() {
        return new String[0];
    }

    @Override
    public String[] getListType() {
        return new String[0];
    }

    @Override
    public String[] getPrimaryKey() {
        return new String[0];
    }
}
