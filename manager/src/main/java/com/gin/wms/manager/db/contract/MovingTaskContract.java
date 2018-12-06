package com.gin.wms.manager.db.contract;

import com.bosnet.ngemart.libgen.Contract;
import com.bosnet.ngemart.libgen.SqlLiteDataType;

public class MovingTaskContract extends Contract {
    public static final String TABLE_NAME = "movingTask";

    public MovingTaskContract(){
        super();
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String[] getListColumn() {
        return new String[]{
                Column.MOVING_ID,
                Column.OPERATOR_ID,
                Column.CLIENT_ID,
                Column.CLIENT_LOCATION_ID,
                Column.START_TIME,
                Column.END_TIME,
                Column.MOVING_TYPE,
                Column.STATUS,
                Column.DOC_REF_URI,
                Column.DOC_REF_ID,
                Column.STAGING_BIN_ID,
                Column.DOCKING_ID,
                Column.PALLET_NO,
                Column.UPDATED
        };
    }

    @Override
    public String[] getListType() {
        return new String[]{
                Type.MOVING_ID,
                Type.OPERATOR_ID,
                Type.CLIENT_ID,
                Type.CLIENT_LOCATION_ID,
                Type.START_TIME,
                Type.END_TIME,
                Type.MOVING_TYPE,
                Type.STATUS,
                Type.DOC_REF_URI,
                Type.DOC_REF_ID,
                Type.STAGING_BIN_ID,
                Type.DOCKING_ID,
                Type.PALLET_NO,
                Type.UPDATED
        };
    }

    @Override
    public String[] getPrimaryKey() {
        return new String[]{
                Column.MOVING_ID
        };
    }

    public class Column {
        public final static String MOVING_ID = "movingId";
        public final static String OPERATOR_ID = "operatorId";
        public final static String CLIENT_ID = "clientId";
        public final static String CLIENT_LOCATION_ID = "clientLocationId";
        public final static String START_TIME = "startTime";
        public final static String END_TIME = "endTime";
        public final static String MOVING_TYPE = "movingType";
        public final static String STATUS = "status";
        public final static String DOC_REF_URI = "docRefUri";
        public final static String DOC_REF_ID = "docRefId";
        public final static String STAGING_BIN_ID = "stagingBinId";
        public final static String DOCKING_ID = "dockingId";
        public final static String PALLET_NO = "palletNo";
        public final static String UPDATED = "updated";
    }

    public class Type {
        public final static String MOVING_ID = SqlLiteDataType.TEXT;
        public final static String OPERATOR_ID = SqlLiteDataType.TEXT;
        public final static String CLIENT_ID = SqlLiteDataType.TEXT;
        public final static String CLIENT_LOCATION_ID = SqlLiteDataType.TEXT;
        public final static String START_TIME = SqlLiteDataType.DATE;
        public final static String END_TIME = SqlLiteDataType.DATE;
        public final static String MOVING_TYPE = SqlLiteDataType.INTEGER;
        public final static String STATUS = SqlLiteDataType.INTEGER;
        public final static String DOC_REF_URI = SqlLiteDataType.TEXT;
        public final static String DOC_REF_ID = SqlLiteDataType.TEXT;
        public final static String STAGING_BIN_ID = SqlLiteDataType.TEXT;
        public final static String DOCKING_ID = SqlLiteDataType.TEXT;
        public final static String PALLET_NO = SqlLiteDataType.TEXT;
        public final static String UPDATED = SqlLiteDataType.DATE;
    }
}
