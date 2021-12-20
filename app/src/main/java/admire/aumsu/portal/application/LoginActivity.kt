package admire.aumsu.portal.application

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_login.*
import admire.aumsu.portal.application.models.User
import android.widget.Toast
import android.content.Context
import admire.aumsu.portal.application.retrofit.RequestAPI
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import admire.aumsu.portal.application.models.Authorization
import android.content.Intent

class LoginActivity : BaseActivity() {

    private var isRequest = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (savedInstanceState == null) intent.extras?.let {
            login.setText(it.getString(AUTHORIZATION_LOGIN_KEY))
            password.setText(it.getString(AUTHORIZATION_PASSWORD_KEY))
        }

        login_button.setOnClickListener {
            authorization()
        }

        to_registration_button.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            intent.putExtra(AUTHORIZATION_LOGIN_KEY, login.text.toString())
            intent.putExtra(AUTHORIZATION_PASSWORD_KEY, password.text.toString())
            startActivityWithTransition(intent)
        }
    }

    private fun authorization() {
        if (isRequest) return
        isRequest = true

        val service = getRetrofit().create(RequestAPI::class.java)

        val messages = service.authorization(
            Authorization(
                login.text.toString(),
                password.text.toString()
            )
        )

        messages.enqueue(object : Callback<User> {
            override fun onFailure(call: Call<User>, t: Throwable) {
                isRequest = false
                Toast.makeText(this@LoginActivity, getString(R.string.system_response_authorisation_error), Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<User>, response: Response<User>) {
                isRequest = false
                if(response.code() == 200) {
                    userData = response.body()

                    val sp = getSharedPreferences(packageName, Context.MODE_PRIVATE)
                    sp.edit()
                        .putString(USER_DATA_KEY, gson.toJson(userData))
                        .putString(USER_TOKEN_KEY, userData!!.token).apply()

                    startActivity(MainActivity::class.java)
                    finish()
                } else {
                    Toast.makeText(this@LoginActivity, getString(R.string.system_response_authorisation_incorrect), Toast.LENGTH_LONG).show()
                }
            }
        })
    }
}
