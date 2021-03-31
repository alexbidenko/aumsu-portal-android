package ru.aumsu.www.application

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.aumsu.www.application.models.User


abstract class BaseActivity : AppCompatActivity() {

    private val forwardAnimation: PendingTransitionAnimation =
        PendingTransitionAnimation(
            R.anim.forward_open,
            R.anim.forward_close
        )
//    private val backAnimation: PendingTransitionAnimation =
//        PendingTransitionAnimation(
//            R.anim.back_open,
//            R.anim.back_close
//        )

    fun startActivity(
        activityClass: Class<*>
    ) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
        overridePendingTransition(
                forwardAnimation.open,
                forwardAnimation.close
        )
    }

    fun hasConnection(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        if (wifiInfo != null && wifiInfo.isConnected) return true
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
        if (wifiInfo != null && wifiInfo.isConnected) return true
        wifiInfo = cm.activeNetworkInfo
        return wifiInfo != null && wifiInfo.isConnected
    }

    class PendingTransitionAnimation internal constructor(var open: Int, var close: Int)

    fun getRetrofit(): Retrofit {
        val gson = GsonBuilder()
                .setLenient()
                .create()

        return Retrofit.Builder()
            .client(OkHttpClient().newBuilder().build())
            .baseUrl("https://aumsu-portal.admire.social/api/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    companion object {
        const val USER_TOKEN_KEY = "user_token"
        const val USER_DATA_KEY = "user_data"

        var userData: User? = null
    }
}