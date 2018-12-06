package com.gin.wms.manager.db.data.enums;

/**
 * Created by manbaul on 4/23/2018.
 */

public enum PutawayTypeEnum {
    PUTAWAY(0), MOVING_TO_STAGING(1), PUTAWAY_PER_PRODUCT(2), PUTAWAY_BAD_PRODUCT(3);

    private final int value;
    PutawayTypeEnum(int value) {
        this.value = value;
    }

    public static PutawayTypeEnum init (int value) {
        for (PutawayTypeEnum balEnum :
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
