package com.gin.ngemart.baseui.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.bosnet.ngemart.libgen.GsonMapper;
import com.gin.ngemart.baseui.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by manbaul on 9/23/2016.
 */

public class MessagingService extends FirebaseMessagingService {
    public static final String FROM_MESSAGING_SERVICE_INTENT = "fromMessagingService";
    static final protected int ERROR_NOTIF_REQUEST_CODE = 100;
    LocalBroadcastManager localBroadcastManager = null;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        if (remoteMessage.getData().size() > 0) {
            GsonMapper gsonMapper = new GsonMapper();
            PushedData pushedData = PushedData.getDataFromMap(remoteMessage.getData());
            String pushedDataString = gsonMapper.write(pushedData);
            Intent intent = new Intent();
            intent.setAction(remoteMessage.getData().get("subjectId"));
            intent.putExtra("pushedData", pushedDataString);
            localBroadcastManager.sendBroadcast(intent);
        }
        else
        {
            throw new NullPointerException("PushedData is null");
        }
    }

    protected void sendNotification(Context context, PendingIntent pendingIntent, String title, String message, int requestCode, Bitmap largeIcon) {
        sendNotification(context,pendingIntent,title,message,requestCode,largeIcon,0);
    }

    protected void sendNotification(Context context, PendingIntent pendingIntent, String title, String message, int requestCode, Bitmap largeIcon,int smallIcon) {
        int defaultSmallIcon = R.drawable.ic_cart_toolbar;
        if (smallIcon != 0)
            defaultSmallIcon = smallIcon;
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(defaultSmallIcon)
                .setLargeIcon(largeIcon)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message));

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(requestCode, notificationBuilder.build());
    }

    protected void sendNotification(Context context, Class cls, String title, PushedData pushedData, int requestCode, Bitmap largeIcon) {
        sendNotification (context, cls, title, pushedData, requestCode, largeIcon,0);
    }

    protected void sendNotification(Context context, Class cls, String title, PushedData pushedData, int requestCode, Bitmap largeIcon,int smallIcon) {
        Intent intent = new Intent(context, cls);
        intent.putExtra("id", pushedData.referenceId);
        intent.putExtra(FROM_MESSAGING_SERVICE_INTENT, true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, requestCode, intent,
                PendingIntent.FLAG_ONE_SHOT);

        sendNotification (context, pendingIntent, title, pushedData.message, requestCode, largeIcon, smallIcon);
    }


}
