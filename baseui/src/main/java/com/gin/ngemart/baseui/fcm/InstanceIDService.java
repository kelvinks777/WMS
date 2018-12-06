package com.gin.ngemart.baseui.fcm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.gin.ngemart.baseui.R;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;

/**
 * Created by manbaul on 9/23/2016.
 */

public class InstanceIDService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() { }

    /**
     * Get device token for cloud messaging.
     *
     * @deprecated  Not for future use.
     * use {@link #GetDeviceToken(String)() GetDeviceToken(String)} instead.
     */
    @Deprecated
    public static String GetDeviceToken(final Context context) throws IOException {
        String firebaseSenderID = context.getResources().getString(R.string.firebaseSenderID);
        return FirebaseInstanceId.getInstance().getToken(firebaseSenderID, FirebaseMessaging.INSTANCE_ID_SCOPE);
    }

    public static String GetDeviceToken(final String senderId) throws IOException {
        return FirebaseInstanceId.getInstance().getToken(senderId, FirebaseMessaging.INSTANCE_ID_SCOPE);
    }

    public static void DeleteDeviceToken() throws IOException {
        FirebaseInstanceId.getInstance().deleteInstanceId();
    }
}
