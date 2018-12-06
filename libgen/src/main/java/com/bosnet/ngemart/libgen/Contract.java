package com.bosnet.ngemart.libgen;

/**
 * Created by luis on 2/23/2016.
 * Purpose : Parent for all contract, used for schema database
 */
public abstract class Contract {
    public Contract() {

    }

    public abstract String getTableName();
    public abstract String[] getListColumn();
    public abstract String[] getListType();
    public abstract String[] getPrimaryKey();

    void validate() throws Exception {
        if (getTableName() == null)
            throw new Exception("Table Name Null");
        if (getTableName().equals(""))
            throw new Exception("Table Name empty");
        if (getListColumn().length == 0)
            throw new Exception("List column empty");
        if (getListType().length == 0)
            throw new Exception("List type empty");
        if (getPrimaryKey().length == 0)
            throw new Exception("List primaryKey empty");
        if (getListType().length != getListColumn().length)
            throw new Exception("List column length and list type not match");

    }
}
