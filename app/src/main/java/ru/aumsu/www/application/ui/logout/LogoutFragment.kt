package ru.aumsu.www.application.ui.logout

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ru.aumsu.www.application.BaseActivity
import ru.aumsu.www.application.LoginActivity
import ru.aumsu.www.application.R

class LogoutFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sp = requireContext().getSharedPreferences(requireContext().packageName, Context.MODE_PRIVATE)
        sp.edit().remove(BaseActivity.USER_DATA_KEY).remove(BaseActivity.USER_TOKEN_KEY).apply()

        (activity as BaseActivity).startActivity(LoginActivity::class.java)
        (activity as BaseActivity).finish()
    }
}
