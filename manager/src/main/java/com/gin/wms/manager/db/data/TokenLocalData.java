package com.gin.wms.manager.db.data;

import com.bosnet.ngemart.libgen.Contract;
import com.bosnet.ngemart.libgen.Data;
import com.gin.wms.manager.db.contract.TokenLocalContract;

/**
 * Created by manbaul on 2/19/2018.
 */

public class TokenLocalData extends Data {

    public String id = "";
    public String ngToken = "";
    public String deviceToken = "";
    public String appId = "";


    @Override
    public Contract getContract() throws Exception {
        return new TokenLocalContract();
    }

    @Override
    public String toString() {
        return "TokenLocalData{" +
                "id='" + id + '\'' +
                ", ngToken='" + ngToken + '\'' +
                ", deviceToken='" + deviceToken + '\'' +
                ", appId='" + appId + '\'' +
                '}';
    }
}