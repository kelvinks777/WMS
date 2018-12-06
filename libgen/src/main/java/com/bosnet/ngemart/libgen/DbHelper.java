package com.bosnet.ngemart.libgen;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by luis on 2/23/2016.
 * Purpose : direct access to Sqlite db, don't add specific Query here..
 */
class DbHelper extends SQLiteOpenHelper {

    private boolean isNeedUpgrade = false;
    private List<Contract> listContract;
    private Context context;

    DbHelper(Context context, DbConfig dbConfig) throws Exception {
        super(context, dbConfig.getDbName(), null, dbConfig.getDbVersion());
        this.context = context;
        this.listContract = dbConfig.getListContract();
    }

    //todo: change Object.equals() : http://stackoverflow.com/questions/21548989/using-objects-equals-in-android
    public static boolean equals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }

    boolean isNeedUpgrade() {
        SQLiteDatabase db = getReadableDatabase();
        if (db != null)
            db.close();

        return isNeedUpgrade;
    }

    void doUpgradeDb() {
        SQLiteDatabase db = getReadableDatabase();

        try {
            db.beginTransaction();

            for (Contract contract : listContract) {
                db.execSQL("DROP TABLE IF EXISTS " + contract.getTableName());
            }
            onCreate(db);

            db.setTransactionSuccessful();
        }finally {
            db.endTransaction();
            db.close();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            for (Contract contract: listContract) {
                ValidateSqlLiteType(contract);
                db.execSQL(getQueryCreateTable(contract));
            }
        } catch (Exception e) {
            Toast.makeText(context,e.toString(),Toast.LENGTH_LONG).show();
        }
    }

    private String getQueryCreateTable(Contract contract)
    {
        String[] listColumn = contract.getListColumn();
        String[] listType = contract.getListType();
        String[] listPrimaryKey = contract.getPrimaryKey();

        String textCreateColumn = "";
        String textPrimaryKey   = "";

        for(int i=0; i < listColumn.length; i++){
            textCreateColumn += "\"" + listColumn[i] +"\"" + " " + listType[i] + " NOT NULL";
            if(i != listColumn.length -1){
            textCreateColumn += ", ";
            }
        }
        for(int pK=0; pK < listPrimaryKey.length; pK++){
            textPrimaryKey += "\""+listPrimaryKey[pK]+"\"";
            if(pK != listPrimaryKey.length -1){
                textPrimaryKey += ", ";
            }
        }

        return " CREATE TABLE " + "\"" + contract.getTableName() + "\"" +
                " (" + textCreateColumn + ", PRIMARY KEY(" + textPrimaryKey +"));";
    }

    private void ValidateSqlLiteType(Contract contract) throws Exception {
        SqlLiteDataType sqlLiteDataType = new SqlLiteDataType();
        String[] listContractType = contract.getListType();
        for(String contractType: listContractType)
        {
            Field[] listField = sqlLiteDataType.getClass().getFields();
            Field field;

            for (int i=0 ; i < listField.length; i++) {
                field = listField[i];
                if (field.getName().equals("$change"))
                    continue;

                if (field.getName().equals(contractType))
                    break;

                if (!field.getName().equals(contractType) && i == listField.length -1)
                    throw new Exception("Type : ["+contractType+"] Not Supported on Table "+contract.getTableName());
            }


        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        isNeedUpgrade=true;
    }
}
