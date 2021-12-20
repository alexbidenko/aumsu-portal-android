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
import admire.aumsu.portal.application.models.StudyGroup
import admire.aumsu.portal.application.models.User
import admire.aumsu.portal.application.retrofit.RequestAPI
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.isseiaoki.simplecropview.CropImageView
import com.isseiaoki.simplecropview.callback.CropCallback
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.fragment_send.view.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.isseiaoki.simplecropview.callback.LoadCallback
import java.io.*

import androidx.appcompat.app.AlertDialog
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import kotlinx.android.synthetic.main.change_password.view.*
import kotlinx.android.synthetic.main.redact_comment.view.*
import okhttp3.RequestBody
import okio.Buffer

class ProfileFragment : Fragment() {

    private lateinit var profileViewModel: ProfileViewModel
    private var isRequest = false
    private var isRequestAvatar = false
    private var image: Uri? = null
    private var studyGroups = ArrayList<StudyGroup>()

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

        login.setText(BaseActivity.userData!!.login)
        first_name.setText(BaseActivity.userData!!.firstName)
        last_name.setText(BaseActivity.userData!!.lastName)
        patronymic.setText(BaseActivity.userData!!.patronymic)
        study_group.setText(BaseActivity.userData!!.studyGroup?.name ?: "")

        change_avatar.setOnClickListener {
            ImagePicker.with(this)
                .start { resultCode, data ->
                    when (resultCode) {
                        Activity.RESULT_OK -> {
                            Log.i("Admire", "Avatar update started")
                            crop_container.visibility = VISIBLE
                            crop_image_view.setCropMode(CropImageView.CropMode.CIRCLE)
                            crop_image_view.load(data!!.data).execute(mLoadCallback)
                        }
                        ImagePicker.RESULT_ERROR -> {
                            Toast.makeText(context, ImagePicker.getError(data), Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
        }

        logout_button.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setMessage("Выйти из текущего аккаунта?")
                .setPositiveButton("Выйти") { _, _ ->
                    findNavController().navigate(R.id.nav_logout)
                }
                .setNegativeButton("Отмена") { _, _ ->
                }
                .show()
        }

        save_button.setOnClickListener {
            saveData()
        }

        crop_finish.setOnClickListener {
            crop_image_view.crop(image).execute(mCropCallback)
        }

        crop_cancel.setOnClickListener {
            crop_container.visibility = GONE
        }

        update_password.setOnClickListener {
            openChangePassword()
        }

        Log.i("Admire", "test" + (BaseActivity.userData?.studyGroup?.name ?: "test"))
        getStudyGroups()
    }

    private fun convertBitmapToFile(bitmap: Bitmap): File {
        val file = File(requireContext().cacheDir, "avatar.jpeg")
        file.createNewFile()

        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
        val bitMapData = bos.toByteArray()

        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(file)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        try {
            fos?.write(bitMapData)
            fos?.flush()
            fos?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return file
    }

    private fun saveData() {
        if (this.isRequest || first_name.text!!.length < 2 || last_name.text!!.length < 2 || login.text!!.length < 5) return

        val sp = requireActivity().getSharedPreferences(requireActivity().packageName, Context.MODE_PRIVATE)
        this.isRequest = true
        val service = (activity as BaseActivity).getRetrofit().create(RequestAPI::class.java)

        val studyGroup = studyGroups.find { it.name == study_group.text.toString() }?.id
        val messages = service.updateUser(User(
            first_name.text.toString(),
            last_name.text.toString(),
            login.text.toString(),
            "",
            "",
            "",
            "",
            patronymic.text.toString(),
            studyGroup,
            0
        ), BaseActivity.userData!!.token)
        messages.enqueue(object : Callback<User> {
            override fun onFailure(call: Call<User>, t: Throwable) {
                this@ProfileFragment.isRequest = false
                Toast.makeText(context, "При обновлении данных произошла ошибка", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<User>,
                response: Response<User>
            ) {
                if (response.code() == 200) {
                    this@ProfileFragment.isRequest = false
                    BaseActivity.userData = response.body()!!
                    sp.edit().putString(BaseActivity.USER_DATA_KEY, BaseActivity.gson.toJson(BaseActivity.userData)).apply()
                    (requireActivity() as MainActivity).updateNavHeader()
                    Toast.makeText(context, "Данные профиля успешно обновлены", Toast.LENGTH_LONG).show()
                } else {
                    Log.i("Admire", "Error: " + response.code() + " | " + response.errorBody()?.string())
                }
            }
        })
    }

    private fun savePassword(password: String, newPassword: String) {
        val sp = requireActivity().getSharedPreferences(requireActivity().packageName, Context.MODE_PRIVATE)
        val service = (activity as BaseActivity).getRetrofit().create(RequestAPI::class.java)

        val messages = service.updatePassword(password, newPassword, BaseActivity.userData!!.token)
        Log.i("Admire", "Error: " + bodyToString(messages.request().body!!))
        messages.enqueue(object : Callback<User> {
            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(context, "При обновлении пароля произошла ошибка", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<User>,
                response: Response<User>
            ) {
                if (response.code() == 200) {
                    BaseActivity.userData = response.body()!!
                    sp.edit().putString(BaseActivity.USER_DATA_KEY, BaseActivity.gson.toJson(BaseActivity.userData)).apply()
                    Toast.makeText(context, "Пароль успешно обновлен", Toast.LENGTH_LONG).show()
                } else {
                    Log.i("Admire", "Error: " + response.code() + " | " + response.errorBody()?.string())
                }
            }
        })
    }

    private fun bodyToString(request: RequestBody): String {
        return try {
            val copy: RequestBody = request
            val buffer = Buffer()
            copy.writeTo(buffer)
            buffer.readUtf8()
        } catch (e: IOException) {
            "did not work"
        }
    }

    private fun saveAvatar(image: File) {
        if (this.isRequestAvatar) return

        val sp = requireActivity().getSharedPreferences(requireActivity().packageName, Context.MODE_PRIVATE)
        progress.visibility = VISIBLE
        this.isRequestAvatar = true
        val service = (activity as BaseActivity).getRetrofit().create(RequestAPI::class.java)

        val filePart = MultipartBody.Part.createFormData(
            "avatar",
            image.name,
            image.asRequestBody("image/*".toMediaType())
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
                    sp.edit().putString(BaseActivity.USER_DATA_KEY, BaseActivity.gson.toJson(BaseActivity.userData)).apply()
                    Glide.with(requireView()).load(getString(R.string.base_url) + "/files/avatars/" + BaseActivity.userData!!.avatar).circleCrop().into(avatar)
                    (requireActivity() as MainActivity).updateNavHeader()
                    Log.i("Admire", "Avatar updated")
                } else {
                    Log.i("Admire", "Error: " + response.code() + " | " + response.errorBody()?.string())
                }
            }
        })
    }

    private fun openChangePassword() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        val dialogView: View = layoutInflater.inflate(R.layout.change_password, null)
        dialogBuilder.setTitle("Изменение пароля")
        dialogBuilder.setView(dialogView)
        dialogBuilder.setPositiveButton("Сохранить", null)
        dialogBuilder.setNegativeButton("Отменить") { dialog, _ ->
            dialog.cancel()
        }

        val alertDialog = dialogBuilder.create()
        alertDialog.setOnShowListener {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                if (dialogView.new_password.text.toString().length < 8) {
                    Toast.makeText(context, "Пароль слишком короткий", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (dialogView.new_password.text.toString() != dialogView.repeat_password.text.toString()) {
                    Toast.makeText(context, "Пароли не совпадают", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                savePassword(dialogView.old_password.text.toString(), dialogView.new_password.text.toString())
                alertDialog.cancel()
            }
        }
        alertDialog.show()
    }

    private fun getStudyGroups() {
        val service = (activity as BaseActivity).getRetrofit().create(RequestAPI::class.java)

        val messages = service.getStudyGroups()

        messages.enqueue(object : Callback<ArrayList<StudyGroup>> {
            override fun onFailure(call: Call<ArrayList<StudyGroup>>, t: Throwable) {
                Toast.makeText(context, "При запросе групп произошла ошибка", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<ArrayList<StudyGroup>>, response: Response<ArrayList<StudyGroup>>) {
                Log.i("Admire", "Response " + response.code())
                if(response.code() == 200) {
                    studyGroups = response.body()!!
                    val adapter = ArrayAdapter(
                        requireContext(), android.R.layout.simple_dropdown_item_1line, studyGroups.map { it.name }
                    )
                    requireView().study_group.setAdapter(adapter)
                    requireView().study_group.threshold = 0
                    requireView().study_group.setOnFocusChangeListener { v, _ ->
                        val search = v as MaterialAutoCompleteTextView
                        studyGroups.find { search.text.toString() == it.name }.let {
                            if (it == null) {
                                search.setText("")
                            }
                        }
                    }
                }
            }
        })
    }

    private val mLoadCallback: LoadCallback = object : LoadCallback {
        override fun onSuccess() {}
        override fun onError(e: Throwable) {}
    }

    private val mCropCallback: CropCallback = object : CropCallback {
        override fun onSuccess(cropped: Bitmap) {
            saveAvatar(convertBitmapToFile(cropped))
            crop_container.visibility = GONE
        }

        override fun onError(e: Throwable) {}
    }
}