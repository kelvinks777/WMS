package com.gin.wms.manager.db.data.enums;

/**
 * Created by manbaul on 6/6/2018.
 */

public enum RefDocUriEnum {
    RECEIVING("com.gin.wms.rcv.receivingorder"),
    RELEASE("com.gin.wms.shp.releaseorder"),
    DESTRUCTION("com.gin.wms.task.destructionorder"),
    MUTATION("com.gin.wms.task.mutationorder"),
    BANDED("com.gin.wms.task.bandedorder");

    private final String value;

    RefDocUriEnum(String value) {
        this.value = value;
    }

    public static RefDocUriEnum init (String value) {
        for (RefDocUriEnum uriEnum :
                values()) {
            if (uriEnum.getValue().equals(value))
                return uriEnum;
        }
        return null;
    }

    public String getValue() {
        return value;
    }
}

