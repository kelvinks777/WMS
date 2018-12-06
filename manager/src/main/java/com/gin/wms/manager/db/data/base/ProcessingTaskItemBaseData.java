package com.gin.wms.manager.db.data.base;

import com.gin.wms.manager.db.data.helper.CompUomHelper;

/**
 * Created by Fernandes on 10/05/2018.
 */

public abstract class ProcessingTaskItemBaseData extends ProductBaseData {
    public double palletQty = 0;
    public double qty = 0;
    public double qtyCheckResult = 0;

    public String qtyCompUomValue = "";
    public String qtyCheckResultCompUomValue = "";

    public double getCalcPalletByQty() {
        return Math.ceil(qty / palletConversionValue);
    }

    public void setCheckResultQtyFromCompUomValue(String value) throws Exception {
        qtyCheckResult =  getHelper().getTotalFromCompUomValue(value);
    }

    public String getCompUomValueFromQty() throws Exception {
        return getHelper().getCompUomValueFromTotal(qty);
    }

    public String getCompUomValueFromCheckResultQty() throws Exception {
        return getHelper().getCompUomValueFromTotal(qtyCheckResult);
    }
}
