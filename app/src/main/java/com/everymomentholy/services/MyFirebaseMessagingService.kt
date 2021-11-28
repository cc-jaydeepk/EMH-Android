package com.everymomentholy.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.everymomentholy.ui.activity.MainActivity
import com.everymomentholy.utils.Constants
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*

class MyFirebaseMessagingService : FirebaseMessagingService() {
    var TAG = MyFirebaseMessagingService::class.java.canonicalName
    var addNewToken = ""

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        Log.d(TAG, "PPC Refreshed token: $token")
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)
            handleNow(remoteMessage)
        }

        // Check if message contains a notification payload.
        if (remoteMessage.notification != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.notification!!.body)
            remoteMessage.notification!!.body?.let { sendNotification(it) }
        }
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private fun handleNow(remoteMessage: RemoteMessage) {
        Log.d(TAG, "Short lived task is done.")
        val data: Map<String, String> = remoteMessage.data
        val messageBody = data["body"]
        messageBody?.let { sendNotification(it) }
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private fun sendNotification(messageBody: String) {
        val generator = Random()
        var UNIQUE_NUMBER = 10000
        UNIQUE_NUMBER = generator.nextInt(UNIQUE_NUMBER)
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, UNIQUE_NUMBER, intent,
            PendingIntent.FLAG_ONE_SHOT
        )
        val defaultSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(
            this,
            Constants.CHANNEL_ID
        )
            .setSmallIcon(notificationIcon)
            .setContentTitle("Property Preservation")
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setSound(defaultSoundUri)
        //.setContentIntent(pendingIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mNotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel =
                NotificationChannel(Constants.CHANNEL_ID, Constants.CHANNEL_NAME, importance)
            mChannel.description = Constants.CHANNEL_DESCRIPTION
            Objects.requireNonNull(mNotificationManager)?.createNotificationChannel(mChannel)
            mNotificationManager!!.notify(UNIQUE_NUMBER, notificationBuilder.build())
            notificationBuilder.build().flags = Notification.FLAG_AUTO_CANCEL
        } else {
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
            Objects.requireNonNull(notificationManager)
                ?.notify(UNIQUE_NUMBER, notificationBuilder.build())
            notificationBuilder.build().flags = Notification.FLAG_AUTO_CANCEL
        }
    }

    private val notificationIcon: Int
        private get() {
            val useWhiteIcon =
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
            return 1/*if (useWhiteIcon) {
                R.drawable.ic_back
            } else {
                R.mipmap.ic_launcher
            }*/
        }

    private fun sendRegistrationToServer(token: String) {
        val sharedPref: SharedPreferences =
            getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString(Constants.SHARED_PREF_FIREBASE_INSTANCE_ID, token)
        editor.apply()
        //   updateFirebaseToken(sharedPref, token);
    }
}