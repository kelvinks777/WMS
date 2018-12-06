package com.gin.wms.manager.db.data.base;

import com.bosnet.ngemart.libgen.Data;

/**
 * Created by manbaul on 4/16/2018.
 */

public abstract class OperatorBaseData extends Data {
    public String id;
    public String name;

    public OperatorBaseData(){}

    public OperatorBaseData(String id, String name) throws Exception {
        this.id = id;
        this.name = name;
    }
}
