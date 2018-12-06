package com.gin.wms.manager.db.contract.base;

import com.bosnet.ngemart.libgen.Contract;
import com.bosnet.ngemart.libgen.SqlLiteDataType;

/**
 * Created by Fernandes on 10/05/2018.
 */

public abstract class ProcessingTaskBaseContract extends Contract {

    public ProcessingTaskBaseContract() throws Exception {
        super();
    }

    @Override
    public String[] getListColumn() {
        return new String[]{
                Column.REF_DOC_ID,
                Column.REF_DOC_URI,
                Column.PROCESSING_AREA_ID,
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
                Type.REF_DOC_URI,
                Type.PROCESSING_AREA_ID,
                Type.MIN_OPERATOR,
                Type.MAX_OPERATOR,
                Type.HAS_BEEN_START,
                Type.MULTIPLY_OPERATOR,
                Type.UPDATED
        };
    }


    public class Column {
        public final static String REF_DOC_ID = "refDocId";
        public final static String REF_DOC_URI = "refDocUri";
        public final static String PROCESSING_AREA_ID = "processingAreaId";
        public final static String MIN_OPERATOR = "minOperator";
        public final static String MAX_OPERATOR = "maxOperator";
        public final static String HAS_BEEN_START = "hasBeenStart";
        public final static String MULTIPLY_OPERATOR = "multiplyOperator";
        public final static String UPDATED = "updated";
    }

    public class Type {
        public final static String REF_DOC_ID = SqlLiteDataType.TEXT;
        public final static String REF_DOC_URI = SqlLiteDataType.TEXT;
        public final static String PROCESSING_AREA_ID = SqlLiteDataType.TEXT;
        public final static String MIN_OPERATOR = SqlLiteDataType.INTEGER;
        public final static String MAX_OPERATOR = SqlLiteDataType.INTEGER;
        public final static String HAS_BEEN_START = SqlLiteDataType.INTEGER;
        public final static String MULTIPLY_OPERATOR = SqlLiteDataType.INTEGER;
        public final static String UPDATED = SqlLiteDataType.DATE;
    }
}
