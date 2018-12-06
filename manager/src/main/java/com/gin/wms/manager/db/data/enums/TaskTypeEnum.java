package com.gin.wms.manager.db.data.enums;

public enum TaskTypeEnum {
    REWAREHOUSING(1), REPLENISHMENT_BOX(2), REPLENISHMENT_PCS(3), STAGING_TO_DOCKING(4), INTERNAL_MOVEMENT(5);

    private final int value;
    TaskTypeEnum(int value) {
        this.value = value;
    }

    public static TaskTypeEnum init (int value) {
        for (TaskTypeEnum typeEnum : values()) {
            if (typeEnum.getValue() == value)
                return typeEnum;
        }
        return null;
    }

    public int getValue() {
        return value;
    }
}
