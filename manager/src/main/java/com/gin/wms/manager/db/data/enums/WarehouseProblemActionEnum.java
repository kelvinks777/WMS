package com.gin.wms.manager.db.data.enums;

public enum WarehouseProblemActionEnum {
    NO_ACTION(0);

    private final int value;
    WarehouseProblemActionEnum(int value) {
        this.value = value;
    }

    public static WarehouseProblemActionEnum init (int value) {
        for (WarehouseProblemActionEnum wareActionEnum : values()) {
            if (wareActionEnum.getValue() == value)
                return wareActionEnum;
        }
        return null;
    }

    public int getValue() {
        return value;
    }

}
