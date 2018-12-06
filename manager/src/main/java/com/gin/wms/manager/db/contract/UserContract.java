package com.gin.wms.manager.db.contract;

import com.bosnet.ngemart.libgen.Contract;
import com.bosnet.ngemart.libgen.SqlLiteDataType;

/**
 * Created by manbaul on 2/19/2018.
 */

public class UserContract extends Contract {

    private static final String TABLE_NAME = "user";

    public UserContract() throws Exception {
        super();
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String[] getListColumn() {
        return new String[]{
                Column.ID,
                Column.FULLNAME,
                Column.EMAIL,
                Column.PHONENUMBER,
                Column.PROFILE_IMAGE,
                Column.PUBLIC_KEY,
                Column.UPDATED
        };
    }

    @Override
    public String[] getListType() {
        return new String[]{
                Type.ID,
                Type.FULLNAME,
                Type.EMAIL,
                Type.PHONENUMBER,
                Type.PROFILE_IMAGE,
                Type.PUBLIC_KEY,
                Type.UPDATED

        };
    }

    @Override
    public String[] getPrimaryKey() {
        return new String[]{Column.ID};
    }


    public class Column {
        public final static String ID = "id";
        public final static String FULLNAME = "fullName";
        public final static String EMAIL = "email";
        public final static String PHONENUMBER = "phoneNumber";
        public final static String PROFILE_IMAGE = "profileImage";
        public final static String PUBLIC_KEY = "publicKey";
        public final static String UPDATED = "updated";
    }

    public class Type {
        public final static String ID = "TEXT";
        public final static String FULLNAME = "TEXT";
        public final static String EMAIL = "TEXT";
        public final static String PHONENUMBER = "TEXT";
        public final static String PROFILE_IMAGE = "TEXT";
        public final static String PUBLIC_KEY = "TEXT";
        public final static String UPDATED = SqlLiteDataType.DATE;
    }

    public class Query {
        public final static String SELECT = "select * from " + TABLE_NAME + " order by " + Column.ID + " desc limit 1";
    }
}