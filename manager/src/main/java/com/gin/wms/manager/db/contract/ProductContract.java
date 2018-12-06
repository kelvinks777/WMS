package com.gin.wms.manager.db.contract;

import com.gin.wms.manager.db.contract.base.ProductBaseContract;

/**
 * Created by manbaul on 3/26/2018.
 */

public class ProductContract extends ProductBaseContract {
    private static final String TABLE_NAME = "product";

    public ProductContract() throws Exception {
        super();
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String[] getListColumn() {
        return super.getListColumn();
    }

    @Override
    public String[] getListType() {
        return super.getListType();
    }

    @Override
    public String[] getPrimaryKey() {
        return super.getPrimaryKey();
    }

    public static class Query {
        public static String getSelectAll() {
            return "select * from " + TABLE_NAME;
        }
        public static String getSelectByProduct(String productId) {
            return "select * from " + TABLE_NAME + " where " + Column.PRODUCT_ID + "='" + productId + "'";
        }

    }

}
