package com.gin.wms.manager.db.contract.checker;

import com.gin.wms.manager.db.contract.base.CheckerTaskBaseContract;

import java.text.MessageFormat;

/**
 * Created by manbaul on 3/23/2018.
 */

public class HigherPriorityTaskContract extends CheckerTaskBaseContract {

    private static final String TABLE_NAME = "higher_priority_task";

    public HigherPriorityTaskContract() throws Exception {
        super();
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String[] getListColumn() {
        return super.getListColumn();
    }

    @Override
    public String[] getListType() {
        return super.getListType();

    }

    @Override
    public String[] getPrimaryKey() {
        return new String[]{
                Column.REF_DOC_URI,
                Column.REF_DOC_ID,
        };
    }

    public static class Query {
        private final static String SELECT_ALL = "SELECT ALL * FROM " + TABLE_NAME;
        private final static String DELETE_LIST = "DELETE FROM " + TABLE_NAME +
                " WHERE " +
                "  " + Column.REF_DOC_URI  + "=''{0}'' AND "  +
                "  " + Column.REF_DOC_ID  + "=''{1}'' " ;

        public static String getDeleteList(String refDocUri, String refDocId) {
            return MessageFormat.format(DELETE_LIST, refDocUri, refDocId);
        }

        public static String getSelectAll() {
            return SELECT_ALL;
        }

    }


}
