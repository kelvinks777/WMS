package com.gin.wms.manager.db.contract.base;

import com.bosnet.ngemart.libgen.Contract;
import com.bosnet.ngemart.libgen.SqlLiteDataType;

/**
 * Created by manbaul on 3/26/2018.
 */

public abstract class ProductBaseContract extends Contract {
    public ProductBaseContract() throws Exception {
        super();
    }

    @Override
    public String[] getListColumn() {
        return new String[]{
                Column.PRODUCT_ID,
                Column.PRODUCT_NAME,
                Column.UOM_ID,
                Column.UOM_PALLET_ID,
                Column.COMP_UOM_ID,
                Column.PALLET_CONVERSION_VALUE,
                Column.CLIENT_ID,
                Column.CLIENT_LOCATION_ID,
                Column.UPDATED,
        };
    }

    @Override
    public String[] getListType() {
        return new String[]{
                Type.PRODUCT_ID,
                Type.PRODUCT_NAME,
                Type.UOM_ID,
                Type.UOM_PALLET_ID,
                Type.COMP_UOM_ID,
                Type.PALLET_CONVERSION_VALUE,
                Type.CLIENT_ID,
                Type.CLIENT_LOCATION_ID,
                Type.UPDATED
        };
    }

    @Override
    public String[] getPrimaryKey() {
        return new String[]{
                Column.PRODUCT_ID,
                Column.CLIENT_LOCATION_ID,
        };
    }

    public class Column {
        public final static String PRODUCT_ID = "productId";
        public final static String PRODUCT_NAME = "productName";
        public final static String UOM_ID = "uomId";
        public final static String UOM_PALLET_ID = "uomPalletId";
        public final static String COMP_UOM_ID = "compUomId";
        public final static String PALLET_CONVERSION_VALUE = "palletConversionValue";
        public final static String CLIENT_ID = "clientId";
        public final static String CLIENT_LOCATION_ID = "clientLocationId";
        public final static String UPDATED = "updated";
    }

    public class Type {
        public final static String PRODUCT_ID = SqlLiteDataType.TEXT;
        public final static String PRODUCT_NAME = SqlLiteDataType.TEXT;
        public final static String UOM_ID = SqlLiteDataType.TEXT;
        public final static String UOM_PALLET_ID = SqlLiteDataType.TEXT;
        public final static String COMP_UOM_ID = SqlLiteDataType.TEXT;
        public final static String PALLET_CONVERSION_VALUE = SqlLiteDataType.DOUBLE;
        public final static String CLIENT_ID = SqlLiteDataType.TEXT;
        public final static String CLIENT_LOCATION_ID = SqlLiteDataType.TEXT;
        public final static String UPDATED = SqlLiteDataType.DATE;
    }

}
