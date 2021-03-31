package ru.aumsu.www.application.services

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
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
import io.reactivex.subjects.SingleSubject
import ru.aumsu.www.application.BaseActivity
import ru.aumsu.www.application.MainActivity
import ru.aumsu.www.application.R
import ru.aumsu.www.application.models.Message
import ru.aumsu.www.application.models.User

class MessagesService : IntentService("MessagesService") {

    private val pusher: Pusher
    private lateinit var channel: Channel
    private var userData: User? = null

    init {
        val options = PusherOptions()
        options.setCluster("eu")
        options.isEncrypted = false

        pusher = Pusher("8da04f0e1ecfefbeaecc", options)
        pusher.connect(object : ConnectionEventListener {
            override fun onConnectionStateChange(p0: ConnectionStateChange) {
                Log.i("Admire", "State changed to " + p0.currentState +
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

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        return Service.START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(CHANNEL_ID, "Сообщения университета", importance)
            mChannel.description = "Канал для получения сообщений от университета"
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }

        updateConnection()
    }

    override fun onHandleIntent(intent: Intent?) {
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

    private fun onGetMessage(message: Message) {
        val sp = getSharedPreferences(packageName, Context.MODE_PRIVATE)
        if(Gson().fromJson(sp.getString(BaseActivity.USER_DATA_KEY, ""), User::class.java).id == message.from) return
        if(MainActivity.isAppRunning) messagesObservable.onSuccess(message)
        else {
            val notificationIntent =
                Intent(this, MainActivity::class.java)
            val contentIntent = PendingIntent.getActivity(
                this,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT
            )

            val builder: NotificationCompat.Builder =
                NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher_foreground)
                    .setContentTitle("Уведомление от университета")
                    .setContentText(message.message)
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true)
                    .setStyle(NotificationCompat.BigTextStyle().bigText(message.message))
                    .addAction(R.drawable.ic_menu_send, "Принято", contentIntent)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)

            val notificationManager =
                NotificationManagerCompat.from(this)
            notificationManager.notify(1, builder.build())
        }
    }

    private fun updateConnection() {
        try {
            val sp = getSharedPreferences(packageName, Context.MODE_PRIVATE)
            userData = Gson().fromJson(sp.getString(BaseActivity.USER_DATA_KEY, ""), User::class.java)
        } catch (e: Exception) {}
        pusher.unsubscribe("study-message")
        channel = pusher.subscribe("study-message")
        createConnect(channel)
    }

    companion object {
        val messagesObservable = SingleSubject.create<Message>()

        private const val CHANNEL_ID = "main_channel"
    }
}
