package com.gin.wms.manager.db.contract;

import com.bosnet.ngemart.libgen.Contract;
import com.bosnet.ngemart.libgen.SqlLiteDataType;

public class WarehouseProblemContract extends Contract {

    public static final String TABLE_NAME = "warehouseProblem";

    public WarehouseProblemContract(){
        super();
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String[] getListColumn() {
        return new String[]{
                Column.BINDID,
                Column.PALLETNO,
                Column.PRODUCTID,
                Column.OPERATORID,
                Column.OPERATORNAME,
                Column.TYPE,
                Column.STATUS,
                Column.ACTION,
                Column.INPUTTIME
        };
    }

    @Override
    public String[] getListType() {
        return new String[]{
                Type.BINDID,
                Type.PALLETNO,
                Type.PRODUCTID,
                Type.OPERATORID,
                Type.OPERATORNAME,
                Type.TYPE,
                Type.STATUS,
                Type.ACTION,
                Type.INPUTTIME
        };
    }

    @Override
    public String[] getPrimaryKey() {
        return new String[]{
                Column.BINDID
        };
    }

    public class Column {
        public final static String BINDID = "binId";
        public final static String PALLETNO = "palletNo";
        public final static String PRODUCTID = "productId";
        public final static String OPERATORID = "operatorId";
        public final static String OPERATORNAME = "operatorName";
        public final static String TYPE = "Type";
        public final static String STATUS = "Status";
        public final static String ACTION = "Action";
        public final static String INPUTTIME = "InputTime";
    }

    public class Type {
        public final static String BINDID = SqlLiteDataType.TEXT;
        public final static String PALLETNO = SqlLiteDataType.TEXT;
        public final static String PRODUCTID = SqlLiteDataType.TEXT;
        public final static String OPERATORID = SqlLiteDataType.TEXT;
        public final static String OPERATORNAME = SqlLiteDataType.TEXT;
        public final static String TYPE =  SqlLiteDataType.INTEGER;
        public final static String STATUS =  SqlLiteDataType.INTEGER;
        public final static String ACTION =  SqlLiteDataType.INTEGER;
        public final static String INPUTTIME = SqlLiteDataType.DATE;
    }
}
