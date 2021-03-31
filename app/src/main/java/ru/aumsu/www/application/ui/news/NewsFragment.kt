package ru.aumsu.www.application.ui.news

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_news.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.aumsu.www.application.BaseActivity
import ru.aumsu.www.application.BaseActivity.Companion.userData
import ru.aumsu.www.application.R
import ru.aumsu.www.application.models.Message
import ru.aumsu.www.application.retrofit.RequestAPI

class NewsFragment : Fragment() {

    private lateinit var newsViewModel: NewsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        newsViewModel =
            ViewModelProviders.of(this).get(NewsViewModel::class.java)
        return inflater.inflate(R.layout.fragment_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.news_line.layoutManager = LinearLayoutManager(context)
        view.news_line.adapter = NewsRecyclerAdapter()
        Log.i("Admire", "onViewCreated")

        getMessages()
    }

    private fun getMessages() {
        Log.i("Admire", "Prepare")
        if((activity as BaseActivity).hasConnection()) {
            Log.i("Admire", "hasConnection")
            val service = (activity as BaseActivity).getRetrofit().create<RequestAPI>(RequestAPI::class.java)

            val messages = service.getMessages(userData!!.token)

            messages.enqueue(object : Callback<ArrayList<Message>> {
                override fun onFailure(call: Call<ArrayList<Message>>, t: Throwable) {
                    Toast.makeText(context, "При запросе новостей произошла ошибка", Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<ArrayList<Message>>, response: Response<ArrayList<Message>>) {
                    Log.i("Admire", "Response " + response.code())
                    if(response.code() == 200) {
                        (view!!.news_line.adapter as NewsRecyclerAdapter).data = response.body()!!
                        view!!.news_line.adapter?.notifyDataSetChanged()
                    }
                }
            })
        } else {
            Toast.makeText(context, "Отсутствует интернет соединение", Toast.LENGTH_LONG).show()
        }
    }
}