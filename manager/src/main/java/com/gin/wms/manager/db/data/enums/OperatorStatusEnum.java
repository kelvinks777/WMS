package com.gin.wms.manager.db.data.enums;

/**
 * Created by manbaul on 3/6/2018.
 */

public enum OperatorStatusEnum {
    NA(0),
    UNLOADING(1),
    CHECKING(2),
    PUT_AWAY(3),
    PICKING(4),
    LOADING(5),
    MOVING_TO_STAGING(6),
    INVENTORY_TAKING(7),
    COUNTING(8),
    SECURITY(9),
    PUT_AWAY_PER_PRODUCT(10),
    PUT_AWAY_BAD_PRODUCT(11),
    MOVING_TO_DOCKING(12),
    REPLENISHMENT(13),
    REWAREHOUSING (14),
    DESTRUCTING (15),
    MUTATION (16),
    BANDED (17),
    MANUAL_MUTATION_ORDER(18),
    STOCK_COUNTING_ORDER(19);

    private final int value;
    OperatorStatusEnum(int value) {
        this.value = value;
    }

    public static OperatorStatusEnum init (int value) {
        for (OperatorStatusEnum balEnum :
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
