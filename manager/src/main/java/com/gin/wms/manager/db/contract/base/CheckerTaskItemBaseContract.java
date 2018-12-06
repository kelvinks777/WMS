package com.gin.wms.manager.db.contract.base;

import com.bosnet.ngemart.libgen.SqlLiteDataType;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by manbaul on 4/16/2018.
 */

public abstract class CheckerTaskItemBaseContract extends ProductBaseContract {
    public CheckerTaskItemBaseContract() throws Exception {
        super();
    }

    @Override
    public String[] getListColumn() {
        ArrayList<String> arrayList = new ArrayList<>();
        Collections.addAll(arrayList, super.getListColumn());

        String[] list = new String[]{
                Column.POLICE_NO,
                Column.QTY,
                Column.PALLET_QTY,
                Column.GOOD_QTY_CHECK_RESULT,
                Column.BAD_QTY_CHECK_RESULT,
                Column.QTY_COMP_UOM_VALUE,
                Column.GOOD_QTY_CHECK_RESULT_COMP_UOM_VALUE,
                Column.BAD_QTY_CHECK_RESULT_COMP_UOM_VALUE,
        };

        Collections.addAll(arrayList, list);
        return arrayList.toArray(new String[0]);
    }

    @Override
    public String[] getListType() {
        ArrayList<String> arrayList = new ArrayList<>();
        Collections.addAll(arrayList, super.getListType());

        String[] list = new String[]{
                Type.POLICE_NO,
                Type.QTY,
                Type.PALLET_QTY,
                Type.GOOD_QTY_CHECK_RESULT,
                Type.BAD_QTY_CHECK_RESULT,
                Type.QTY_COMP_UOM_VALUE,
                Type.GOOD_QTY_CHECK_RESULT_COMP_UOM_VALUE,
                Type.BAD_QTY_CHECK_RESULT_COMP_UOM_VALUE,
        };

        Collections.addAll(arrayList, list);
        return arrayList.toArray(new String[0]);
    }

    @Override
    public String[] getPrimaryKey() {
        return super.getPrimaryKey();
    }

    public class Column {
        public final static String POLICE_NO = "policeNo";
        public final static String QTY_COMP_UOM_VALUE = "qtyCompUomValue";
        public final static String PALLET_QTY = "palletQty";
        public final static String GOOD_QTY_CHECK_RESULT_COMP_UOM_VALUE = "goodQtyCheckResultCompUomValue";
        public final static String BAD_QTY_CHECK_RESULT_COMP_UOM_VALUE = "badQtyCheckResultCompUomValue";
        public final static String QTY = "qty";
        public final static String GOOD_QTY_CHECK_RESULT = "goodQtyCheckResult";
        public final static String BAD_QTY_CHECK_RESULT = "badQtyCheckResult";
    }

    public class Type {
        public final static String POLICE_NO = SqlLiteDataType.TEXT;
        public final static String QTY = SqlLiteDataType.DOUBLE;
        public final static String PALLET_QTY = SqlLiteDataType.DOUBLE;
        public final static String GOOD_QTY_CHECK_RESULT = SqlLiteDataType.DOUBLE;
        public final static String BAD_QTY_CHECK_RESULT = SqlLiteDataType.DOUBLE;
        public final static String QTY_COMP_UOM_VALUE = SqlLiteDataType.TEXT;
        public final static String GOOD_QTY_CHECK_RESULT_COMP_UOM_VALUE = SqlLiteDataType.TEXT;
        public final static String BAD_QTY_CHECK_RESULT_COMP_UOM_VALUE = SqlLiteDataType.TEXT;
    }
}
