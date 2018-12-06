package com.gin.wms.manager.db.data.base;

import com.bosnet.ngemart.libgen.Data;
import com.gin.wms.manager.db.data.CompUomData;
import com.gin.wms.manager.db.data.helper.CompUomHelper;

/**
 * Created by manbaul on 3/26/2018.
 */

public abstract class ProductBaseData extends Data {
    public String productId = "";
    public String productName = "";
    public String uomId = "";
    public String uomPalletId = "";
    public double palletConversionValue = 0;
    public String compUomId = "";
    public String clientId = "";
    public String clientLocationId = "";

    private transient CompUomHelper compUomHelper;
    public CompUomHelper getHelper() {
        if (compUomHelper == null)
            compUomHelper = new CompUomHelper(compUom);
        return compUomHelper;
    }

    private CompUomData compUom;
    public CompUomData getCompUom() {
        return compUom;
    }
    public void setCompUom(CompUomData compUom) {
        compUomHelper = new CompUomHelper(compUom);
        this.compUom = compUom;
    }

}
