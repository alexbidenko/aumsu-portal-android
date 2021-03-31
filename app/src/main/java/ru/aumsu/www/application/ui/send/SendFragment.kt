package ru.aumsu.www.application.ui.send

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_send.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.aumsu.www.application.BaseActivity
import ru.aumsu.www.application.MainActivity
import ru.aumsu.www.application.R
import ru.aumsu.www.application.models.Message
import ru.aumsu.www.application.retrofit.RequestAPI

class SendFragment : Fragment() {

    private lateinit var sendViewModel: SendViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sendViewModel =
            ViewModelProviders.of(this).get(SendViewModel::class.java)
        return inflater.inflate(R.layout.fragment_send, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.send_button.setOnClickListener {
            if(view.message.text!!.isNotEmpty())
                sendMessage(Runnable {
                    view.message.text = null
                    (activity as MainActivity).messageSendDialog()
                })
        }
    }

    private fun sendMessage(r: Runnable) {
        if((activity as BaseActivity).hasConnection()) {
            val service = (activity as BaseActivity).getRetrofit().create<RequestAPI>(RequestAPI::class.java)

            val messages = service.sendMessage(BaseActivity.userData!!.token, Message(
                BaseActivity.userData!!.id!!,
                requireView().message.text.toString(),
                null
            ))

            messages.enqueue(object : Callback<Message> {
                override fun onFailure(call: Call<Message>, t: Throwable) {}

                override fun onResponse(
                    call: Call<Message>,
                    response: Response<Message>
                ) {
                    if (response.code() == 200) {
                        Handler().post(r)
                    }
                }
            })
        } else {
            Toast.makeText(context, "Отсутствует интернет соединение", Toast.LENGTH_LONG).show()
        }
    }
}