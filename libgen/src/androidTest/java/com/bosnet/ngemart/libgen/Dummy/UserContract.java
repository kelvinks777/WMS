package com.bosnet.ngemart.libgen.Dummy;

import com.bosnet.ngemart.libgen.Contract;
import com.bosnet.ngemart.libgen.SqlLiteDataType;

/**
 * Created by luis on 2/23/2016.
 * Purpose : Contract for Class data User
 */
public class UserContract extends Contract {

    public UserContract() throws Exception {
        super();
    }

    @Override
    public String getTableName() {
        return "User";
    }

    @Override
    public String[] getListColumn() {
        return new String[]{
                Column.ID,
                Column.FULLNAME,
                Column.EMAIL,
                Column.PHONENUMBER,
                Column.PROFILE_IMAGE,
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
        public final static String UPDATED = "updated";
    }

    public class Type {
        public final static String ID = "TEXT";
        public final static String FULLNAME = "TEXT";
        public final static String EMAIL = "TEXT";
        public final static String PHONENUMBER = "TEXT";
        public final static String PROFILE_IMAGE = "TEXT";
        public final static String UPDATED = SqlLiteDataType.DATE;
    }
}
