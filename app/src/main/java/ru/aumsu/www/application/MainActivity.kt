package ru.aumsu.www.application

import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.nav_header_main.view.*
import ru.aumsu.www.application.models.Message
import ru.aumsu.www.application.services.MessagesService

class MainActivity : BaseActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private var messagesDisposable: Disposable? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(userData == null) {
            startActivity(LoginActivity::class.java)
            finish()
            return
        }

        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_gallery,
                R.id.nav_slideshow,
                R.id.nav_tools,
                R.id.nav_share,
                R.id.nav_send,
                R.id.nav_logout
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        Glide.with(this).load(userData!!.avatar).circleCrop().into(navView.getHeaderView(0).avatar)
        navView.getHeaderView(0).name.text = userData!!.firstName + " " + userData!!.lastName
        navView.getHeaderView(0).email.text = userData!!.login

        if(userData!!.status == "user") {
            navView.menu.findItem(R.id.nav_send).isVisible = false
        }

//        getLastMessage()

        startService(Intent(applicationContext, MessagesService::class.java))

        drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(newState: Int) {
                val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                var view = currentFocus
                if (view == null) {
                    view = View(this@MainActivity)
                }
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}

            override fun onDrawerClosed(drawerView: View) {}

            override fun onDrawerOpened(drawerView: View) {}

        })
    }

    override fun onResume() {
        super.onResume()
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
        isAppRunning = true
        messagesDisposable = MessagesService.messagesObservable
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { message, _ -> showMessage(message) }
    }

    override fun onPause() {
        super.onPause()
        isAppRunning = false
        messagesDisposable?.dispose()
    }

//    private fun getLastMessage() {
//        if(hasConnection()) {
//            val service = getRetrofit().create<RequestAPI>(RequestAPI::class.java)
//
//            val messages = service.getLastMessage(userData!!.token)
//
//            messages.enqueue(object : Callback<Message> {
//                override fun onFailure(call: Call<Message>, t: Throwable) {}
//
//                override fun onResponse(call: Call<Message>, response: Response<Message>) {
//                    if(response.code() == 200) {
//                        showMessage(response.body()!!)
//                    }
//                }
//            })
//        } else {
//            Toast.makeText(this, "Отсутствует интернет соединение", Toast.LENGTH_LONG).show()
//        }
//    }

    private fun showMessage(message: Message) {
        val sp = getSharedPreferences(packageName, Context.MODE_PRIVATE)
        if(message.id != null && message.id > sp.getInt(LAST_MESSAGE_ID_KEY, -1)) {
            sp.edit().putInt(LAST_MESSAGE_ID_KEY, message.id).apply()

            Log.i("Admire", "show message")
            AlertDialog.Builder(this)
                .setMessage(message.message)
                .setTitle("Сообщение")
                .setPositiveButton("Принято") { _, _ ->
                    run {}
                }.create().show()
        }
    }

    fun messageSendDialog() {
        AlertDialog.Builder(this)
            .setMessage("Сообщение успешно отправлено")
            .setPositiveButton("Ок") { _, _ ->
                run {}
            }.create().show()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    companion object {
        private const val LAST_MESSAGE_ID_KEY = "last_message_id"
        var isAppRunning = false
    }
}
