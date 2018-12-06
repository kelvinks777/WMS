package com.gin.wms.warehouse.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.RingtoneManager
import android.support.v4.app.NotificationCompat
import com.gin.wms.warehouse.R

/**
 * Created by manbaul on 5/17/2018.
 */
class NotificationTool {
    fun showNotification(context: Context, channelId: String, pendingIntent: PendingIntent, title: String, message: String, requestCode: Int, largeIcon: Bitmap, smallIcon: Int) {
        var defaultSmallIcon = R.drawable.ic_notif_small_default
        if (smallIcon != 0)
            defaultSmallIcon = smallIcon
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(defaultSmallIcon)
                .setLargeIcon(largeIcon)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(requestCode, notificationBuilder.build())
    }


    fun showNotification(context: Context, channelId: String, cls: Class<*>, title: String, message:String, requestCode: Int, largeIcon:Bitmap ) {
        val intent = Intent(context, cls)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        val pendingIntentClass = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        showNotification(context, channelId, pendingIntentClass, title, message , requestCode, largeIcon, 0)
    }

}