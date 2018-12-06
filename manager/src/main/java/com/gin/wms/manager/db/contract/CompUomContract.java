package com.gin.wms.manager.db.contract;

import com.bosnet.ngemart.libgen.Contract;
import com.bosnet.ngemart.libgen.SqlLiteDataType;

/**
 * Created by manbaul on 3/14/2018.
 */

public class CompUomContract extends Contract {
    private static final String TABLE_NAME = "compUom";

    public CompUomContract() throws Exception {
        super();
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String[] getListColumn() {
        return new String[]{
                Column.COMP_UOM_ID,
                Column.SEPARATOR,
                Column.UPDATED
        };
    }

    @Override
    public String[] getListType() {
        return new String[] {
                Type.COMP_UOM_ID,
                Type.SEPARATOR,
                Type.UPDATED
        };
    }

    @Override
    public String[] getPrimaryKey() {
        return new String[] {
                Column.COMP_UOM_ID
        };
    }

    public class Column {
        public final static String COMP_UOM_ID = "compUomId";
        public final static String SEPARATOR = "separator";
        public final static String UPDATED = "updated";
    }

    public class Type {
        public final static String COMP_UOM_ID = SqlLiteDataType.TEXT;
        public final static String SEPARATOR = SqlLiteDataType.TEXT;
        public final static String UPDATED = SqlLiteDataType.DATE;
    }
}
