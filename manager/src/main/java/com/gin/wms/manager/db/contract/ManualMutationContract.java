package com.gin.wms.manager.db.contract;

import com.bosnet.ngemart.libgen.Contract;
import com.bosnet.ngemart.libgen.SqlLiteDataType;

import java.util.Date;

public class ManualMutationContract  extends Contract {

    public static final String TABLE_NAME = "manualMutationOrder";

    public ManualMutationContract() {
        super();
    }

    @Override
    public String getTableName()  {
        return TABLE_NAME;
    }

    @Override
    public String[] getListColumn() {
        return new String[]{
                Column.RECORDERNO,
                Column.TRXTYPEID,
                Column.DOCSTATUS,
                Column.CREATED,
                Column.LASTUPDATED,
                Column.ORDERTYPE,
                Column.OPERATOR,
                Column.PRODUCTID,
                Column.QTY,
                Column.SOURCEPALLETNO,
                Column.SOURCEBINID,
                Column.DESTPALLETNO,
                Column.DESTBINID,
                Column.UOMID,
                Column.CLIENTID,
                Column.CLIENTLOCATIONID,
                Column.EXPIREDDATE,
                Column.REFDOCURI,
                Column.FINISHED
        };
    }

    @Override
    public String[] getListType() {
        return new String[]{
                Type.RECORDERNO,
                Type.TRXTYPEID,
                Type.DOCSTATUS,
                Type.CREATED,
                Type.LASTUPDATED,
                Type.ORDERTYPE,
                Type.OPERATOR,
                Type.PRODUCTID,
                Type.QTY,
                Type.SOURCEPALLETNO,
                Type.SOURCEBINID,
                Type.DESTPALLETNO,
                Type.DESTBINID,
                Type.UOMID,
                Type.CLIENTID,
                Type.CLIENTLOCATIONID,
                Type.EXPIREDDATE,
                Type.REFDOCURI,
                Type.FINISHED
        };
    }

    @Override
    public String[] getPrimaryKey() {
        return new String[] {
                Column.RECORDERNO
        };
    }

    public class Column {
        public final static String RECORDERNO = "recOrderNo";
        public final static String TRXTYPEID = "trxTypeId";
        public final static String DOCSTATUS = "docStatus";
        public final static String CREATED = "created";
        public final static String LASTUPDATED = "lastUpdated";
        public final static String ORDERTYPE = "orderType";
        public final static String OPERATOR = "operator";
        public final static String PRODUCTID = "productId";
        public final static String QTY = "qty";
        public final static String SOURCEPALLETNO = "sourcePalletNo";
        public final static String SOURCEBINID = "sourceBinId";
        public final static String DESTPALLETNO = "destPalletNo";
        public final static String DESTBINID = "destBinId";
        public final static String UOMID = "uOMId";
        public final static String CLIENTID = "clientId";
        public final static String CLIENTLOCATIONID = "clientLocationId";
        public final static String EXPIREDDATE = "expiredDate";
        public final static String REFDOCURI = "refDocUri";
        public final static String FINISHED = "finished";
    }

    public class Type {
        public final static String RECORDERNO = SqlLiteDataType.TEXT;
        public final static String TRXTYPEID = SqlLiteDataType.TEXT;
        public final static String DOCSTATUS = SqlLiteDataType.INTEGER;
        public final static String CREATED = SqlLiteDataType.DATE;
        public final static String LASTUPDATED = SqlLiteDataType.DATE;
        public final static String ORDERTYPE = SqlLiteDataType.INTEGER;
        public final static String OPERATOR = SqlLiteDataType.INTEGER;
        public final static String PRODUCTID = SqlLiteDataType.TEXT;
        public final static String QTY = SqlLiteDataType.DOUBLE;
        public final static String SOURCEPALLETNO = SqlLiteDataType.TEXT;
        public final static String SOURCEBINID = SqlLiteDataType.TEXT;
        public final static String DESTPALLETNO = SqlLiteDataType.TEXT;
        public final static String DESTBINID = SqlLiteDataType.TEXT;
        public final static String UOMID = SqlLiteDataType.TEXT;
        public final static String CLIENTID = SqlLiteDataType.TEXT;
        public final static String CLIENTLOCATIONID = SqlLiteDataType.TEXT;
        public final static String EXPIREDDATE = SqlLiteDataType.DATE;
        public final static String REFDOCURI = SqlLiteDataType.TEXT;
        public final static String FINISHED =  SqlLiteDataType.INTEGER;
    }
}
