package ru.aumsu.www.application.ui.news

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.news_card.view.*
import ru.aumsu.www.application.R
import ru.aumsu.www.application.models.Message

class NewsRecyclerAdapter : RecyclerView.Adapter<NewsRecyclerAdapter.Holder>() {

    var data: ArrayList<Message> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.news_card, parent, false))
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(data[position])
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {

//        private val title = itemView.title
//        private val photo = itemView.photo
        private val content = itemView.content

        fun bind(data: Message) {
//            title.text = data.title
//            Glide.with(photo).load(data.image).into(photo)
            content.text = data.message

//            itemView.setOnClickListener {
//                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(data.url))
//                startActivity(itemView.context, browserIntent, null)
//            }
        }
    }
}