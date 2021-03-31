package ru.aumsu.www.application

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_login.*
import ru.aumsu.www.application.models.User
import android.widget.Toast
import android.content.Context
import ru.aumsu.www.application.retrofit.RequestAPI
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.aumsu.www.application.models.Authorization

class LoginActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login_button.setOnClickListener {
            authorization()
        }
    }

    private fun authorization() {
        if(hasConnection()) {
            val service = getRetrofit().create<RequestAPI>(RequestAPI::class.java)

            val messages = service.authorization(
                Authorization(
                    login.text.toString(),
                    password.text.toString()
                )
            )

            messages.enqueue(object : Callback<User> {
                override fun onFailure(call: Call<User>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, "Ошибка авторизации, пожалуйста, повторите попытку", Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if(response.code() == 200) {
                        userData = response.body()

                        val sp = getSharedPreferences(packageName, Context.MODE_PRIVATE)
                        sp.edit()
                            .putString(USER_DATA_KEY, Gson().toJson(userData))
                            .putString(USER_TOKEN_KEY, userData!!.token).apply()

                        startActivity(MainActivity::class.java)
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Неверный логин или пароль", Toast.LENGTH_LONG).show()
                    }
                }
            })
        } else {
            Toast.makeText(this, "Отсутствует интернет соединение", Toast.LENGTH_LONG).show()
        }
    }
}
