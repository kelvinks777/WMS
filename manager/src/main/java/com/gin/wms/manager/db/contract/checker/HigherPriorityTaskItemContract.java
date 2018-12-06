package com.gin.wms.manager.db.contract.checker;

import com.bosnet.ngemart.libgen.SqlLiteDataType;
import com.gin.wms.manager.db.contract.base.ProductBaseContract;
import com.gin.wms.manager.db.contract.base.CheckerTaskItemBaseContract;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by manbaul on 4/16/2018.
 */

public class HigherPriorityTaskItemContract extends CheckerTaskItemBaseContract {
    public static final String TABLE_NAME = "higher_priority_task_item";

    public HigherPriorityTaskItemContract() throws Exception {
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
                + ProductBaseContract.Column.PRODUCT_ID + " = ''{2}'' AND "
                + ProductBaseContract.Column.CLIENT_LOCATION_ID + " = ''{3}'' ";

        private final static String LIST_EXPRESSION = TABLE_NAME
                + " WHERE "
                + Column.REF_DOC_URI + " = ''{0}'' AND "
                + Column.REF_DOC_ID + " = ''{1}'' ";

        public static String getDeleteList(String refDocUri, String refDocId) {
            return DELETE + MessageFormat.format(LIST_EXPRESSION, refDocUri, refDocId);
        }

        public static String getSelectList(String refDocUri, String refDocId) {
            return SELECT + MessageFormat.format(LIST_EXPRESSION, refDocUri, refDocId);
        }
    }
}
