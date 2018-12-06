package com.gin.wms.manager.db.data.enums;

/**
 * Created by manbaul on 3/6/2018.
 */

public enum OperatorTypeEnum {
    CHECKER(0), UNLOADER(1), SECURITY(2);

    private final int value;
    OperatorTypeEnum(int value) {
        this.value = value;
    }

    public static OperatorTypeEnum init (int value) {
        for (OperatorTypeEnum balEnum :
                values()) {
            if (balEnum.getValue() == value)
                return balEnum;
        }
        return null;
    }

    public int getValue() {
        return value;
    }
}
