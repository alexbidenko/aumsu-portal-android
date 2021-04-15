package admire.aumsu.portal.application.ui.send

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.dhaval2404.imagepicker.ImagePicker
import kotlinx.android.synthetic.main.fragment_send.view.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import admire.aumsu.portal.application.BaseActivity
import admire.aumsu.portal.application.MainActivity
import admire.aumsu.portal.application.R
import admire.aumsu.portal.application.models.Message
import admire.aumsu.portal.application.retrofit.RequestAPI
import java.io.File

class SendFragment : Fragment() {

    private lateinit var sendViewModel: SendViewModel
    private var image: File? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sendViewModel =
            ViewModelProvider(this).get(SendViewModel::class.java)
        return inflater.inflate(R.layout.fragment_send, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.send_button.setOnClickListener {
            if(view.title.text!!.isNotEmpty() && view.description.text!!.isNotEmpty()) sendMessage()
        }

        view.fab_add_photo.setOnClickListener {
            ImagePicker.with(this)
                .start { resultCode, data ->
                    when (resultCode) {
                        Activity.RESULT_OK -> {
                            val fileUri = data?.data
                            view.imgProfile.setImageURI(fileUri)

                            this.image = ImagePicker.getFile(data)
                        }
                        ImagePicker.RESULT_ERROR -> {
                            Toast.makeText(context, ImagePicker.getError(data), Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
        }
    }

    private fun sendMessage() {
        val service = (activity as BaseActivity).getRetrofit().create(RequestAPI::class.java)

        var filePart: MultipartBody.Part? = null
        this.image?.let {
            filePart = MultipartBody.Part.createFormData(
                "image",
                it.name,
                it.asRequestBody("image/*".toMediaType())
            )
        }

        val messages = service.sendMessage(
            BaseActivity.userData!!.token,
            requireView().title.text.toString(),
            requireView().description.text.toString(),
            filePart
        )
        messages.enqueue(object : Callback<Message> {
            override fun onFailure(call: Call<Message>, t: Throwable) {
                Toast.makeText(context, getString(R.string.system_response_send_message_error), Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<Message>,
                response: Response<Message>
            ) {
                if (response.code() == 200) {
                    this@SendFragment.onSendSuccess()
                } else {
                    Log.i("Admire", "Error: " + response.code() + " | " + response.errorBody()?.string())
                }
            }
        })
    }

    private fun onSendSuccess() {
        requireView().title.text = null
        requireView().description.text = null
        requireView().imgProfile.setImageURI(null)
        (activity as MainActivity).messageSendDialog()
    }
}