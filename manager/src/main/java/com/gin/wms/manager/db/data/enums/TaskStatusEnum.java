package com.gin.wms.manager.db.data.enums;

/**
 * Created by manbaul on 3/6/2018.
 */

public enum TaskStatusEnum {
    NEW(0), PROGRESS(1), PENDING(2), FINISH(3), INTERRUPT(4);

    private final int value;
    TaskStatusEnum(int value) {
        this.value = value;
    }

    public static TaskStatusEnum init (int value) {
        for (TaskStatusEnum taskEnum : values()) {
            if (taskEnum.getValue() == value)
                return taskEnum;
        }
        return null;
    }

    public int getValue() {
        return value;
    }
}
