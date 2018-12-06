package com.gin.wms.manager.db.contract;

import com.bosnet.ngemart.libgen.Contract;
import com.bosnet.ngemart.libgen.SqlLiteDataType;

/**
 * Created by manbaul on 2/19/2018.
 */

public class TokenLocalContract extends Contract {

    private static final String TABLE_NAME = "tokenlocal";

    public TokenLocalContract() throws Exception {
        super();
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String[] getListColumn() {
        return new String[]{
                Column.ID,
                Column.NG_TOKEN,
                Column.DEVICE_TOKEN,
                Column.UPDATED,
                Column.APP_ID
        };
    }

    @Override
    public String[] getListType() {
        return new String[]{
                Type.ID,
                Type.NG_TOKEN,
                Type.DEVICE_TOKEN,
                Type.UPDATED,
                Type.APP_ID
        };
    }

    @Override
    public String[] getPrimaryKey() {
        return new String[]{
                Column.ID
        };
    }

    public class Column {
        public final static String ID = "id";
        public final static String NG_TOKEN = "ngToken";
        public final static String DEVICE_TOKEN = "deviceToken";
        public final static String UPDATED = "updated";
        public final static String APP_ID = "appId";
    }

    public class Type {
        public final static String ID = SqlLiteDataType.TEXT;
        public final static String NG_TOKEN = SqlLiteDataType.TEXT;
        public final static String DEVICE_TOKEN = SqlLiteDataType.INTEGER;
        public final static String UPDATED = SqlLiteDataType.DATE;
        public final static String APP_ID = SqlLiteDataType.TEXT;
    }
}