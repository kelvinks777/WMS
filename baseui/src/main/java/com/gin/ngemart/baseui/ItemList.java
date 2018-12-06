package com.gin.ngemart.baseui;

import com.bosnet.ngemart.libgen.Data;

public   class ItemList
{
    public final static String ITEM_TITLE = "itemTitle";
    public final static String ITEM_SUB_TITLE = "itemSubTitle";

    public String itemTitle = "";
    public String itemSubTitle = "";
    public boolean isHeader = false;
    public Data data;

    public ItemList(String itemTitle, String itemSubTitle, boolean isHeader, Data data) {
        this.itemTitle = itemTitle;
        this.itemSubTitle = itemSubTitle;
        this.isHeader = isHeader;
        this.data = data;
    }

    @Override
    public String toString() {
        return "ItemList{" +
                "itemTitle='" + itemTitle + '\'' +
                ", itemSubTitle='" + itemSubTitle + '\'' +
                ", isHeader=" + isHeader +
                '}';
    }
}
