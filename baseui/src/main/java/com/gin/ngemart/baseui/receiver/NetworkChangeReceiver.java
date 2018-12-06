package com.gin.ngemart.baseui.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by bintang on 12/8/2016.
 */

public class NetworkChangeReceiver extends BroadcastReceiver {
    public static final String NETWORK_INTENT = "ngemart.network.change";
    private LocalBroadcastManager localBroadcastManager;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        Intent i = new Intent();
        i.setAction(NETWORK_INTENT);
        localBroadcastManager.sendBroadcast(i);
    }

}