package com.gin.wms.manager.db.contract;

import com.bosnet.ngemart.libgen.Contract;
import com.bosnet.ngemart.libgen.SqlLiteDataType;

/**
 * Created by manbaul on 3/20/2018.
 */

public class PutawayBookingContract extends Contract {
    private static final String TABLE_NAME = "putaway_operator_booking";

    public PutawayBookingContract() throws Exception {
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
                Column.RECEIVING_NO,
                Column.CLIENT_ID,
                Column.PALLET_QTY,
                Column.UPDATED
        };
    }

    @Override
    public String[] getListType() {
        return new String[]{
                Type.POLICE_NO,
                Type.DOC_NO,
                Type.PRODUCT_ID,
                Type.RECEIVING_NO,
                Type.CLIENT_ID,
                Type.PALLET_QTY,
                Type.UPDATED
        };
    }

    @Override
    public String[] getPrimaryKey() {
        return new String[]{
                Column.POLICE_NO,
                Column.DOC_NO,
                Column.PRODUCT_ID,
        };
    }

    public class Column {
        public final static String POLICE_NO = "policeNo";
        public final static String DOC_NO = "taskId";
        public final static String PRODUCT_ID = "productId";
        public final static String PALLET_QTY = "palletQty";
        public final static String CLIENT_ID = "clientId";
        public final static String RECEIVING_NO = "receivingNo";
        public final static String UPDATED = "updated";
    }

    public class Type {
        public final static String POLICE_NO = SqlLiteDataType.TEXT;
        public final static String DOC_NO = SqlLiteDataType.TEXT;
        public final static String PRODUCT_ID = SqlLiteDataType.TEXT;
        public final static String PALLET_QTY = SqlLiteDataType.INTEGER;
        public final static String RECEIVING_NO = SqlLiteDataType.TEXT;
        public final static String CLIENT_ID = SqlLiteDataType.TEXT;
        public final static String UPDATED = SqlLiteDataType.DATE;
    }

}
