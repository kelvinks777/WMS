package com.gin.wms.manager.db.data;

import com.bosnet.ngemart.libgen.Contract;
import com.bosnet.ngemart.libgen.Data;
import com.gin.wms.manager.db.contract.UomConversionContract;

/**
 * Created by manbaul on 3/12/2018.
 */

public class UomConversionData extends Data {
    public String uomId;
    public String toUomId;
    public int value;

    @Override
    public Contract getContract() throws Exception {
        return new UomConversionContract();
    }
}
