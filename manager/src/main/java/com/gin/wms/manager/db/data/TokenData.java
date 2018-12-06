package com.gin.wms.manager.db.data;

import com.bosnet.ngemart.libgen.Contract;
import com.bosnet.ngemart.libgen.Data;

/**
 * Created by manbaul on 2/20/2018.
 */

public class TokenData extends Data {
    public String token;

    @Override
    public Contract getContract() throws Exception {
        return null;
    }

    @Override
    public String toString() {
        return "TokenData{" +
                "token='" + token + '\'' +
                '}';
    }
}
