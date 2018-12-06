package com.gin.wms.manager.db.contract;

import com.bosnet.ngemart.libgen.SqlLiteDataType;
import com.gin.wms.manager.db.contract.base.ProductBaseContract;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by manbaul on 4/23/2018.
 */

public class PutawayTaskItemContract extends ProductBaseContract {
    public static final String TABLE_NAME = "putaway_task_item";

    public PutawayTaskItemContract() throws Exception {
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
                Column.SOURCE_ID,
                Column.QTY,
                Column.PALLET_NO,
                Column.QTY_COMPUOM_VALUE,
                Column.EXPIRED_DATE,
        };

        Collections.addAll(arrayList, list);
        return arrayList.toArray(new String[0]);
    }

    @Override
    public String[] getListType() {
        ArrayList<String> arrayList = new ArrayList<>();
        Collections.addAll(arrayList, super.getListType());

        String[] list = new String[]{
                Type.SOURCE_ID,
                Type.QTY,
                Type.PALLET_NO,
                Type.QTY_COMPUOM_VALUE,
                Type.EXPIRED_DATE,
        };

        Collections.addAll(arrayList, list);
        return arrayList.toArray(new String[0]);
    }

    @Override
    public String[] getPrimaryKey() {
        ArrayList<String> arrayList = new ArrayList<>();
        Collections.addAll(arrayList, super.getPrimaryKey());

        String[] list = new String[]{
                Column.SOURCE_ID,
        };

        Collections.addAll(arrayList, list);
        return arrayList.toArray(new String[0]);
    }

    public class Column {
        public final static String SOURCE_ID = "sourceId";
        public final static String QTY = "qty";
        public final static String PALLET_NO = "palletNo";
        public final static String QTY_COMPUOM_VALUE = "qtyCompUomValue";
        public final static String EXPIRED_DATE = "expiredDate";
    }

    public class Type {
        public final static String SOURCE_ID = SqlLiteDataType.TEXT;
        public final static String QTY = SqlLiteDataType.DOUBLE;
        public final static String PALLET_NO = SqlLiteDataType.TEXT;
        public final static String QTY_COMPUOM_VALUE = SqlLiteDataType.TEXT;
        public final static String EXPIRED_DATE = SqlLiteDataType.DATE;
    }

    public static class Query {
        public static String getSelectAll() {
            return "select * from " + TABLE_NAME;
        }
    }
}
