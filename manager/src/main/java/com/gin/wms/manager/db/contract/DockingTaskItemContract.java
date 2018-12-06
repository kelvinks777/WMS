package com.gin.wms.manager.db.contract;

import com.bosnet.ngemart.libgen.Contract;
import com.bosnet.ngemart.libgen.SqlLiteDataType;

/**
 * Created by manbaul on 4/7/2018.
 */

public class DockingTaskItemContract extends Contract {
    public final static String TABLE_NAME = "docking_task_item";

    public DockingTaskItemContract() throws Exception {
        super();
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String[] getListColumn() {
        return new String[]{
                Column.DOC_ID,
                Column.DOCKING_ID,
                Column.UPDATED,
        };
    }

    @Override
    public String[] getListType() {
        return new String[]{
                Type.DOC_ID,
                Type.DOCKING_ID,
                Type.UPDATED,
        };
    }

    @Override
    public String[] getPrimaryKey() {
        return new String[]{
                Column.DOC_ID,
                Column.DOCKING_ID,
        };
    }

    public class Column {
        public final static String DOC_ID = "docId";
        public final static String DOCKING_ID = "dockingId";
        public final static String UPDATED = "updated";
    }

    public class Type {
        public final static String DOC_ID = SqlLiteDataType.TEXT;
        public final static String DOCKING_ID = SqlLiteDataType.TEXT;
        public final static String UPDATED = SqlLiteDataType.DATE;
    }
}
