package com.gin.ngemart.baseui;

import java.util.List;

/**
 * Created by luis on 3/28/2018.
 */

public interface NgemartFinderProcessor <T> {
    List<T> getData(String tag) throws Exception;
    String getDialogTitle(String tag);
    String getFieldTitle(String tag);
    String getFieldSubTitle (String tag);
    String getFieldImage(String tag);
    void onSelected(T data, String tag);
}
