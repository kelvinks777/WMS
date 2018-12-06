package com.gin.wms.manager.db.contract;

import com.bosnet.ngemart.libgen.Contract;
import com.bosnet.ngemart.libgen.SqlLiteDataType;

/**
 * Created by bintang on 4/26/2018.
 */

public class PickingTaskDestItemContract extends Contract {
    private final static String TABLE_NAME = "pikingTaskDestItem";

    public PickingTaskDestItemContract() throws Exception {
        super();
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String[] getListColumn() {
        return new String[]{
                Column.PICKING_TASK_ID,
                Column.DEST_BIN_ID,
                Column.PALLET_NO,
                Column.QTY,
                Column.UPDATED
        };
    }

    @Override
    public String[] getListType() {
        return new String[]{
                Type.PICKING_TASK_ID,
                Type.DEST_BIN_ID,
                Type.PALLET_NO,
                Type.QTY,
                Type.UPDATED
        };
    }

    @Override
    public String[] getPrimaryKey() {
        return new String[]{
                Column.PICKING_TASK_ID,
                Column.DEST_BIN_ID,
                Column.PALLET_NO
        };
    }

    public class Column {
        public final static String PICKING_TASK_ID = "pickingTaskId";
        public final static String DEST_BIN_ID = "destBinId";
        public final static String PALLET_NO = "palletNo";
        public final static String QTY = "qty";
        public final static String UPDATED = "updated";
    }

    public class Type {
        public final static String PICKING_TASK_ID = SqlLiteDataType.TEXT;
        public final static String DEST_BIN_ID = SqlLiteDataType.TEXT;
        public final static String PALLET_NO = SqlLiteDataType.TEXT;
        public final static String QTY = SqlLiteDataType.DOUBLE;
        public final static String UPDATED = SqlLiteDataType.DATE;
    }

    public static class Query {
        public static String getSelectAll() {
            return "select * from " + TABLE_NAME;
        }
    }
}
