package com.example.guardianapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class PushNotificationService: FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String?) {
        Log.d("PushNotificationService", "sendRegistrationTokenToServer($token)")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        /*if(remoteMessage.getData().size()>0){
            showNotification(remoteMessage.getData().get("title"),
                          remoteMessage.getData().get("message"));
        }*/

        // Handling notification payload
        Log.d("PushNotificationService","Notification msg received")
        Log.d("PushNotificationService",remoteMessage.notification?.title.toString())
        Log.d("PushNotificationService",remoteMessage.notification?.body.toString())
        if (remoteMessage.notification != null) {
            showNotification(
                remoteMessage.notification!!.title!!,
                remoteMessage.notification!!.body!!
            )
        }
    }

    private fun showNotification(title: String, message: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val channel_id = "notification_channel"
        val builder = NotificationCompat.Builder(this, channel_id)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
            .setContentTitle(title)
            .setContent(getCustomDesign(title, message))

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationChannel = NotificationChannel(
            channel_id, "web_app", NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(notificationChannel)
        notificationManager.notify(0, builder.build())
    }

    private fun getCustomDesign(title: String, message: String): RemoteViews {
        val remoteViews = RemoteViews("com.example.guardianapp", R.layout.notification)
        remoteViews.setTextViewText(R.id.title, title)
        remoteViews.setTextViewText(R.id.message, message)
        remoteViews.setImageViewResource(R.id.icon, R.drawable.ic_launcher_background)
        return remoteViews
    }
}
