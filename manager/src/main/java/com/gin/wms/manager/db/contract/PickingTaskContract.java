package com.gin.wms.manager.db.contract;

import com.bosnet.ngemart.libgen.Contract;
import com.bosnet.ngemart.libgen.SqlLiteDataType;

import java.sql.SQLData;

/**
 * Created by bintang on 4/26/2018.
 */

public class PickingTaskContract extends Contract{
    private final static String TABLE_NAME = "pikingTask";

    public PickingTaskContract()throws Exception{
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
                Column.OPERATOR_ID,
                Column.PRODUCT_ID,
                Column.CLIENT_ID,
                Column.DOC_URI,
                Column.DOC_REF_URI,
                Column.QTY,
                Column.START_TIME,
                Column.END_TIME,
                Column.STATUS,
                Column.TYPE,
                Column.UPDATED
        };
    }

    @Override
    public String[] getListType() {
        return new String[]{
                Type.PICKING_TASK_ID,
                Type.OPERATOR_ID,
                Type.PRODUCT_ID,
                Type.CLIENT_ID,
                Type.DOC_URI,
                Type.DOC_REF_URI,
                Type.QTY,
                Type.START_TIME,
                Type.END_TIME,
                Type.STATUS,
                Type.TYPE,
                Type.UPDATED
        };
    }

    @Override
    public String[] getPrimaryKey() {
        return new String[]{
                Column.PICKING_TASK_ID
        };
    }

    public class Column {
        public final static String PICKING_TASK_ID = "pickingTaskId";
        public final static String OPERATOR_ID = "operatorId";
        public final static String PRODUCT_ID = "productId";
        public final static String CLIENT_ID = "clientId";
        public final static String DOC_URI = "docUri";
        public final static String DOC_REF_URI = "docRefUri";
        public final static String QTY = "qty";
        public final static String START_TIME = "startTime";
        public final static String END_TIME = "endTime";
        public final static String STATUS = "status";
        public final static String TYPE = "type";
        public final static String UPDATED = "updated";
    }

    public class Type {
        public final static String PICKING_TASK_ID = SqlLiteDataType.TEXT;
        public final static String OPERATOR_ID = SqlLiteDataType.TEXT;
        public final static String PRODUCT_ID = SqlLiteDataType.TEXT;
        public final static String CLIENT_ID = SqlLiteDataType.TEXT;
        public final static String DOC_URI = SqlLiteDataType.TEXT;
        public final static String DOC_REF_URI = SqlLiteDataType.TEXT;
        public final static String QTY = SqlLiteDataType.DOUBLE;
        public final static String START_TIME = SqlLiteDataType.DATE;
        public final static String END_TIME = SqlLiteDataType.DATE;
        public final static String STATUS = SqlLiteDataType.INTEGER;
        public final static String TYPE = SqlLiteDataType.INTEGER;
        public final static String UPDATED = SqlLiteDataType.DATE;
    }
}
