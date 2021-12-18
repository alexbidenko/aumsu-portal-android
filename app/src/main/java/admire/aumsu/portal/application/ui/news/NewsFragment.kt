package admire.aumsu.portal.application.ui.news

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_news.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import admire.aumsu.portal.application.BaseActivity
import admire.aumsu.portal.application.BaseActivity.Companion.userData
import admire.aumsu.portal.application.R
import admire.aumsu.portal.application.models.Message
import admire.aumsu.portal.application.retrofit.RequestAPI
import android.view.View.GONE
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_news.*

class NewsFragment : Fragment() {

    private lateinit var detailsViewModel: NewsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        detailsViewModel =
            ViewModelProvider(this).get(NewsViewModel::class.java)
        return inflater.inflate(R.layout.fragment_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i("Admire", "onViewCreated")

        if (detailsViewModel.isDataRequested) messagesLoad()
        else getMessages()

        if(userData!!.status == "user") {
            create_news.visibility = GONE
        } else {
            create_news.setOnClickListener {
                findNavController().navigate(R.id.to_send)
            }
        }
    }
    
    private fun messagesLoad() {
        if (view != null) {
            val adapter = NewsRecyclerAdapter()
            adapter.data = detailsViewModel.messages
            requireView().news_line.layoutManager = LinearLayoutManager(context)
            requireView().news_line.adapter = adapter
            requireView().progress.visibility = GONE
        }
    }

    private fun getMessages() {
        Log.i("Admire", "Prepare")
        val service = (activity as BaseActivity).getRetrofit().create(RequestAPI::class.java)

        val messages = service.getMessages(userData!!.token)

        messages.enqueue(object : Callback<ArrayList<Message>> {
            override fun onFailure(call: Call<ArrayList<Message>>, t: Throwable) {
                Toast.makeText(context, getString(R.string.system_response_news_error), Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<ArrayList<Message>>, response: Response<ArrayList<Message>>) {
                Log.i("Admire", "Response " + response.code())
                if(response.code() == 200) {
                    detailsViewModel.messages = response.body()!!
                    detailsViewModel.isDataRequested = true

                    messagesLoad()
                }
            }
        })
    }
}