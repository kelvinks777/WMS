package com.gin.wms.manager.db.data.enums;

public enum WarehouseProblemTypeEnum {
    WRONG_LOCATION(0);

    private final int value;
    WarehouseProblemTypeEnum(int value) {
        this.value = value;
    }

    public static WarehouseProblemTypeEnum init (int value) {
        for (WarehouseProblemTypeEnum wareTypeEnum : values()) {
            if (wareTypeEnum.getValue() == value)
                return wareTypeEnum;
        }
        return null;
    }

    public int getValue() {
        return value;
    }

}
