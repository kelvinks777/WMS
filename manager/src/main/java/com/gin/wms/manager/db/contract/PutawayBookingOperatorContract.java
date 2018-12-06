package com.gin.wms.manager.db.contract;

import com.bosnet.ngemart.libgen.Contract;
import com.bosnet.ngemart.libgen.SqlLiteDataType;

import java.text.MessageFormat;

/**
 * Created by manbaul on 3/6/2018.
 */

public class PutawayBookingOperatorContract extends Contract {
    private static final String TABLE_NAME = "putaway_booking_operator";

    public PutawayBookingOperatorContract() throws Exception {
        super();
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String[] getListColumn() {
        return new String[]{
                Column.POLICE_NO,
                Column.DOC_NO,
                Column.PRODUCT_ID,
                Column.OPERATOR_ID,
                Column.OPERATOR_NAME,
                Column.POLICE_NO_BEFORE,
                Column.DOC_NO_BEFORE,
                Column.UPDATED
        };
    }

    @Override
    public String[] getListType() {
        return new String[]{
                Type.POLICE_NO,
                Type.DOC_NO,
                Type.PRODUCT_ID,
                Type.OPERATOR_ID,
                Type.OPERATOR_NAME,
                Type.POLICE_NO_BEFORE,
                Type.DOC_NO_BEFORE,
                Type.UPDATED,
        };
    }

    @Override
    public String[] getPrimaryKey() {
        return new String[]{
                Column.POLICE_NO,
                Column.DOC_NO,
                Column.PRODUCT_ID,
                Column.OPERATOR_ID
        };
    }

    public class Column {
        public final static String POLICE_NO_BEFORE = "policeNoBefore";
        public final static String DOC_NO_BEFORE = "taskIdBefore";
        public final static String POLICE_NO = "policeNo";
        public final static String DOC_NO = "taskId";
        public final static String PRODUCT_ID = "productId";
        public final static String OPERATOR_ID = "id";
        public final static String OPERATOR_NAME = "name";
        public final static String UPDATED = "updated";
    }

    public class Type {
        public final static String POLICE_NO_BEFORE = SqlLiteDataType.TEXT;
        public final static String DOC_NO_BEFORE = SqlLiteDataType.TEXT;
        public final static String POLICE_NO = SqlLiteDataType.TEXT;
        public final static String DOC_NO = SqlLiteDataType.TEXT;
        public final static String OPERATOR_ID = SqlLiteDataType.TEXT;
        public final static String OPERATOR_NAME = SqlLiteDataType.TEXT;
        public final static String PRODUCT_ID = SqlLiteDataType.TEXT;
        public final static String UPDATED = SqlLiteDataType.DATE;
    }

    public static class Query {
        private final static String SELECT_LIST = "SELECT * FROM " + TABLE_NAME +
                " WHERE " +
                Column.POLICE_NO + " = ''{0}'' AND " +
                Column.DOC_NO + " = ''{1}'' AND " +
                Column.PRODUCT_ID + " = ''{2}'' ";

        public static String getSelectList(String policeNo, String docNo, String productId) {
            return MessageFormat.format(SELECT_LIST, policeNo, docNo, productId);
        }
    }
}
