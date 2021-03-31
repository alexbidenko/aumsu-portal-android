package ru.aumsu.www.application.ui.gallery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.gallery_photo.view.*
import ru.aumsu.www.application.R
import ru.aumsu.www.application.models.GalleryPhoto

class GalleryTable(private val photos: Array<GalleryPhoto>, private val onChooseListener: OnChoosePhoto) : RecyclerView.Adapter<GalleryTable.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.gallery_photo, parent, false))
    }

    override fun getItemCount(): Int {
        return photos.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(photos[position], position)
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(data: GalleryPhoto, position: Int) {
            Glide.with(itemView).load(data.photoUrl).into(itemView.photo)
            itemView.photo.setOnClickListener {
                onChooseListener.onChoose(position)
            }
        }
    }

    interface OnChoosePhoto {
        fun onChoose(position: Int)
    }
}