package com.gin.wms.manager.db.contract;

import com.bosnet.ngemart.libgen.Contract;
import com.bosnet.ngemart.libgen.SqlLiteDataType;

/**
 * Created by bintang on 4/2/2018.
 */

public class VehicleTypeContract extends Contract{
    private static final String TABLE_NAME = "vehicle_type";
    public VehicleTypeContract() throws Exception {
        super();
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String[] getListColumn() {
        return new String[]{
                Column.VEHICLE_TYPE_ID,
                Column.NAME,
                Column.LENGTH,
                Column.WIDTH,
                Column.HEIGHT,
                Column.UOM_LENGTH,
                Column.UOM_WIDTH,
                Column.UOM_HEIGHT,
                Column.UPDATED
        };
    }

    @Override
    public String[] getListType() {
        return new String[]{
                Type.VEHICLE_TYPE_ID,
                Type.NAME,
                Type.LENGTH,
                Type.WIDTH,
                Type.HEIGHT,
                Type.UOM_LENGTH,
                Type.UOM_WIDTH,
                Type.UOM_HEIGHT,
                Type.UPDATED
        };
    }

    @Override
    public String[] getPrimaryKey() {
        return new String[]{
                Column.VEHICLE_TYPE_ID
        };
    }

    public class Column {
        public final static String VEHICLE_TYPE_ID = "vehicleTypeId";
        public final static String NAME = "name";
        public final static String LENGTH = "length";
        public final static String WIDTH = "width";
        public final static String HEIGHT = "height";
        public final static String UOM_LENGTH = "uomLength";
        public final static String UOM_WIDTH = "uomWidth";
        public final static String UOM_HEIGHT = "uomHeight";
        public final static String UPDATED = "updated";
    }

    public class Type {
        public final static String VEHICLE_TYPE_ID = SqlLiteDataType.TEXT;
        public final static String NAME = SqlLiteDataType.TEXT;
        public final static String LENGTH = SqlLiteDataType.DOUBLE;
        public final static String WIDTH = SqlLiteDataType.DOUBLE;
        public final static String HEIGHT = SqlLiteDataType.DOUBLE;
        public final static String UOM_LENGTH = SqlLiteDataType.TEXT;
        public final static String UOM_WIDTH = SqlLiteDataType.TEXT;
        public final static String UOM_HEIGHT = SqlLiteDataType.TEXT;
        public final static String UPDATED = SqlLiteDataType.DATE;
    }
}
