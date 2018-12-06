package com.gin.wms.manager.db.contract.checker;

import com.bosnet.ngemart.libgen.SqlLiteDataType;
import com.gin.wms.manager.db.contract.base.OperatorBaseContract;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;

import static com.gin.wms.manager.db.contract.base.OperatorBaseContract.Column.OPERATOR_ID;

/**
 * Created by manbaul on 3/6/2018.
 */

public class CheckerTaskOperatorContract extends OperatorBaseContract {
    public static final String TABLE_NAME = "checker_task_operator";

    public CheckerTaskOperatorContract() throws Exception {
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
                + OPERATOR_ID + " = ''{1}'' ";

        private final static String LIST_EXPRESSION = TABLE_NAME
                + " WHERE "
                + Column.TASK_ID + " = ''{0}'' ";

        public static String getDelete(String taskId, String id) {
            return DELETE + MessageFormat.format(PK_EXPRESSION, taskId, id);
        }

        public static String getDeleteList(String taskId) {
            return DELETE + MessageFormat.format(LIST_EXPRESSION, taskId);
        }

        public static String getSelectList(String taskId) {
            return SELECT + MessageFormat.format(LIST_EXPRESSION, taskId);
        }
    }

}
