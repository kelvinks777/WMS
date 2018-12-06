package com.gin.wms.manager.db.data;

import com.bosnet.ngemart.libgen.Contract;
import com.bosnet.ngemart.libgen.Data;
import com.gin.wms.manager.db.contract.CompUomItemContract;

/**
 * Created by manbaul on 3/12/2018.
 */

public class CompUomItemData extends Data {
    public String compUomId;
    public int itemNumber;
    public String uomId;


    @Override
    public Contract getContract() throws Exception {
        return new CompUomItemContract();
    }
}
