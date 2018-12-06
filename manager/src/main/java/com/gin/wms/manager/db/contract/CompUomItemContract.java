package com.gin.wms.manager.db.contract;

import com.bosnet.ngemart.libgen.Contract;
import com.bosnet.ngemart.libgen.SqlLiteDataType;

import java.text.MessageFormat;

/**
 * Created by manbaul on 3/14/2018.
 */

public class CompUomItemContract extends Contract {
    private static final String TABLE_NAME = "compUomItem";

    public CompUomItemContract() throws Exception {
        super();
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String[] getListColumn() {
        return new String[] {
                Column.COMP_UOM_ID,
                Column.ITEM_NUMBER,
                Column.UOM_ID,
                Column.UPDATED
        };
    }

    @Override
    public String[] getListType() {
        return new String[] {
                Type.COMP_UOM_ID,
                Type.ITEM_NUMBER,
                Type.UOM_ID,
                Type.UPDATED
        };
    }

    @Override
    public String[] getPrimaryKey() {
        return new String[] {
                Column.COMP_UOM_ID,
                Column.ITEM_NUMBER,
        };
    }

    public class Column {
        public final static String COMP_UOM_ID = "compUomId";
        public final static String ITEM_NUMBER = "itemNumber";
        public final static String UOM_ID = "uomId";
        public final static String UPDATED = "updated";
    }

    public class Type {
        public final static String COMP_UOM_ID = SqlLiteDataType.TEXT;
        public final static String ITEM_NUMBER = SqlLiteDataType.INTEGER;
        public final static String UOM_ID = SqlLiteDataType.TEXT;
        public final static String UPDATED = SqlLiteDataType.DATE;
    }
     public static class Query {
        private final static String SELECT = "SELECT * FROM ";

        private final static String LIST_EXPRESSION = TABLE_NAME
                + " WHERE "
                + Column.COMP_UOM_ID + " = ''{0}'' ";

        public static String getSelectList(String compUomId) {
            return SELECT + MessageFormat.format(LIST_EXPRESSION, compUomId);
        }
    }
}
