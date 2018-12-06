package com.gin.wms.manager.db.contract;

import com.bosnet.ngemart.libgen.Contract;
import com.bosnet.ngemart.libgen.SqlLiteDataType;

/**
 * Created by bintang on 3/29/2018.
 */

public class VehicleContract extends Contract {
    private static final String TABLE_NAME = "vehicle";

    public VehicleContract() throws Exception {
        super();
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String[] getListColumn() {
        return new String[]{
                Column.POLICE_NUMBER,
                Column.VEHICLE_OWNER_ID,
                Column.TYPE,
                Column.WORKPLACE_ID,
                Column.CATEGORY_1,
                Column.CATEGORY_2,
                Column.UPDATED
        };
    }

    @Override
    public String[] getListType() {
        return new String[]{
                Type.POLICE_NUMBER,
                Type.VEHICLE_OWNER_ID,
                Type.TYPE,
                Type.WORKPLACE_ID,
                Type.CATEGORY_1,
                Type.CATEGORY_2,
                Type.UPDATED
        };
    }

    @Override
    public String[] getPrimaryKey() {
        return new String[]{
                Column.POLICE_NUMBER,
                Column.VEHICLE_OWNER_ID
        };
    }

    public class Column {
        public final static String POLICE_NUMBER = "policeNumber";
        public final static String VEHICLE_OWNER_ID = "vehicleOwnerId";
        public final static String TYPE = "type";
        public final static String WORKPLACE_ID = "workplaceId";
        public final static String CATEGORY_1 = "category1";
        public final static String CATEGORY_2 = "category2";
        public final static String UPDATED = "updated";
    }

    public class Type{
        public final static String POLICE_NUMBER = SqlLiteDataType.TEXT;
        public final static String VEHICLE_OWNER_ID = SqlLiteDataType.TEXT;
        public final static String TYPE = SqlLiteDataType.TEXT;
        public final static String WORKPLACE_ID = SqlLiteDataType.TEXT;
        public final static String CATEGORY_1 = SqlLiteDataType.TEXT;
        public final static String CATEGORY_2 = SqlLiteDataType.TEXT;
        public final static String UPDATED = SqlLiteDataType.DATE;
    }

    public class Query {
        public final static String SELECT = "select * from "
                + TABLE_NAME
                + " order by "
                + UserContract.Column.ID
                + " desc limit 1";
    }
}
