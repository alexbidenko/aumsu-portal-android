package admire.aumsu.portal.application.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import admire.aumsu.portal.application.BaseActivity
import admire.aumsu.portal.application.MainActivity
import admire.aumsu.portal.application.R
import admire.aumsu.portal.application.models.User
import admire.aumsu.portal.application.retrofit.RequestAPI
import android.app.Activity
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.fragment_send.view.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ProfileFragment : Fragment() {

    private lateinit var profileViewModel: ProfileViewModel
    private var isRequest = false
    private var isRequestAvatar = false
    private var image: File? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (BaseActivity.userData!!.avatar != "")
            Glide.with(this).load(getString(R.string.base_url) + "/files/avatars/" + BaseActivity.userData!!.avatar).circleCrop().into(avatar)

        first_name.setText(BaseActivity.userData!!.firstName)
        last_name.setText(BaseActivity.userData!!.lastName)
        patronymic.setText(BaseActivity.userData!!.patronymic)

        change_avatar.setOnClickListener {
            ImagePicker.with(this)
                .start { resultCode, data ->
                    when (resultCode) {
                        Activity.RESULT_OK -> {
                            Log.i("Admire", "Avatar update started")
                            this.image = ImagePicker.getFile(data)
                            saveAvatar()
                        }
                        ImagePicker.RESULT_ERROR -> {
                            Toast.makeText(context, ImagePicker.getError(data), Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
        }

        save_button.setOnClickListener {
            saveData()
        }
    }

    private fun saveData() {
        if (this.isRequest || first_name.text!!.length < 2 || last_name.text!!.length < 2) return

        this.isRequest = true
        val service = (activity as BaseActivity).getRetrofit().create(RequestAPI::class.java)

        val messages = service.updateUser(User(
            first_name.text.toString(),
            last_name.text.toString(),
            "",
            "",
            "",
            "",
            "",
            patronymic.text.toString(),
            0
        ), BaseActivity.userData!!.token)
        messages.enqueue(object : Callback<User> {
            override fun onFailure(call: Call<User>, t: Throwable) {
                this@ProfileFragment.isRequest = false
                Toast.makeText(context, getString(R.string.system_response_send_message_error), Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<User>,
                response: Response<User>
            ) {
                if (response.code() == 200) {
                    this@ProfileFragment.isRequest = false
                    BaseActivity.userData = response.body()!!
                    (requireActivity() as MainActivity).updateNavHeader()
                    Toast.makeText(context, "Данные профиля успешно обновлены", Toast.LENGTH_LONG).show()
                } else {
                    Log.i("Admire", "Error: " + response.code() + " | " + response.errorBody()?.string())
                }
            }
        })
    }

    private fun saveAvatar() {
        if (this.isRequestAvatar || this.image == null) return

        progress.visibility = VISIBLE
        this.isRequestAvatar = true
        val service = (activity as BaseActivity).getRetrofit().create(RequestAPI::class.java)

        val filePart = MultipartBody.Part.createFormData(
            "avatar",
            this.image!!.name,
            this.image!!.asRequestBody("image/*".toMediaType())
        )

        val messages = service.updateAvatar(filePart, BaseActivity.userData!!.token)
        messages.enqueue(object : Callback<User> {
            override fun onFailure(call: Call<User>, t: Throwable) {
                this@ProfileFragment.isRequest = false
                Toast.makeText(context, getString(R.string.system_response_send_message_error), Toast.LENGTH_LONG).show()
                progress.visibility = GONE
            }

            override fun onResponse(
                call: Call<User>,
                response: Response<User>
            ) {
                progress.visibility = GONE
                this@ProfileFragment.isRequestAvatar = false
                if (response.code() == 200) {
                    BaseActivity.userData = response.body()!!
                    Glide.with(requireView()).load(getString(R.string.base_url) + "/files/avatars/" + BaseActivity.userData!!.avatar).circleCrop().into(requireView().avatar)
                    (requireActivity() as MainActivity).updateNavHeader()
                    Log.i("Admire", "Avatar updated")
                } else {
                    Log.i("Admire", "Error: " + response.code() + " | " + response.errorBody()?.string())
                }
            }
        })
    }
}