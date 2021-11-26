package admire.aumsu.portal.application.ui.details

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_details.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import admire.aumsu.portal.application.BaseActivity
import admire.aumsu.portal.application.BaseActivity.Companion.userData
import admire.aumsu.portal.application.R
import admire.aumsu.portal.application.models.Comment
import admire.aumsu.portal.application.models.Message
import admire.aumsu.portal.application.retrofit.RequestAPI
import android.graphics.Color
import android.text.method.LinkMovementMethod
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide

class DetailsFragment : Fragment() {

    private lateinit var detailsViewModel: DetailsViewModel
    private var isRequest = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        detailsViewModel =
            ViewModelProvider(this).get(DetailsViewModel::class.java)
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("Admire", "onViewCreated")

        getMessage()

        view.send.setOnClickListener {
            sendComment()
        }

        view.swipe_refresh.setColorSchemeColors(Color.BLUE)
        view.swipe_refresh.setOnRefreshListener {
            getMessage()
        }
    }

    private fun getMessage() {
        Log.i("Admire", "Prepare")
        val service = (activity as BaseActivity).getRetrofit().create(RequestAPI::class.java)

        val messages = service.getMessageById(requireArguments().getInt("messageId"), userData!!.token)

        messages.enqueue(object : Callback<Message> {
            override fun onFailure(call: Call<Message>, t: Throwable) {
                Toast.makeText(context, getString(R.string.system_response_news_error), Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<Message>, response: Response<Message>) {
                Log.i("Admire", "Response " + response.code())
                if(response.code() == 200) {
                    requireView().title.text = response.body()!!.title
                    requireView().content.text = response.body()!!.description
                    if (response.body()!!.image == "") {
                        requireView().title.setTextColor(ContextCompat.getColor(context!!, R.color.black))
                        requireView().photo.visibility = View.GONE
                    } else {
                        Glide.with(context!!).load(context!!.getString(R.string.base_url) + "/files/messages/images/" + response.body()!!.image).into(requireView().photo)
                    }
                    requireView().comments.layoutManager = LinearLayoutManager(context)
                    requireView().comments.adapter = DetailsRecyclerAdapter(response.body()!!.comments ?: ArrayList())

                    requireView().container.visibility = VISIBLE;
                    requireView().progress.visibility = GONE;
                    requireView().swipe_refresh.isRefreshing = false
                }
            }
        })
    }

    private fun sendComment() {
        if (isRequest || requireView().comment.text.toString().trim() == "") return;
        if (!checkCorrectMessage()) return;
        isRequest = true

        Log.i("Admire", "Prepare")
        val service = (activity as BaseActivity).getRetrofit().create(RequestAPI::class.java)

        val messages = service.sendComment(
            Comment(0, userData!!.id!!, requireArguments().getInt("messageId"), requireView().comment.text.toString().trim(), null),
            userData!!.token
        )

        messages.enqueue(object : Callback<Comment> {
            override fun onFailure(call: Call<Comment>, t: Throwable) {
                Toast.makeText(context, getString(R.string.system_response_news_error), Toast.LENGTH_LONG).show()
                isRequest = false
            }

            override fun onResponse(call: Call<Comment>, response: Response<Comment>) {
                Log.i("Admire", "Response " + response.code())
                isRequest = false
                if(response.code() == 200) {
                    val comment = response.body()!!
                    comment.user = userData!!
                    val data = (requireView().comments.adapter as DetailsRecyclerAdapter).data
                    data.add(comment)
                    requireView().comments.adapter!!.notifyItemInserted(data.size)
                    requireView().comment.setText("")
                }
            }
        })
    }

    private fun checkCorrectMessage(): Boolean {
        val words = arrayOf(
            "блять",
            "блядь",
            "хуйня",
            "пиздец",
            "пизда"
        )
        val result = !words.any {
            requireView().comment.text.toString().lowercase().contains(it)
        }
        if (!result)
            Toast.makeText(context, "В приложении запрещено использование нецензурной лексики", Toast.LENGTH_LONG).show()
        return result
    }
}