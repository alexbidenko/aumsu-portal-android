package admire.aumsu.portal.application

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_registration.*
import admire.aumsu.portal.application.models.User
import android.widget.Toast
import android.content.Context
import admire.aumsu.portal.application.retrofit.RequestAPI
import android.content.Intent
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_registration.login
import kotlinx.android.synthetic.main.activity_registration.password
import kotlinx.android.synthetic.main.activity_registration.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistrationActivity : BaseActivity() {

    private var isRequest = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        if (savedInstanceState == null) intent.extras?.let {
            login.setText(it.getString(AUTHORIZATION_LOGIN_KEY))
            password.setText(it.getString(AUTHORIZATION_PASSWORD_KEY))
        }

        registration_button.setOnClickListener {
            registration()
        }

        to_login_button.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra(AUTHORIZATION_LOGIN_KEY, login.text.toString())
            intent.putExtra(AUTHORIZATION_PASSWORD_KEY, password.text.toString())
            startActivityWithTransition(intent)
        }
    }

    private fun registration() {
        val service = getRetrofit().create(RequestAPI::class.java)

        if (login.text.toString().length < 5) {
            Toast.makeText(
                this,
                getString(R.string.activity_registration_error_login_short),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (password.text.toString().length < 8) {
            Toast.makeText(
                this,
                getString(R.string.activity_registration_error_password_short),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (password.text.toString() != repeat_password.text.toString()) {
            Toast.makeText(
                this,
                getString(R.string.activity_registration_error_password),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (isRequest) return
        isRequest = true

        val messages = service.registration(
            User(
                first_name.text.toString(),
                last_name.text.toString(),
                login.text.toString(),
                password.text.toString(),
                login.text.toString(),
                "",
                "",
                patronymic.text.toString(),
                null,
                0
            )
        )

        messages.enqueue(object : Callback<User> {
            override fun onFailure(call: Call<User>, t: Throwable) {
                isRequest = false
                Toast.makeText(this@RegistrationActivity, getString(R.string.system_response_authorisation_error), Toast.LENGTH_LONG).show()
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
                    Toast.makeText(this@RegistrationActivity, getString(R.string.activity_registration_error_data), Toast.LENGTH_LONG).show()
                }
            }
        })
    }
}
