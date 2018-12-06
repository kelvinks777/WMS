package com.gin.wms.manager.db.contract;

import com.bosnet.ngemart.libgen.SqlLiteDataType;
import com.gin.wms.manager.db.contract.base.OperatorBaseContract;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;

import static com.gin.wms.manager.db.contract.base.OperatorBaseContract.Column.OPERATOR_ID;

/**
 * Created by manbaul on 3/6/2018.
 */

public class HigherPriorityTaskOperatorContract extends OperatorBaseContract {
    public static final String TABLE_NAME = "higher_priority_task_operator";

    public HigherPriorityTaskOperatorContract() throws Exception {
        super();
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String[] getListColumn() {
        ArrayList<String> arrayList = new ArrayList<>();
        Collections.addAll(arrayList, super.getListColumn());

        String[] list = new String[]{
                Column.REF_DOC_URI,
                Column.REF_DOC_ID,
        };

        Collections.addAll(arrayList, list);
        return arrayList.toArray(new String[0]);
    }

    @Override
    public String[] getListType() {
        ArrayList<String> arrayList = new ArrayList<>();
        Collections.addAll(arrayList, super.getListType());

        String[] list = new String[]{
                Type.REF_DOC_URI,
                Type.REF_DOC_ID,
        };

        Collections.addAll(arrayList, list);
        return arrayList.toArray(new String[0]);
    }

    @Override
    public String[] getPrimaryKey() {
        ArrayList<String> arrayList = new ArrayList<>();
        Collections.addAll(arrayList, super.getPrimaryKey());

        String[] list = new String[]{
                Column.REF_DOC_URI,
                Column.REF_DOC_ID,
        };

        Collections.addAll(arrayList, list);
        return arrayList.toArray(new String[0]);
    }

    public class Column {
        public final static String REF_DOC_URI = "refDocUri";
        public final static String REF_DOC_ID = "refDocId";
    }

    public class Type {
        public final static String REF_DOC_URI = SqlLiteDataType.TEXT;
        public final static String REF_DOC_ID = SqlLiteDataType.TEXT;
    }

    public static class Query {
        private final static String SELECT = "SELECT * FROM ";
        private final static String DELETE = "DELETE FROM ";

        private final static String PK_EXPRESSION = TABLE_NAME
                + " WHERE "
                + Column.REF_DOC_URI + " = ''{0}'' AND "
                + Column.REF_DOC_ID + " = ''{1}'' AND "
                + OPERATOR_ID + " = ''{2}'' ";

        private final static String LIST_EXPRESSION = TABLE_NAME
                + " WHERE "
                + Column.REF_DOC_URI + " = ''{0}'' AND "
                + Column.REF_DOC_ID + " = ''{1}'' ";

        public static String getDelete(String refDocUri, String refDocId, String id) {
            return DELETE + MessageFormat.format(PK_EXPRESSION, refDocUri, refDocId, id);
        }

        public static String getDeleteList(String refDocUri, String refDocId) {
            return DELETE + MessageFormat.format(LIST_EXPRESSION, refDocUri, refDocId);
        }

        public static String getSelectList(String refDocUri, String refDocId) {
            return SELECT + MessageFormat.format(LIST_EXPRESSION, refDocUri, refDocId);
        }
    }

}
