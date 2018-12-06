package com.gin.wms.manager.db.contract;

import com.bosnet.ngemart.libgen.Contract;
import com.bosnet.ngemart.libgen.SqlLiteDataType;

/**
 * Created by manbaul on 4/7/2018.
 */

public class DockingTaskContract extends Contract {
    public static final String TABLE_NAME = "docking_task";
    public DockingTaskContract() throws Exception {
        super();
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String[] getListColumn() {
        return new String[] {
                Column.DOC_ID,
                Column.POLICE_NO,
                Column.STATUS,
                Column.DOC_REF_ID,
                Column.DOC_REF_URI,
                Column.UPDATED,
        };
    }

    @Override
    public String[] getListType() {
        return new String[] {
                Type.DOC_ID,
                Type.POLICE_NO,
                Type.STATUS,
                Type.DOC_REF_ID,
                Type.DOC_REF_URI,
                Type.UPDATED,
        };
    }

    @Override
    public String[] getPrimaryKey() {
        return new String[] {
                Column.DOC_ID,
        };
    }

    public class Column {
        public final static String DOC_ID = "docId";
        public final static String POLICE_NO = "policeNo";
        public final static String STATUS = "status";
        public final static String DOC_REF_ID = "docRefId";
        public final static String DOC_REF_URI = "docRefUri";
        public final static String UPDATED = "updated";
    }

    public class Type {
        public final static String DOC_ID = SqlLiteDataType.TEXT;
        public final static String POLICE_NO = SqlLiteDataType.TEXT;
        public final static String STATUS = SqlLiteDataType.INTEGER;
        public final static String DOC_REF_ID = SqlLiteDataType.TEXT;
        public final static String DOC_REF_URI = SqlLiteDataType.TEXT;
        public final static String UPDATED = SqlLiteDataType.DATE;
    }

    public static class Query {
        public static final String SELECT_ALL = "select * from " + TABLE_NAME;
    }
}
