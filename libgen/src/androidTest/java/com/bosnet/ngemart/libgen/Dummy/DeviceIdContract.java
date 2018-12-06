package com.bosnet.ngemart.libgen.Dummy;

import com.bosnet.ngemart.libgen.Contract;
import com.bosnet.ngemart.libgen.SqlLiteDataType;

/**
 * Created by luis on 2/29/2016.
 * Purpose : Contract for data class deviceId
 */
public class DeviceIdContract extends Contract {

    public DeviceIdContract() throws Exception {
        super();
    }

    @Override
    public String getTableName() {
        return "DeviceId";
    }

    @Override
    public String[] getListColumn() {
        return new String[]{
                Column.ID,
                Column.ROWKEY,
                Column.UID,
                Column.UPDATED,
                Column.PRICE,
                Column.QUANTITY,
                Column.AMOUNT,
                Column.IMAGE
        };
    }

    @Override
    public String[] getListType() {
        return new String[]{
                Type.ID,
                Type.ROWKEY,
                Type.UID,
                Type.UPDATED,
                Type.PRICE,
                Type.QUANTITY,
                Type.AMOUNT,
                Type.IMAGE
        };
    }

    @Override
    public String[] getPrimaryKey() {
        return new String[]{Column.ID};
    }

    public class Column {
        public final static String ID = "id";
        public final static String UID = "UID";
        public final static String ROWKEY = "RowKey";
        public final static String UPDATED = "updated";
        public final static String PRICE = "price";
        public final static String QUANTITY = "quantity";
        public final static String AMOUNT = "amount";
        public final static String IMAGE = "image";

    }

    public class Type {
        public final static String ID = SqlLiteDataType.TEXT;
        public final static String UID = SqlLiteDataType.TEXT;
        public final static String ROWKEY = SqlLiteDataType.TEXT;
        public final static String UPDATED = SqlLiteDataType.DATE;
        public final static String PRICE = SqlLiteDataType.FLOAT;
        public final static String QUANTITY = SqlLiteDataType.INTEGER;
        public final static String AMOUNT = SqlLiteDataType.DOUBLE;
        public final static String IMAGE = "BLOB";

    }
}
