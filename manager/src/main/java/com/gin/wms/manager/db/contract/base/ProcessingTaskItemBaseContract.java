package com.gin.wms.manager.db.contract.base;

import com.bosnet.ngemart.libgen.SqlLiteDataType;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Fernandes on 10/05/2018.
 */

public abstract class ProcessingTaskItemBaseContract extends ProductBaseContract {
    public ProcessingTaskItemBaseContract() throws Exception {
        super();
    }

    @Override
    public String[] getListColumn() {
        ArrayList<String> arrayList = new ArrayList<>();
        Collections.addAll(arrayList, super.getListColumn());

        String[] list = new String[]{
                Column.QTY,
                Column.PALLET_QTY,
                Column.QTY_CHECK_RESULT,
                Column.QTY_COMP_UOM_VALUE,
                Column.QTY_CHECK_RESULT_COMP_UOM_VALUE,
        };

        Collections.addAll(arrayList, list);
        return arrayList.toArray(new String[0]);
    }

    @Override
    public String[] getListType() {
        ArrayList<String> arrayList = new ArrayList<>();
        Collections.addAll(arrayList, super.getListType());

        String[] list = new String[]{
                Type.QTY,
                Type.PALLET_QTY,
                Type.QTY_CHECK_RESULT,
                Type.QTY_COMP_UOM_VALUE,
                Type.QTY_CHECK_RESULT_COMP_UOM_VALUE,
        };

        Collections.addAll(arrayList, list);
        return arrayList.toArray(new String[0]);
    }

    @Override
    public String[] getPrimaryKey() {
        return super.getPrimaryKey();
    }

    public class Column {
        public final static String QTY_COMP_UOM_VALUE = "qtyCompUomValue";
        public final static String PALLET_QTY = "palletQty";
        public final static String QTY_CHECK_RESULT_COMP_UOM_VALUE = "qtyCheckResultCompUomValue";
        public final static String QTY = "qty";
        public final static String QTY_CHECK_RESULT = "qtyCheckResult";
    }

    public class Type {
        public final static String QTY = SqlLiteDataType.DOUBLE;
        public final static String PALLET_QTY = SqlLiteDataType.DOUBLE;
        public final static String QTY_CHECK_RESULT = SqlLiteDataType.DOUBLE;
        public final static String QTY_COMP_UOM_VALUE = SqlLiteDataType.TEXT;
        public final static String QTY_CHECK_RESULT_COMP_UOM_VALUE = SqlLiteDataType.TEXT;
    }
}
