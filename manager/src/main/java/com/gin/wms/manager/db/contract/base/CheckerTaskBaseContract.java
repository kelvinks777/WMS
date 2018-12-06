package com.gin.wms.manager.db.contract.base;

import com.bosnet.ngemart.libgen.Contract;
import com.bosnet.ngemart.libgen.SqlLiteDataType;

import java.text.MessageFormat;

/**
 * Created by manbaul on 3/23/2018.
 */

public abstract class CheckerTaskBaseContract extends Contract {

    public CheckerTaskBaseContract() throws Exception {
        super();
    }

    @Override
    public String[] getListColumn() {
        return new String[]{
                Column.REF_DOC_ID,
                Column.POLICE_NO,
                Column.REF_DOC_URI,
                Column.STAGING_ID,
                Column.DOCKING_IDS,
                Column.MIN_OPERATOR,
                Column.MAX_OPERATOR,
                Column.HAS_BEEN_START,
                Column.MULTIPLY_OPERATOR,
                Column.UPDATED
        };
    }

    @Override
    public String[] getListType() {
        return new String[]{
                Type.REF_DOC_ID,
                Type.POLICE_NO,
                Type.REF_DOC_URI,
                Type.STAGING_ID,
                Type.DOCKING_IDS,
                Type.MIN_OPERATOR,
                Type.MAX_OPERATOR,
                Type.HAS_BEEN_START,
                Type.MULTIPLY_OPERATOR,
                Type.UPDATED
        };
    }


    public class Column {
        public final static String REF_DOC_ID = "refDocId";
        public final static String POLICE_NO = "policeNo";
        public final static String REF_DOC_URI = "refDocUri";
        public final static String STAGING_ID = "stagingId";
        public final static String DOCKING_IDS = "dockingIds";
        public final static String MIN_OPERATOR = "minOperator";
        public final static String MAX_OPERATOR = "maxOperator";
        public final static String HAS_BEEN_START = "hasBeenStart";
        public final static String MULTIPLY_OPERATOR = "multiplyOperator";
        public final static String UPDATED = "updated";
    }

    public class Type {
        public final static String REF_DOC_ID = SqlLiteDataType.TEXT;
        public final static String POLICE_NO = SqlLiteDataType.TEXT;
        public final static String REF_DOC_URI = SqlLiteDataType.TEXT;
        public final static String STAGING_ID = SqlLiteDataType.TEXT;
        public final static String DOCKING_IDS = SqlLiteDataType.TEXT;
        public final static String MIN_OPERATOR = SqlLiteDataType.INTEGER;
        public final static String MAX_OPERATOR = SqlLiteDataType.INTEGER;
        public final static String HAS_BEEN_START = SqlLiteDataType.INTEGER;
        public final static String MULTIPLY_OPERATOR = SqlLiteDataType.INTEGER;
        public final static String UPDATED = SqlLiteDataType.DATE;
    }
}
