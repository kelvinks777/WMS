package com.gin.ngemart.baseui;

import android.content.Context;

import com.bosnet.ngemart.libgen.Data;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by luis on 4/4/2016.
 * Purpose :
 */
public class NgemartAdapterWithHeader {
    Context context;
    List<ItemList> listItem = new ArrayList<>();
    NgemartAdapter<ItemList> adapter = null;
    public NgemartAdapterWithHeader(Context context) {
        this.context = context;
    }

    public void setSource(List<DataItem> dataItemList) throws Exception {
        listItem.clear();
        for (DataItem dataItem : dataItemList) {
            ItemList itemList = new ItemList(dataItem.HeaderTitle,"",true,null);
            listItem.add(itemList);
            ItemList itemListData;

            for (Data data: dataItem.listData) {
                Field fieldTitle = data.getClass().getField(dataItem.fieldTitle);
                Field fieldSubTitle = data.getClass().getField(dataItem.fieldSubTitle);
                if(fieldSubTitle.getType() == Date.class)
                {
                    Date dateData = (Date) fieldSubTitle.get(data);
                    DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    String dateStringData = format.format(dateData);
                    itemListData = new ItemList((String) fieldTitle.get(data), dateStringData,false,data );
                }
                else
                    itemListData = new ItemList((String) fieldTitle.get(data), fieldSubTitle.get(data).toString(),false,data );

                listItem.add(itemListData);
            }
        }

        adapter = new NgemartAdapter<>(context, listItem);
        adapter.setFieldTitle(ItemList.ITEM_TITLE);
        adapter.setFieldSubTitle(ItemList.ITEM_SUB_TITLE);
    }

    public NgemartAdapter getAdapter() {return adapter;}
}

