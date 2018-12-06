package com.gin.wms.manager.db.contract;

import com.bosnet.ngemart.libgen.Contract;
import com.bosnet.ngemart.libgen.SqlLiteDataType;

import java.text.MessageFormat;

/**
 * Created by manbaul on 3/14/2018.
 */

public class UomConversionContract extends Contract {
    private static final String TABLE_NAME = "uomConversion";

    public UomConversionContract() throws Exception {
        super();
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String[] getListColumn() {
        return new String[] {
                Column.UOM_ID,
                Column.TO_UOM_ID,
                Column.VALUE,
                Column.UPDATED,
        };
    }

    @Override
    public String[] getListType() {
        return new String[] {
                Type.UOM_ID,
                Type.TO_UOM_ID,
                Type.VALUE,
                Type.UPDATED,
        };
    }

    @Override
    public String[] getPrimaryKey() {
        return new String[] {
                Column.UOM_ID,
                Column.TO_UOM_ID,
        };
    }

    public class Column {
        public final static String UOM_ID = "uomId";
        public final static String TO_UOM_ID = "toUomId";
        public final static String VALUE = "value";
        public final static String UPDATED = "updated";
    }

    public class Type {
        public final static String UOM_ID = SqlLiteDataType.TEXT;
        public final static String TO_UOM_ID = SqlLiteDataType.TEXT;
        public final static String VALUE = SqlLiteDataType.INTEGER;
        public final static String UPDATED = SqlLiteDataType.DATE;
    }

    public static class Query {
        private final static String SELECT = "SELECT * FROM ";

        private final static String LIST_EXPRESSION = TABLE_NAME
                + " WHERE "
                + Column.UOM_ID + " in (''{0}'',''{1}'',''{2}'') AND "
                + Column.TO_UOM_ID + " in (''{0}'',''{1}'',''{2}'') ";

        public static String getSelectList(String uom1, String uom2, String uom3) {
            return SELECT + MessageFormat.format(LIST_EXPRESSION, uom1, uom2, uom3);
        }
    }
}
