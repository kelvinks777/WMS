package com.gin.wms.manager.db.contract;

import com.bosnet.ngemart.libgen.Contract;
import com.bosnet.ngemart.libgen.SqlLiteDataType;

import java.text.MessageFormat;

public class StockCountingOrderItemContract extends Contract {

    public static final String TABLE_NAME = "StockCountingOrderItem";

    public StockCountingOrderItemContract()  throws Exception {
        super();
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String[] getListColumn() {
        return new String[]{
                Column.BINID,
                Column.OPERATORID
        };
    }

    @Override
    public String[] getListType() {
        return new String[]{
                Type.BINID,
                Type.OPERATORID
        };
    }

    @Override
    public String[] getPrimaryKey() {
        return new String[]{
                Column.BINID
        };
    }

    public class Column {
        public final static String BINID = "binId";
        public final static String OPERATORID = "operatorId";
    }

    public class Type {
        public final static String BINID = SqlLiteDataType.TEXT;
        public final static String OPERATORID = SqlLiteDataType.TEXT;
    }
    public static class Query {
        public static final String SELECT_ALL="select * from "+ TABLE_NAME;
    }



}