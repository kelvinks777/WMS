package com.gin.wms.manager.db.data;

import com.bosnet.ngemart.libgen.Contract;
import com.gin.wms.manager.db.contract.ProductContract;
import com.gin.wms.manager.db.data.base.ProductBaseData;

/**
 * Created by manbaul on 3/26/2018.
 */

public class ProductData extends ProductBaseData {

    public static ProductData getFromBase(ProductBaseData productBaseData) throws Exception {
        ProductData productData = new ProductData();
        productData.setCompUom(productBaseData.getCompUom()) ;
        productData.clientId = productBaseData.clientId;
        productData.clientLocationId = productBaseData.clientLocationId;
        productData.compUomId = productBaseData.compUomId;
        productData.palletConversionValue = productBaseData.palletConversionValue;
        productData.productId = productBaseData.productId;
        productData.productName = productBaseData.productName;
        productData.uomId = productBaseData.uomId;
        productData.uomPalletId = productBaseData.uomPalletId;
        return productData;
    }

    @Override
    public Contract getContract() throws Exception {
        return new ProductContract();
    }
}
