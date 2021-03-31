package ru.aumsu.www.application

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.aumsu.www.application.models.Authorization
import ru.aumsu.www.application.models.User
import ru.aumsu.www.application.retrofit.RequestAPI

class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(R.style.AppTheme)
        val sp = getSharedPreferences(packageName, Context.MODE_PRIVATE)
        if(sp.getString(USER_TOKEN_KEY, "") != "") {
            userData = Gson().fromJson(sp.getString(USER_DATA_KEY, ""), User::class.java)

            if(hasConnection()) {
                val service = getRetrofit().create<RequestAPI>(RequestAPI::class.java)

                val messages = service.authorization(
                    Authorization(
                        userData!!.login,
                        userData!!.password
                    )
                )

                messages.enqueue(object : Callback<User> {
                    override fun onFailure(call: Call<User>, t: Throwable) {
                        Toast.makeText(this@SplashActivity, "Ошибка авторизации, пожалуйста, повторите попытку", Toast.LENGTH_LONG).show()
                    }

                    override fun onResponse(call: Call<User>, response: Response<User>) {
                        if(response.code() == 200) {
                            userData = response.body()

                            sp.edit()
                                .putString(USER_DATA_KEY, Gson().toJson(userData))
                                .putString(USER_TOKEN_KEY, userData!!.token).apply()
                        } else {
                            Toast.makeText(this@SplashActivity, "Неверный логин или пароль", Toast.LENGTH_LONG).show()
                        }
                    }
                })
            } else {
                Toast.makeText(this, "Отсутствует интернет соединение", Toast.LENGTH_LONG).show()
            }
            startActivity(MainActivity::class.java)
            finish()
        } else {
            startActivity(LoginActivity::class.java)
            finish()
        }
    }
}
