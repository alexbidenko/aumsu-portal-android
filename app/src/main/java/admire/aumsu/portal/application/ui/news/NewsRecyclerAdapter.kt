package admire.aumsu.portal.application.ui.news

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.news_card.view.*
import admire.aumsu.portal.application.R
import admire.aumsu.portal.application.models.Message
import androidx.core.content.ContextCompat.getColor

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

        private val title = itemView.title
        private val photo = itemView.photo
        private val content = itemView.content

        fun bind(data: Message) {
            title.text = data.title.removeSurrounding("\"")
            if (data.image == "") {
                title.setTextColor(getColor(itemView.context, R.color.black))
                photo.visibility = View.GONE
            } else {
                Glide.with(photo).load(itemView.context.getString(R.string.base_url) + "/files/messages/images/" + data.image).into(photo)
            }
            content.text = data.description.removeSurrounding("\"")

//            itemView.setOnClickListener {
//                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(data.url))
//                startActivity(itemView.context, browserIntent, null)
//            }
        }
    }
}