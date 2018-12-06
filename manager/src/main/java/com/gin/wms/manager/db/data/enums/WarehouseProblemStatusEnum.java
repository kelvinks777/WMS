package com.gin.wms.manager.db.data.enums;

public enum WarehouseProblemStatusEnum {
    NEW(1),RESOLVED(2);

    private final int value;
    WarehouseProblemStatusEnum(int value) {
        this.value = value;
    }

    public static WarehouseProblemStatusEnum init (int value) {
        for (WarehouseProblemStatusEnum wareStatusEnum : values()) {
            if (wareStatusEnum.getValue() == value)
                return wareStatusEnum;
        }
        return null;
    }

    public int getValue() {
        return value;
    }
}
