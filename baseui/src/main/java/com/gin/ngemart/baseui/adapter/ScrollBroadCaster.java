package com.gin.ngemart.baseui.adapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luis on 7/20/2017.
 * Purpose : Untuk menotifikasi card supaya tau kalo recycle view berhenti ngescroll
 */

public class ScrollBroadCaster {
    private static List<ScrollListener> listenerList = new ArrayList<>();

    public static void registerReceiver(ScrollListener receiver) {
        boolean isNeedToAdd = true;
        for (ScrollListener rec : listenerList) {
            if (rec.equals(receiver)) {
                isNeedToAdd = false;
                break;
            }
        }
        if (isNeedToAdd) {
            listenerList.add(receiver);
        }
    }

    public static Boolean isListenerExist(ScrollListener receiver) {
        boolean isExist = false;
        for (ScrollListener rec : listenerList) {
            if (rec.equals(receiver)) {
                isExist = true;
                break;
            }
        }

        return isExist;

    }

    public static void unregisterAll() {
        listenerList.clear();
    }

    static void NotifyAllReceiver() {
        for (ScrollListener receiver : listenerList) {
            if (receiver != null) {
                receiver.onScrollIdle();
            }
        }
    }


}
