package admire.aumsu.portal.application.services

import admire.aumsu.portal.application.BaseActivity
import admire.aumsu.portal.application.R
import admire.aumsu.portal.application.SplashActivity
import admire.aumsu.portal.application.models.Message
import admire.aumsu.portal.application.models.User
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_CANCEL_CURRENT
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.gson.Gson
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.channel.Channel
import com.pusher.client.connection.ConnectionEventListener
import com.pusher.client.connection.ConnectionState
import com.pusher.client.connection.ConnectionStateChange

class MessagesService : Service() {

    private val pusher: Pusher
    private lateinit var channel: Channel
    private var userData: User? = null

    init {
        val options = PusherOptions()
        options.setCluster("eu")
        options.isEncrypted = true

        pusher = Pusher("8da04f0e1ecfefbeaecc", options)
        pusher.connect(object : ConnectionEventListener {
            override fun onConnectionStateChange(p0: ConnectionStateChange) {
                Log.i(
                    "Admire", "State changed to " + p0.currentState +
                            " from " + p0.previousState
                )
            }

            override fun onError(p0: String, p1: String?, p2: Exception) {
                Log.i("Admire", "There was a problem connecting!")
                p2.message?.let {
                    Log.e("Admire", p2.message!!)
                }
            }
        }, ConnectionState.ALL)
    }

    @SuppressLint("CheckResult")
    private fun createConnect(channel: Channel) {
        Log.i("Admire", "createConnect")
        channel.bind(
            "messages"
        ) { _, _, data ->
            Log.i("Admire", data)
            onGetMessage(
                Gson().fromJson(data, Message::class.java)
            )
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance1 = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel1 = NotificationChannel(
                MAIN_CHANNEL_ID,
                "Системные сообщения",
                importance1
            )
            mChannel1.description = "Канал для системных сообщений приложения"
            mChannel1.setSound(null, null)
            val notificationManager1 = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager1.createNotificationChannel(mChannel1)

            val importance2 = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel2 = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.system_notification_channel_name),
                importance2
            )
            mChannel2.description = getString(R.string.system_notification_channel_description)
            val notificationManager2 = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager2.createNotificationChannel(mChannel2)
        }

        updateConnection()

        val notification = NotificationCompat.Builder(this, MAIN_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_notifications_24)
            .setContentTitle("Новости университета")
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .build()
        startForeground(1, notification)

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun onGetMessage(message: Message) {
        val sp = getSharedPreferences(packageName, Context.MODE_PRIVATE)
        if(Gson().fromJson(sp.getString(BaseActivity.USER_DATA_KEY, ""), User::class.java).id == message.from) return
        else {
            val notificationIntent =
                Intent(this, SplashActivity::class.java)
            notificationIntent.putExtra("fragment", "news")
            val contentIntent = PendingIntent.getActivity(
                this,
                0, notificationIntent,
                FLAG_CANCEL_CURRENT
            )

            val builder: NotificationCompat.Builder =
                NotificationCompat.Builder(this, CHANNEL_ID)
                    .setOngoing(false)
                    .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                    .setContentTitle(message.title)
                    .setContentIntent(contentIntent)
                    .setStyle(NotificationCompat.BigTextStyle().bigText(message.description))
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

    private fun updateConnection() {
        try {
            val sp = getSharedPreferences(packageName, Context.MODE_PRIVATE)
            userData = Gson().fromJson(
                sp.getString(BaseActivity.USER_DATA_KEY, ""),
                User::class.java
            )
        } catch (e: Exception) {}
        pusher.unsubscribe("study-message")
        channel = pusher.subscribe("study-message")
        createConnect(channel)
    }

    companion object {
        private const val MAIN_CHANNEL_ID = "main_channel"
        private const val CHANNEL_ID = "notify_channel"
    }
}
