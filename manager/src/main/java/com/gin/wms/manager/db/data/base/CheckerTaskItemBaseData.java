package com.gin.wms.manager.db.data.base;

import com.gin.wms.manager.db.data.helper.CompUomHelper;

/**
 * Created by manbaul on 4/16/2018.
 */

public abstract class CheckerTaskItemBaseData extends ProductBaseData {
    public String policeNo = "";
    public double palletQty = 0;
    public double qty = 0;
    public double goodQtyCheckResult = 0;
    public double badQtyCheckResult = 0;

    public String qtyCompUomValue = "";
    public String goodQtyCheckResultCompUomValue = "";
    public String badQtyCheckResultCompUomValue = "";

    public double getCalcPalletByQty() {
        return Math.ceil(qty / palletConversionValue);
    }

    public void setGoodCheckResultQtyFromCompUomValue(String value) throws Exception {
        goodQtyCheckResult =  getHelper().getTotalFromCompUomValue(value);
    }

    public void setBadCheckResultQtyFromCompUomValue(String value) throws Exception {
        badQtyCheckResult = getHelper().getTotalFromCompUomValue(value);
    }


    public String getCompUomValueFromQty() throws Exception {
        return getHelper().getCompUomValueFromTotal(qty);
    }

    public String getCompUomValueFromGoodCheckResultQty() throws Exception {
        return getHelper().getCompUomValueFromTotal(goodQtyCheckResult);
    }

    public String getCompUomValueFromBadCheckResultQty() throws Exception {
        return getHelper().getCompUomValueFromTotal(badQtyCheckResult);
    }
}
