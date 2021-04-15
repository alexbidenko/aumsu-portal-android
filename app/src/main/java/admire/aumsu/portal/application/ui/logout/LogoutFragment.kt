package admire.aumsu.portal.application.ui.logout

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import admire.aumsu.portal.application.BaseActivity
import admire.aumsu.portal.application.LoginActivity

class LogoutFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sp = requireContext().getSharedPreferences(requireContext().packageName, Context.MODE_PRIVATE)
        sp.edit().remove(BaseActivity.USER_DATA_KEY).remove(BaseActivity.USER_TOKEN_KEY).apply()

        (activity as BaseActivity).startActivity(LoginActivity::class.java)
        (activity as BaseActivity).finish()
    }
}
