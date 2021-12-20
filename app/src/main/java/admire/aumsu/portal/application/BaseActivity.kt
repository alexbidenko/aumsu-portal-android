package admire.aumsu.portal.application

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import admire.aumsu.portal.application.models.User
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import com.google.gson.FieldNamingPolicy

import com.google.gson.GsonBuilder

import com.google.gson.Gson





abstract class BaseActivity : AppCompatActivity() {

    private val forwardAnimation: PendingTransitionAnimation =
        PendingTransitionAnimation(
            R.anim.forward_open,
            R.anim.forward_close
        )

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

    fun startActivityWithTransition(
        intent: Intent
    ) {
        startActivity(intent)
        overridePendingTransition(
            forwardAnimation.open,
            forwardAnimation.close
        )
    }

    class PendingTransitionAnimation internal constructor(var open: Int, var close: Int)

    fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .client(OkHttpClient().newBuilder().build())
            .baseUrl(getString(R.string.base_url) + "/api/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    companion object {
        const val USER_TOKEN_KEY = "user_token"
        const val USER_DATA_KEY = "user_data"

        const val AUTHORIZATION_LOGIN_KEY = "authorization_login_key"
        const val AUTHORIZATION_PASSWORD_KEY = "authorization_password_key"

        var userData: User? = null

        val gson = GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()!!
    }
}