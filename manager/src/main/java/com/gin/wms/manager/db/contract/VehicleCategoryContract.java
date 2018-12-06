package com.gin.wms.manager.db.contract;

import com.bosnet.ngemart.libgen.Contract;
import com.bosnet.ngemart.libgen.SqlLiteDataType;

/**
 * Created by bintang on 4/2/2018.
 */

public class VehicleCategoryContract extends Contract{
    private static final String TABLE_NAME = "vehicle_category";
    public VehicleCategoryContract() throws Exception {
        super();
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String[] getListColumn() {
        return new String[]{
                Column.TRN_ID,
                Column.VEHICLE_CATEGORY_ID,
                Column.NAME,
                Column.UPDATED
        };
    }

    @Override
    public String[] getListType() {
        return new String[]{
                Type.TRN_ID,
                Type.VEHICLE_CATEGORY_ID,
                Type.NAME,
                Type.UPDATED
        };
    }

    @Override
    public String[] getPrimaryKey() {
        return new String[]{
                Column.TRN_ID,
                Column.VEHICLE_CATEGORY_ID
        };
    }

    public class Column{
        public final static String TRN_ID = "trnId";
        public final static String VEHICLE_CATEGORY_ID = "vehicleCategoryId";
        public final static String NAME = "name";
        public final static String UPDATED = "updated";
    }

    public class Type{
        public final static String TRN_ID = SqlLiteDataType.TEXT;
        public final static String VEHICLE_CATEGORY_ID = SqlLiteDataType.TEXT;
        public final static String NAME = SqlLiteDataType.TEXT;
        public final static String UPDATED = SqlLiteDataType.DATE;
    }
}
