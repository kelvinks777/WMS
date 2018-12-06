package com.gin.wms.manager.db.contract.checker;

import com.bosnet.ngemart.libgen.SqlLiteDataType;
import com.gin.wms.manager.db.contract.base.ProductBaseContract;
import com.gin.wms.manager.db.contract.base.CheckerTaskItemBaseContract;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by manbaul on 3/9/2018.
 */

public class CheckerTaskItemContract extends CheckerTaskItemBaseContract {
    public static final String TABLE_NAME = "checker_task_item";

    public CheckerTaskItemContract() throws Exception {
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
                Column.TASK_ID,
        };

        Collections.addAll(arrayList, list);
        return arrayList.toArray(new String[0]);
    }

    @Override
    public String[] getListType() {
        ArrayList<String> arrayList = new ArrayList<>();
        Collections.addAll(arrayList, super.getListType());

        String[] list = new String[]{
                Type.TASK_ID,
        };

        Collections.addAll(arrayList, list);
        return arrayList.toArray(new String[0]);
    }


    @Override
    public String[] getPrimaryKey() {
        ArrayList<String> arrayList = new ArrayList<>();
        Collections.addAll(arrayList, super.getPrimaryKey());

        String[] list = new String[]{
                Column.TASK_ID,
        };

        Collections.addAll(arrayList, list);
        return arrayList.toArray(new String[0]);
    }

    public class Column {
        public final static String TASK_ID = "taskId";
    }

    public class Type {
        public final static String TASK_ID = SqlLiteDataType.TEXT;
    }

    public static class Query {
        private final static String SELECT = "SELECT * FROM ";
        private final static String DELETE = "DELETE FROM ";

        private final static String PK_EXPRESSION = TABLE_NAME
                + " WHERE "
                + Column.TASK_ID + " = ''{0}'' AND "
                + ProductBaseContract.Column.PRODUCT_ID + " = ''{1}'' AND "
                + ProductBaseContract.Column.CLIENT_LOCATION_ID + " = ''{2}'' ";

        private final static String LIST_EXPRESSION = TABLE_NAME
                + " WHERE "
                + Column.TASK_ID + " = ''{0}'' ";

        public static String getDeleteList(String taskId) {
            return DELETE + MessageFormat.format(LIST_EXPRESSION, taskId);
        }

        public static String getSelectList(String taskId) {
            return SELECT + MessageFormat.format(LIST_EXPRESSION, taskId) + " ORDER BY " + ProductBaseContract.Column.PRODUCT_NAME;
        }
    }
}
