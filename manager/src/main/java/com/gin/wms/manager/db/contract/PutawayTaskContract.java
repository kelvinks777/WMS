package com.gin.wms.manager.db.contract;

import com.bosnet.ngemart.libgen.Contract;
import com.bosnet.ngemart.libgen.SqlLiteDataType;

/**
 * Created by manbaul on 4/9/2018.
 */

public class PutawayTaskContract extends Contract {
    public final static String TABLE_NAME = "putaway_task";

    public PutawayTaskContract() throws Exception {
        super();
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String[] getListColumn() {
        return new String[] {
                Column.ID,
                Column.DESTINATION_ID,
                Column.RECEIVING_NO,
                Column.STATUS,
                Column.TASK_TYPE,
                Column.HAS_BEEN_START,
                Column.UPDATED,
        };
    }

    @Override
    public String[] getListType() {
        return new String[] {
                Type.ID,
                Type.DESTINATION_ID,
                Type.RECEIVING_NO,
                Type.STATUS,
                Type.TASK_TYPE,
                Type.HAS_BEEN_START,
                Type.UPDATED,
        };
    }

    @Override
    public String[] getPrimaryKey() {
        return new String[] {
                Column.ID,
        };
    }
    public class Column {
        public final static String ID = "id";
        public final static String DESTINATION_ID = "destinationId";
        public final static String RECEIVING_NO = "receivingNo";
        public final static String STATUS = "status";
        public final static String TASK_TYPE = "taskType";
        public final static String HAS_BEEN_START = "hasBeenStart";
        public final static String UPDATED = "updated";
    }

    public class Type {
        public final static String ID = SqlLiteDataType.TEXT;
        public final static String DESTINATION_ID = SqlLiteDataType.TEXT;
        public final static String RECEIVING_NO = SqlLiteDataType.TEXT;
        public final static String STATUS = SqlLiteDataType.INTEGER;
        public final static String TASK_TYPE = SqlLiteDataType.INTEGER;
        public final static String HAS_BEEN_START = SqlLiteDataType.INTEGER;
        public final static String UPDATED = SqlLiteDataType.DATE;
    }

    public static class Query {
        public static String getSelectAll() {
            return "select * from " + TABLE_NAME;
        }
    }
}
