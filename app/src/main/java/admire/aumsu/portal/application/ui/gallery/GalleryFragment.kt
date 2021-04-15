package admire.aumsu.portal.application.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_gallery.view.*
import admire.aumsu.portal.application.R
import admire.aumsu.portal.application.models.GalleryPhoto


class GalleryFragment : Fragment() {

    private lateinit var galleryViewModel: GalleryViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        galleryViewModel =
            ViewModelProvider(this).get(GalleryViewModel::class.java)
        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getPhotos(view)
    }

    private fun getPhotos(view: View) {
        val photos = Gson().fromJson(data, Array<GalleryPhoto>::class.java)
        view.gallery.adapter = GalleryTable(photos, object : GalleryTable.OnChoosePhoto {
            override fun onChoose(position: Int) {
                openPhoto(position)
            }

        })
        view.gallery.layoutManager = GridLayoutManager(context, 3)

        val pagerAdapter = GalleryFragmentPagerAdapter(requireActivity().supportFragmentManager, photos)
        view.opened_gallery.adapter = pagerAdapter

        view.gallery_back.setOnClickListener {
            view.gallery_layout.visibility = View.GONE
        }
    }

    private fun openPhoto(position: Int) {
        requireView().opened_gallery.currentItem = position
        requireView().gallery_layout.visibility = View.VISIBLE
    }

    private class GalleryFragmentPagerAdapter(fm: FragmentManager, val photos: Array<GalleryPhoto>) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getItem(position: Int): Fragment {
            val fragment = PhotoFragment()
            val data = Bundle()
            data.putString(PhotoFragment.GALLERY_PHOTO_KEY, photos[position].photoUrl)
            fragment.arguments = data
            return fragment
        }

        override fun getCount(): Int {
            return photos.size
        }
    }

    companion object {
        const val data = """[{"photoUrl":"https://www.aumsu.ru/images/news/04.2021/6.jpg"},{"photoUrl":"https://www.aumsu.ru/images/news/04.2021/7.jpg"},{"photoUrl":"https://www.aumsu.ru/images/news/04.2021/8.jpg"},{"photoUrl":"https://www.aumsu.ru/images/news/04.2021/9.jpg"},{"photoUrl":"https://www.aumsu.ru/images/news/04.2021/15.jpg"},{"photoUrl":"https://www.aumsu.ru/images/news/04.2021/16.jpg"},{"photoUrl":"https://www.aumsu.ru/images/news/04.2021/17.jpg"},{"photoUrl":"https://www.aumsu.ru/images/news/04.2021/18.jpg"},{"photoUrl":"https://www.aumsu.ru/images/news/04.2021/19.jpg"},{"photoUrl":"https://www.aumsu.ru/images/news/04.2021/20.jpg"},{"photoUrl":"https://www.aumsu.ru/images/news/04.2021/20.jpg"},{"photoUrl":"https://www.aumsu.ru/images/news/04.2021/1.jpg"},{"photoUrl":"https://www.aumsu.ru/images/news/04.2021/2.jpg"}]"""
    }
}