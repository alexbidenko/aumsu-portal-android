package admire.aumsu.portal.application

import admire.aumsu.portal.application.models.Authorization
import admire.aumsu.portal.application.models.User
import admire.aumsu.portal.application.retrofit.RequestAPI
import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import kotlinx.android.synthetic.main.nav_header_main.view.*
import admire.aumsu.portal.application.services.MessagesService
import android.net.Uri
import android.widget.Toast
import androidx.navigation.fragment.NavHostFragment

import android.os.Build
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : BaseActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

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
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_slideshow,
                R.id.nav_gallery,
                R.id.nav_home,
                R.id.nav_profile
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        updateNavHeader()
        navView.getHeaderView(0).nav_header.setOnClickListener {
            navController.navigate(R.id.nav_profile)
            drawerLayout.close()
        }

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

        navView.menu.findItem(R.id.nav_feedback).setOnMenuItemClickListener {
            val info = packageManager.getPackageInfo(packageName, 0)

            val intent = Intent(Intent.ACTION_SENDTO)
            intent.type = "text/plain"
            intent.data = Uri.parse("mailto:")
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.system_feedback_address)))
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.system_feedback_subject))
            intent.putExtra(
                Intent.EXTRA_TEXT,
                getString(R.string.system_feedback_preview) +
                "\n\n\n\nВерсия приложения: " + info.versionName +
                "\nВерсия сборки Android: " + Build.VERSION.SDK_INT +
                "\nМодель телефона: " + Build.MANUFACTURER + " " + Build.MODEL
            )

            try {
                startActivity(Intent.createChooser(intent, getString(R.string.system_feedback_chooser)))
            } catch (e: Exception){
                Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
            }
            true
        }

        if (intent.getStringExtra("fragment") == "news")
            navController.navigate(R.id.nav_slideshow)

        updateUserData()
        compareVersion()
    }

    fun updateNavHeader() {
        val navView: NavigationView = findViewById(R.id.nav_view)
        if (userData!!.avatar != "")
            Glide.with(this).load(getString(R.string.base_url) + "/files/avatars/" + userData!!.avatar).circleCrop().into(navView.getHeaderView(0).avatar)
        navView.getHeaderView(0).name.text = "${userData!!.firstName} ${userData!!.lastName}"
        navView.getHeaderView(0).email.text = userData!!.login
    }

    private fun updateUserData() {
        val sp = getSharedPreferences(packageName, Context.MODE_PRIVATE)
        val service = getRetrofit().create(RequestAPI::class.java)

        val messages = service.authorization(
            Authorization(
                userData!!.login,
                userData!!.password
            )
        )

        messages.enqueue(object : Callback<User> {
            override fun onFailure(call: Call<User>, t: Throwable) {}

            override fun onResponse(call: Call<User>, response: Response<User>) {
                if(response.code() == 200) {
                    userData = response.body()

                    sp.edit()
                        .putString(USER_DATA_KEY, Gson().toJson(userData))
                        .putString(USER_TOKEN_KEY, userData!!.token).apply()

                    updateNavHeader()
                }
            }
        })
    }

    private fun compareVersion() {
        val pInfo = packageManager.getPackageInfo(packageName, 0)
        val service = getRetrofit().create(RequestAPI::class.java)

        val messages = service.compareVersion(pInfo.versionCode)

        messages.enqueue(object : Callback<Boolean> {
            override fun onFailure(call: Call<Boolean>, t: Throwable) {}

            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if(response.code() == 200 && response.body() == false) {
                    startActivityWithTransition(Intent(this@MainActivity, UpdateActivity::class.java))
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }

    fun messageSendDialog() {
        AlertDialog.Builder(this)
            .setMessage(getString(R.string.system_alert_send_success))
            .setPositiveButton(getString(R.string.system_alert_confirm)) { _, _ ->
                run {}
            }.create().show()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
