package admire.aumsu.portal.application.ui.details

import admire.aumsu.portal.application.BaseActivity
import admire.aumsu.portal.application.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import admire.aumsu.portal.application.models.Comment
import admire.aumsu.portal.application.retrofit.RequestAPI
import android.widget.Toast
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.comment_card.view.*
import kotlinx.android.synthetic.main.comment_card.view.avatar

import android.util.Log

import androidx.appcompat.app.AlertDialog
import android.util.TypedValue
import kotlinx.android.synthetic.main.comment_card.view.content
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import android.widget.EditText
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.redact_comment.view.*

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

        fun bind(item: Comment) {
            itemView.user.text = "${item.user!!.firstName} ${item.user!!.lastName}".trim()
            itemView.content.text = item.content
            Glide.with(itemView).load(itemView.context.getString(R.string.base_url) + "/files/avatars/" + item.user!!.avatar).circleCrop().into(itemView.avatar)

            if (item.user!!.id == BaseActivity.userData!!.id || BaseActivity.userData!!.status == "admin") {
                val outValue = TypedValue()
                itemView.context.theme
                    .resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
                itemView.setBackgroundResource(outValue.resourceId)
                itemView.setOnLongClickListener {
                    val items = arrayOf<CharSequence>("Редактировать", "Удалить")

                    val builder: AlertDialog.Builder = AlertDialog.Builder(itemView.context)

                    builder.setItems(
                        items
                    ) { _, i ->
                        when (i) {
                            0 -> {
                                editComment(itemView, item)
                            }
                            1 -> {
                                AlertDialog.Builder(itemView.context)
                                    .setMessage("Удалить комментарий?")
                                    .setPositiveButton("Подтвердить") { _, _ ->
                                        deleteComment(itemView, item)
                                    }
                                    .setNegativeButton("Отмена") { _, _ ->
                                    }
                                    .show()
                            }
                        }
                    }

                    val alert: AlertDialog = builder.create()

                    alert.show()

                    true
                }
            }
        }
    }

    private fun editComment(view: View, item: Comment) {
        val alert = AlertDialog.Builder(view.context)

        val layout = (view.context as BaseActivity).getLayoutInflater().inflate(R.layout.redact_comment, null)
        layout.edit_comment.setText(item.content)

        alert.setTitle("Редактирование комментария")
        alert.setView(layout)

        alert.setPositiveButton("Сохранить", null)
        alert.setNegativeButton("Отменить"
        ) { dialog, _ ->
            dialog.cancel()
        }
        val dialog = alert.create()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val text = layout.edit_comment.text.toString()

                if (text != "") {
                    item.content = layout.edit_comment.text.toString()
                    saveComment(item, view)
                    dialog.dismiss()
                }
            }
        }

        dialog.show()
    }

    private fun deleteComment(view: View, item: Comment) {
        Log.i("Admire", "Prepare")
        val service = (view.context as BaseActivity).getRetrofit().create(RequestAPI::class.java)

        val messages = service.deleteComment(
            item.id,
            BaseActivity.userData!!.token
        )

        messages.enqueue(object : Callback<Boolean> {
            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                Toast.makeText(view.context, "При удалении комментария произошла ошибка", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                Log.i("Admire", "Response " + response.code())
                if(response.code() == 200) {
                    val index = data.indexOf(item)
                    data.remove(item)
                    notifyItemRemoved(index)
                }
            }
        })
    }

    private fun saveComment(item: Comment, view: View) {
        Log.i("Admire", "Prepare")
        val service = (view.context as BaseActivity).getRetrofit().create(RequestAPI::class.java)

        val messages = service.updateComment(
            item.id,
            item,
            BaseActivity.userData!!.token
        )

        messages.enqueue(object : Callback<Comment> {
            override fun onFailure(call: Call<Comment>, t: Throwable) {
                Toast.makeText(view.context, "При обновлении комментария произошла ошибка", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<Comment>, response: Response<Comment>) {
                Log.i("Admire", "Response " + response.code())
                if(response.code() == 200) {
                    val index = data.indexOf(item)
                    data[index] = item
                    notifyItemChanged(index)
                }
            }
        })
    }
}