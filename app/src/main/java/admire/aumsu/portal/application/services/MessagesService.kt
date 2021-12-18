package admire.aumsu.portal.application.services

import admire.aumsu.portal.application.BaseActivity
import admire.aumsu.portal.application.R
import admire.aumsu.portal.application.SplashActivity
import admire.aumsu.portal.application.models.User
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_CANCEL_CURRENT
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson

class MessagesService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        Log.d("Admire", "Refreshed token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.notification?.let {
            onGetMessage(
                it.title!!,
                it.body!!,
                remoteMessage.data["sender_id"]!!.toInt(),
            )
        }
    }

    private fun onGetMessage(title: String, description: String, from: Int) {
        val sp = getSharedPreferences(packageName, Context.MODE_PRIVATE)
        if(Gson().fromJson(sp.getString(BaseActivity.USER_DATA_KEY, ""), User::class.java).id == from) return
        else {
            val notificationIntent =
                Intent(this, SplashActivity::class.java)
            notificationIntent.putExtra("fragment", "news")
            val contentIntent = PendingIntent.getActivity(
                this,
                0,
                notificationIntent,
                FLAG_CANCEL_CURRENT
            )

            val builder: NotificationCompat.Builder =
                NotificationCompat.Builder(this, CHANNEL_ID)
                    .setOngoing(false)
                    .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                    .setContentTitle(title)
                    .setContentIntent(contentIntent)
                    .setStyle(NotificationCompat.BigTextStyle().bigText(description))
                    .addAction(
                        R.drawable.ic_menu_send,
                        getString(R.string.system_alert_agree),
                        contentIntent
                    )
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_MAX)

            val notificationManager =
                NotificationManagerCompat.from(this)
            notificationManager.notify(2, builder.build())
        }
    }

    companion object {
        const val CHANNEL_ID = "notify_channel"
    }
}
