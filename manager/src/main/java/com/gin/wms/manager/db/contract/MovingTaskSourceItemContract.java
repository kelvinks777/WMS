package com.gin.wms.manager.db.contract;

import com.bosnet.ngemart.libgen.Contract;
import com.bosnet.ngemart.libgen.SqlLiteDataType;

public class MovingTaskSourceItemContract extends Contract {
    public static final String TABLE_NAME = "movingTaskSourceItem";

    public MovingTaskSourceItemContract(){
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
                Column.PRODUCT_ID,
                Column.SOURCE_BIN_ID,
                Column.PALLET_NO,
                Column.EXPIRED_DATE,
                Column.QTY,
                Column.UPDATED
        };
    }

    @Override
    public String[] getListType() {
        return new String[]{
                Type.MOVING_ID,
                Type.PRODUCT_ID,
                Type.SOURCE_BIN_ID,
                Type.PALLET_NO,
                Type.EXPIRED_DATE,
                Type.QTY,
                Type.UPDATED
        };
    }

    @Override
    public String[] getPrimaryKey() {
        return new String[]{
                Column.MOVING_ID,
                Column.PRODUCT_ID,
                Column.SOURCE_BIN_ID
        };
    }

    public class Column {
        public final static String MOVING_ID = "movingId";
        public final static String PRODUCT_ID = "productId";
        public final static String SOURCE_BIN_ID = "sourceBinId";
        public final static String PALLET_NO = "palletNo";
        public final static String EXPIRED_DATE = "expiredDate";
        public final static String QTY = "qty";
        public final static String UPDATED = "updated";
    }

    public class Type {
        public final static String MOVING_ID = SqlLiteDataType.TEXT;
        public final static String PRODUCT_ID = SqlLiteDataType.TEXT;
        public final static String SOURCE_BIN_ID = SqlLiteDataType.TEXT;
        public final static String PALLET_NO = SqlLiteDataType.TEXT;
        public final static String EXPIRED_DATE = SqlLiteDataType.DATE;
        public final static String QTY = SqlLiteDataType.TEXT;
        public final static String UPDATED = SqlLiteDataType.DATE;
    }
}
