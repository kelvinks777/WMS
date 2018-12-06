package com.bosnet.ngemart.libgen;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * Created by luis on 2/12/2016.
 * Purpose : access database with auto mapping to class data, don't add business flow specific here ..
 */
public class DbClient {
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private final Context context;
    private SQLiteDatabase dbWritable;
    private DbHelper dbHelper;
    private DbConfig config;

    private long threadId;

    private int openTransactionCount = 0;
    private int openConnectionCount = 0;

    public DbClient(Context context, DbConfig config) {
        this.context = context;
        this.config = config;
        threadId = Thread.currentThread().getId();
        ShowThreadInfo();
    }

    public static boolean IsEqualThreadId(DbClient Tx) {
        boolean temp;
        temp = Tx != null && Thread.currentThread().getId() == Tx.threadId;

        return temp;
    }

    public static boolean IsRelease(DbClient Tx) {
        return Tx == null || Tx.dbWritable == null;

    }

    public static <T extends Data> void ValidateData(T data) throws IllegalAccessException {
        if (data == null) throw new NullPointerException("data");
        Field[] listField = data.getClass().getFields();
        for (Field field : listField) {
            if (field.getName().equals("$change"))
                continue;
            if (field.get(data) == null)
                throw new NullPointerException(field.getName());
        }
    }

    public boolean isNeedUpgrade() {
        try {
            dbHelper = new DbHelper(context, config);
            return dbHelper.isNeedUpgrade();
        } catch (Exception e) {
            return false;
        }
    }

    public void doUpgrade() throws Exception {
        dbHelper.doUpgradeDb();
    }

    private void ShowThreadInfo() {
        Log.d("DbClient", "Thrad  T:" + threadId + " CON:" + openConnectionCount + " TRAN:" + openTransactionCount);
    }

    public void OpenConnection() throws Exception {
        if (openConnectionCount == 0)
            dbWritable = getDbWritable();

        openConnectionCount += 1;
        ShowThreadInfo();
    }

    private void ValidateDisposedConnection() throws Exception {
        if (dbWritable == null)
            throw new Exception("Database has disposed.");
    }

    public void BeginTransaction() throws Exception {
        ValidateDisposedConnection();

        if (openTransactionCount == 0)
            dbWritable.beginTransactionNonExclusive();

        openTransactionCount += 1;
        ShowThreadInfo();
    }

    public void CommitTransaction() {
        openTransactionCount -= 1;

        if (openTransactionCount == 0) {
            dbWritable.setTransactionSuccessful();
            dbWritable.endTransaction();
        }

        ShowThreadInfo();
    }

    private void RollbackTransaction() {
        dbWritable.endTransaction();
        openTransactionCount = 0;
        ShowThreadInfo();
    }

    public void ReleaseConnection() throws Exception {

        openConnectionCount -= 1;
        if (dbWritable != null && openConnectionCount <= 0) {
            if (openTransactionCount > 0)
                RollbackTransaction();

            openConnectionCount = 0;
            dbWritable.close();
            dbWritable = null;
        }

        ShowThreadInfo();
    }

    public <T extends Data> T Find(Class<T> theClass, String valueKey) throws Exception {
        return Find(theClass, new String[]{valueKey});
    }

    public <T extends Data> T Find(Class<T> theClass, String[] keyValue) throws Exception {

        SQLiteDatabase dbReadable = getDbReadable();

        T data = theClass.newInstance();
        String[] listColumn = data.getContract().getListColumn();
        String[] listColumnPrimaryKey = data.getContract().getPrimaryKey();
        String selection = generateKeySelection(listColumnPrimaryKey);

        Cursor cursor = dbReadable.query(data.getContract().getTableName(),
                listColumn, selection, keyValue, "", "", "");
        if (cursor.getCount() == 0)
            return null;
        cursor.moveToNext();
        MappingCursorToData(data, cursor);
        cursor.close();
        dbReadable.close();
        return data;
    }

    public void Update(Data data) throws Exception {
        if (data == null) throw new NullPointerException("data");

        String[] listColumn = data.getContract().getListColumn();

        ContentValues contentValues = mappingDataToContentValues(data, listColumn);
        dbWritable.replaceOrThrow(data.getContract().getTableName(), null, contentValues);
    }

    private long JustUpdate(Data data) throws Exception {
        if (data == null) throw new NullPointerException("data");

        String[] listColumn = data.getContract().getListColumn();

        ContentValues contentValues = mappingDataToContentValues(data, listColumn);
        return dbWritable.replace(data.getContract().getTableName(), null, contentValues);
    }

    public void Delete(Data model) throws Exception {
        Contract contract = model.getContract();

        String[] listColumnPrimaryKey = contract.getPrimaryKey();
        String[] listValueOfPrimaryKey = generateKeySelectionValue(model, listColumnPrimaryKey);
        String selection = generateKeySelection(listColumnPrimaryKey);
        dbWritable.delete(contract.getTableName(), selection, listValueOfPrimaryKey);
    }

    public void Delete(String tableName, String expressionField, String expressionValue) {
        String selection = generateKeySelection(new String[] {expressionField});
        dbWritable.delete(tableName, selection, new String[] {expressionValue});
    }

    public void Delete(String tableName, String[] expressionField, String[] expressionValue) {
        String selection = generateKeySelection(expressionField);
        dbWritable.delete(tableName, selection, expressionValue);
    }

    public <T extends Data> void DeleteAll(Class<T> theClass) throws Exception {
        T data = theClass.newInstance();
        dbWritable.execSQL("Delete from " + data.getContract().getTableName());
    }

    public <T extends Data> void InsertAll(List<T> listData) throws Exception {

        if (listData == null)
            throw new NullPointerException("listData");
        if (listData.size() == 0)
            return;

        for (Data data : listData) {
            ValidateData(data);
            String[] listColumn = data.getContract().getListColumn();
            ContentValues contentValues = mappingDataToContentValues(data, listColumn);
            dbWritable.insertOrThrow(data.getContract().getTableName(), "", contentValues);
        }
    }

    public <T extends Data> void Insert(T data) throws Exception {
        ValidateData(data);

        String[] listColumn = data.getContract().getListColumn();
        ContentValues contentValues = mappingDataToContentValues(data, listColumn);
        dbWritable.insertOrThrow(data.getContract().getTableName(), "", contentValues);
    }

    public <T extends Data> void Save(T data) throws Exception {
        ValidateData(data);
        if (JustUpdate(data) < 1)
            Insert(data);
    }

    public Cursor Query(String query) throws Exception {
        SQLiteDatabase dbReadable = getDbReadable();
        return dbReadable.rawQuery(query, null);
    }

    public void Execute(String query) {
        dbWritable.execSQL(query);
    }

    public <T extends Data> List<T> Query(Class<T> theClass, String query) throws Exception {
        SQLiteDatabase dbReadable = getDbReadable();
        List<T> listData = new ArrayList<>();
        T data;

        Cursor cursor = dbReadable.rawQuery(query, null);

        if (cursor.getCount() == 0)
            return new ArrayList<>();
        while (cursor.moveToNext()) {
            data = theClass.newInstance();
            MappingCursorToData(data, cursor);
            listData.add(data);
        }
        cursor.close();
        dbReadable.close();
        return listData;
    }

    private String generateKeySelection(String[] listColumnPrimaryKey) {
        String selection = "";
        for (int i = 0; i < listColumnPrimaryKey.length; i++) {
            selection += listColumnPrimaryKey[i] + " = ? ";
            if (i != (listColumnPrimaryKey.length - 1)) {
                selection += " AND ";
            }
        }
        return selection;
    }

    private <T extends Data> ContentValues mappingDataToContentValues(T data, String[] listColumn) throws Exception {
        ContentValues contentValues = new ContentValues();
        for (String column : listColumn) {
            Field field = data.getClass().getField(column);
            if (field.getType() == String.class)
                contentValues.put(column, (String) field.get(data));
            else if (field.getType() == Integer.class)
                contentValues.put(column, (Integer) field.get(data));
            else if (field.getType() == int.class)
                contentValues.put(column, (Integer) field.get(data));
            else if (field.getType() == Long.class)
                contentValues.put(column, (Long) field.get(data));
            else if (field.getType() == long.class)
                contentValues.put(column, (Long) field.get(data));
            else if (field.getType() == Double.class)
                contentValues.put(column, (Double) field.get(data));
            else if (field.getType() == double.class)
                contentValues.put(column, (double) field.get(data));
            else if (field.getType() == float.class)
                contentValues.put(column, (float) field.get(data));
            else if (field.getType() == Float.class)
                contentValues.put(column, (Float) field.get(data));
            else if (field.getType() == boolean.class)
                contentValues.put(column, field.getBoolean(data) ? 1 : 0);
            else if (field.getType() == Boolean.class)
                contentValues.put(column, field.getBoolean(data) ? 1 : 0);
            else if (field.getType() == Date.class)
                contentValues.put(column, getDateTime((Date) field.get(data)));
            else if (field.getType() == byte[].class)
                contentValues.put(column, (byte[]) field.get(data));
            else {
                throw new Exception("Field to ContentVales Type data not supported : " + field.getType().toString());
            }
        }
        return contentValues;
    }

    String getDateTime(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                DATE_FORMAT, Locale.getDefault());
        return dateFormat.format(date);
    }

    private SQLiteDatabase getDbReadable() throws Exception {
        dbHelper = new DbHelper(context, config);
        return dbHelper.getReadableDatabase();
    }


    private <T extends Data> void MappingCursorToData(T data, Cursor cursor) throws Exception {
        Field[] fieldList = data.getClass().getFields();
        int colCount = fieldList.length;
        for (int i = 0; i < colCount; i++) {
            Field field = fieldList[i];
            if (field.getName().equals("serialVersionUID"))
                continue;
            if (field.getName().equals("$change"))
                continue;
            if (field.getType() == List.class)
                continue;
            if (field.getType() == String.class)
                field.set(data, cursor.getString(cursor.getColumnIndex(field.getName())));
            else if (field.getType() == Integer.class)
                field.set(data, cursor.getInt(cursor.getColumnIndex(field.getName())));
            else if (field.getType() == int.class)
                field.set(data, cursor.getInt(cursor.getColumnIndex(field.getName())));
            else if (field.getType() == Long.class)
                field.set(data, cursor.getLong(cursor.getColumnIndex(field.getName())));
            else if (field.getType() == long.class)
                field.set(data, cursor.getLong(cursor.getColumnIndex(field.getName())));
            else if (field.getType() == Double.class)
                field.set(data, cursor.getDouble(cursor.getColumnIndex(field.getName())));
            else if (field.getType() == double.class)
                field.set(data, cursor.getDouble(cursor.getColumnIndex(field.getName())));
            else if (field.getType() == float.class)
                field.set(data, cursor.getFloat(cursor.getColumnIndex(field.getName())));
            else if (field.getType() == Float.class)
                field.set(data, cursor.getFloat(cursor.getColumnIndex(field.getName())));
            else if (field.getType() == Boolean.class)
                field.set(data, cursor.getInt(cursor.getColumnIndex(field.getName())) == 1);
            else if (field.getType() == boolean.class)
                field.set(data, cursor.getInt(cursor.getColumnIndex(field.getName())) == 1);
            else if (field.getType() == Date.class) {
                String value = cursor.getString(cursor.getColumnIndex(field.getName()));
                field.set(data, getDateFromString(value));
            } else if (field.getType() == byte[].class)
                field.set(data, cursor.getBlob(cursor.getColumnIndex(field.getName())));
            else {
                throw new Exception("Cursor to Field Type data not supported : " + field.getType().toString());
            }
        }
    }

    Date getDateFromString(String stringDate) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        return dateFormat.parse(stringDate);
    }

    private SQLiteDatabase getDbWritable() throws Exception {
        dbHelper = new DbHelper(context, config);
        return dbHelper.getWritableDatabase();
    }


    private String[] generateKeySelectionValue(Data model, String[] keyColumn) throws Exception {
        String[] keyValue = new String[keyColumn.length];
        for (int i = 0; i < keyValue.length; i++) {
            Field field = model.getClass().getField(keyColumn[i]);
            if (field.getType() == String.class)
                keyValue[i] = (String) field.get(model);
            else
                throw new Exception("Primary Key must be String Type");
        }
        return keyValue;
    }
}
