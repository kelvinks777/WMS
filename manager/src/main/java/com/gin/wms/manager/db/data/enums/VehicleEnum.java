package com.gin.wms.manager.db.data.enums;

/**
 * Created by bintang on 4/3/2018.
 */

public enum VehicleEnum {
    TYPE("vehicle_type"),
    CATEGORY_1("vehicle_category_1"),
    CATEGORY_2("vehicle_category_2");

    private final String value;
    VehicleEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
