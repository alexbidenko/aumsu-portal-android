package admire.aumsu.portal.application.ui.details

import admire.aumsu.portal.application.BaseActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import admire.aumsu.portal.application.R
import admire.aumsu.portal.application.models.Comment
import android.content.res.Resources
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.comment_card.view.*
import kotlinx.android.synthetic.main.comment_card.view.avatar
import kotlinx.android.synthetic.main.fragment_profile.view.*

class DetailsRecyclerAdapter(
    val data: ArrayList<Comment>
) : RecyclerView.Adapter<DetailsRecyclerAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.comment_card, parent, false))
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(data[position])
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(data: Comment) {
            itemView.user.text = "${data.user!!.firstName} ${data.user!!.lastName}".trim()
            itemView.content.text = data.content
            Glide.with(itemView).load(itemView.context.getString(R.string.base_url) + "/files/avatars/" + data.user!!.avatar).circleCrop().into(itemView.avatar)
        }
    }
}