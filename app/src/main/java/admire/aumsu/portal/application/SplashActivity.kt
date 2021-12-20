package admire.aumsu.portal.application

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import admire.aumsu.portal.application.models.Authorization
import admire.aumsu.portal.application.models.User
import admire.aumsu.portal.application.retrofit.RequestAPI
import android.util.Log

class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(R.style.AppTheme)
        val sp = getSharedPreferences(packageName, Context.MODE_PRIVATE)
        if(sp.getString(USER_TOKEN_KEY, "") != "") {
            Log.i("Admire", "test" + sp.getString(USER_DATA_KEY, "")!!)
            userData = gson.fromJson(sp.getString(USER_DATA_KEY, ""), User::class.java)
            Log.i("Admire", "test" + (userData?.studyGroup?.name ?: "test"))

            startActivity(MainActivity::class.java)
            finish()
        } else {
            startActivity(LoginActivity::class.java)
            finish()
        }
    }
}
