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
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import okio.Buffer

class DetailsFragment : Fragment() {

    private lateinit var detailsViewModel: DetailsViewModel

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
                    view!!.title.text = response.body()!!.title.removeSurrounding("\"")
                    view!!.content.text = response.body()!!.description.removeSurrounding("\"")
                    if (response.body()!!.image == "") {
                        view!!.title.setTextColor(ContextCompat.getColor(context!!, R.color.black))
                        view!!.photo.visibility = View.GONE
                    } else {
                        Glide.with(context!!).load(context!!.getString(R.string.base_url) + "/files/messages/images/" + response.body()!!.image).into(view!!.photo)
                    }
                    view!!.comments.layoutManager = LinearLayoutManager(context)
                    view!!.comments.adapter = DetailsRecyclerAdapter(response.body()!!.comments ?: ArrayList())
                }
            }
        })
    }

    private fun sendComment() {
        if (requireView().comment.text.toString().trim() == "") return;

        Log.i("Admire", "Prepare")
        val service = (activity as BaseActivity).getRetrofit().create(RequestAPI::class.java)

        val messages = service.sendComment(
            Comment(0, userData!!.id!!, requireArguments().getInt("messageId"), requireView().comment.text.toString().trim(), null),
            userData!!.token
        )

        messages.enqueue(object : Callback<Comment> {
            override fun onFailure(call: Call<Comment>, t: Throwable) {
                Toast.makeText(context, getString(R.string.system_response_news_error), Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<Comment>, response: Response<Comment>) {
                Log.i("Admire", "Response " + response.code())
                if(response.code() == 200) {
                    val comment = response.body()!!
                    comment.user = userData!!
                    (view!!.comments.adapter as DetailsRecyclerAdapter).data.add(comment)
                    view!!.comments.adapter!!.notifyDataSetChanged()
                    requireView().comment.setText("")
                }
            }
        })
    }
}