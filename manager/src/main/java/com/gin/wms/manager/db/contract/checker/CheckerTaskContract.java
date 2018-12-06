package com.gin.wms.manager.db.contract.checker;

import com.bosnet.ngemart.libgen.SqlLiteDataType;
import com.gin.wms.manager.db.contract.base.CheckerTaskBaseContract;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;


/**
 * Created by manbaul on 2/19/2018.
 */

public class CheckerTaskContract extends CheckerTaskBaseContract {

    public static final String TABLE_NAME = "checkerTask";

    public CheckerTaskContract() throws Exception {
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
        return new String[]{
                Column.TASK_ID,
        };
    }

    public class Column {
        public final static String TASK_ID = "taskId";
    }

    public class Type {
        public final static String TASK_ID = SqlLiteDataType.TEXT;
    }

    public static class Query {
        private final static String SELECT_ALL = "SELECT ALL * FROM " + TABLE_NAME;
        private final static String DELETE_LIST = "DELETE FROM " + TABLE_NAME +
                " WHERE " +
                "  " + Column.TASK_ID + "=''{0}'' " ;

        public static String getDeleteList(String taskId) {
            return MessageFormat.format(DELETE_LIST, taskId);
        }

        public static String getSelectAll() {
            return SELECT_ALL;
        }
    }

}
