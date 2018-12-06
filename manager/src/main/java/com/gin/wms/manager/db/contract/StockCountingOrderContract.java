package com.gin.wms.manager.db.contract;

import com.bosnet.ngemart.libgen.Contract;
import com.bosnet.ngemart.libgen.SqlLiteDataType;

import java.util.Date;

public class StockCountingOrderContract extends Contract {

    public static final String TABLE_NAME = "StockCountingOrder";
    public StockCountingOrderContract() {
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
                Column.CLIENTID,
                Column.DOCDATE,
                Column.LONGINFO,
                Column.ISFINISHED,
                Column.STATUS,
                Column.DOCTYPE,
                Column.SOURCEID,
                Column.DOCREFID,
                Column.DOCREFURI
        };
    }

    @Override
    public String[] getListType() {
        return new String[]{
                Type.ID,
                Type.CLIENTID,
                Type.DOCDATE,
                Type.LONGINFO,
                Type.ISFINISHED,
                Type.STATUS,
                Type.DOCTYPE,
                Type.SOURCEID,
                Type.DOCREFID,
                Type.DOCREFURI
        };
    }

    @Override
    public String[] getPrimaryKey() {
        return new String[]{
                Column.ID
        };
    }

    public class Column {

        public final static String ID = "Id";
        public final static String CLIENTID = "clientId";
        public final static String DOCDATE = "docDate";
        public final static String LONGINFO = "longInfo";
        public final static String ISFINISHED = "isFinished";
        public final static String STATUS = "Status";
        public final static String DOCTYPE = "docType";
        public final static String SOURCEID = "sourceId";
        public final static String DOCREFID = "docRefId";
        public final static String DOCREFURI = "docRefUri";

    }

    public class Type {

        public final static String ID = SqlLiteDataType.TEXT;
        public final static String CLIENTID = SqlLiteDataType.TEXT;
        public final static String DOCDATE = SqlLiteDataType.DATE;
        public final static String LONGINFO = SqlLiteDataType.TEXT;
        public final static String ISFINISHED = SqlLiteDataType.INTEGER;
        public final static String STATUS =  SqlLiteDataType.INTEGER;
        public final static String DOCTYPE =  SqlLiteDataType.INTEGER;
        public final static String SOURCEID = SqlLiteDataType.TEXT;
        public final static String DOCREFID = SqlLiteDataType.TEXT;
        public final static String DOCREFURI = SqlLiteDataType.TEXT;
    }
}
