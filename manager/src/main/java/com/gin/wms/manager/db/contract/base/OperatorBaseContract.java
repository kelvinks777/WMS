package com.gin.wms.manager.db.contract.base;

import com.bosnet.ngemart.libgen.Contract;
import com.bosnet.ngemart.libgen.SqlLiteDataType;

/**
 * Created by manbaul on 4/16/2018.
 */

public abstract class OperatorBaseContract extends Contract {

    public OperatorBaseContract() throws Exception {
        super();
    }

    @Override
    public String[] getListColumn() {
        return new String[]{
                Column.OPERATOR_ID,
                Column.OPERATOR_NAME,
                Column.UPDATED
        };
    }

    @Override
    public String[] getListType() {
        return new String[]{
                Type.OPERATOR_ID,
                Type.OPERATOR_NAME,
                Type.UPDATED
        };
    }

    @Override
    public String[] getPrimaryKey() {
        return new String[]{
                Column.OPERATOR_ID
        };
    }

    public class Column {
        public final static String OPERATOR_ID = "id";
        public final static String OPERATOR_NAME = "name";
        public final static String UPDATED = "updated";
    }

    public class Type {
        public final static String OPERATOR_ID = SqlLiteDataType.TEXT;
        public final static String OPERATOR_NAME = SqlLiteDataType.TEXT;
        public final static String UPDATED = SqlLiteDataType.DATE;
    }
}
