package ru.aumsu.www.application.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_gallery.view.*
import ru.aumsu.www.application.R
import ru.aumsu.www.application.models.GalleryPhoto


class GalleryFragment : Fragment() {

    private lateinit var galleryViewModel: GalleryViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        galleryViewModel =
            ViewModelProviders.of(this).get(GalleryViewModel::class.java)
        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getPhotos(view)
    }

    private fun getPhotos(view: View) {
        val photos = Gson().fromJson<Array<GalleryPhoto>>(data, Array<GalleryPhoto>::class.java)
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

    private class GalleryFragmentPagerAdapter(fm: FragmentManager, val photos: Array<GalleryPhoto>) : FragmentPagerAdapter(fm) {

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
        const val data = """[{"photoUrl":"http://www.aumsu.ru/images/banners/thumbs/123123213123213213.JPG"},{"photoUrl":"http://www.aumsu.ru/images/banners/_DSC7014.JPG"},{"photoUrl":"http://www.aumsu.ru/images/banners/_DSC7037.JPG"},{"photoUrl":"http://www.aumsu.ru/images/banners/P7040022.JPG"},{"photoUrl":"http://www.aumsu.ru/images/banners/P7040052.JPG"},{"photoUrl":"http://www.aumsu.ru/images/banners/P1010015.JPG"},{"photoUrl":"http://www.aumsu.ru/images/banners/P1010008.JPG"},{"photoUrl":"http://www.aumsu.ru/images/photogalereay/4/PICT2906.JPG"},{"photoUrl":"http://www.aumsu.ru/images/photogalereay/09113 035.JPG"},{"photoUrl":"http://www.aumsu.ru/images/photogalereay/09113 985.JPG"},{"photoUrl":"http://www.aumsu.ru/images/photogalereay/1 сентября 030.jpg"},{"photoUrl":"http://www.aumsu.ru/images/photogalereay/DSC_5759.JPG"},{"photoUrl":"http://www.aumsu.ru/images/photogalereay/DSC_5783.JPG"},{"photoUrl":"http://www.aumsu.ru/images/photogalereay/7 марта147.JPG"},{"photoUrl":"http://www.aumsu.ru/images/photogalereay/DSC_5776.JPG"},{"photoUrl":"http://www.aumsu.ru/images/photogalereay/DSC_5820.JPG"},{"photoUrl":"http://www.aumsu.ru/images/photogalereay/DSC_5825.JPG"},{"photoUrl":"http://www.aumsu.ru/images/photogalereay/DSC_5854.JPG"},{"photoUrl":"http://www.aumsu.ru/images/photogalereay/DSC_5916.JPG"},{"photoUrl":"http://www.aumsu.ru/images/photogalereay/DSC_5980.JPG"},{"photoUrl":"http://www.aumsu.ru/images/photogalereay/2/IMG_1261.JPG"},{"photoUrl":"http://www.aumsu.ru/images/photogalereay/2/IMG_1675.JPG"},{"photoUrl":"http://www.aumsu.ru/images/photogalereay/2/IMG_1850.JPG"},{"photoUrl":"http://www.aumsu.ru/images/photogalereay/2/IMG_3964.JPG"},{"photoUrl":"http://www.aumsu.ru/images/photogalereay/2/IMG_4020.JPG"},{"photoUrl":"http://www.aumsu.ru/images/photogalereay/2/IMG_4416.jpg"},{"photoUrl":"http://www.aumsu.ru/images/photogalereay/2/IMG_4578.jpg"},{"photoUrl":"http://www.aumsu.ru/images/photogalereay/2/IMG_0109.jpg"},{"photoUrl":"http://www.aumsu.ru/images/photogalereay/2/IMG_5161.JPG"},{"photoUrl":"http://www.aumsu.ru/images/photogalereay/2/IMG_6776.JPG"},{"photoUrl":"http://www.aumsu.ru/images/photogalereay/2/IMG_6980.JPG"},{"photoUrl":"http://www.aumsu.ru/images/photogalereay/2/IMG_7450.JPG"},{"photoUrl":"http://www.aumsu.ru/images/photogalereay/2/IMG_6141.JPG"},{"photoUrl":"http://www.aumsu.ru/images/photogalereay/3/IMG_7786.JPG"},{"photoUrl":"http://www.aumsu.ru/images/photogalereay/3/IMG_9101.JPG"},{"photoUrl":"http://www.aumsu.ru/images/photogalereay/3/IMG_9206.JPG"},{"photoUrl":"http://www.aumsu.ru/images/photogalereay/3/IMG_9679.jpg"},{"photoUrl":"http://www.aumsu.ru/images/photogalereay/3/PB085145.jpg"},{"photoUrl":"http://www.aumsu.ru/images/photogalereay/3/PB085169.jpg"},{"photoUrl":"http://www.aumsu.ru/images/photogalereay/3/PB085192.jpg"},{"photoUrl":"http://www.aumsu.ru/images/photogalereay/3/PB085202.jpg"},{"photoUrl":"http://www.aumsu.ru/images/photogalereay/4/Панарама БУТЦ 4.jpg"},{"photoUrl":"http://www.aumsu.ru/images/photogalereay/3/PB085218.jpg"},{"photoUrl":"http://www.aumsu.ru/images/photogalereay/3/PB095233.JPG"},{"photoUrl":"http://www.aumsu.ru/images/photogalereay/3/PB095248.JPG"},{"photoUrl":"http://www.aumsu.ru/images/photogalereay/3/PB095263.JPG"},{"photoUrl":"http://www.aumsu.ru/images/photogalereay/3/PB135294.JPG"},{"photoUrl":"http://www.aumsu.ru/images/photogalereay/4/PB145533.JPG"},{"photoUrl":"http://www.aumsu.ru/images/photogalereay/4/vmf-4.JPG"},{"photoUrl":"http://www.aumsu.ru/images/photogalereay/2/IMG_2232.JPG"},{"photoUrl":"http://www.aumsu.ru/images/photogalereay/2/IMG_2013.JPG"},{"photoUrl":"http://www.aumsu.ru/images/photogalereay/4/НШ249.JPG"},{"photoUrl":"http://www.aumsu.ru/images/photogalereay/4/НШ257.JPG"},{"photoUrl":"http://www.aumsu.ru/images/photogalereay/4/НШ345.JPG"},{"photoUrl":"http://www.aumsu.ru/images/photogalereay/4/НШ428.JPG"},{"photoUrl":"http://www.aumsu.ru/images/photogalereay/5/mk7.JPG"},{"photoUrl":"http://www.aumsu.ru/images/photogalereay/5/mk8.JPG"},{"photoUrl":"http://www.aumsu.ru/images/photogalereay/5/mk14.JPG"},{"photoUrl":"http://www.aumsu.ru/images/photogalereay/5/mk19.JPG"},{"photoUrl":"http://www.aumsu.ru/images/photogalereay/5/mk21.JPG"},{"photoUrl":"http://www.aumsu.ru/images/photogalereay/5/mk22.JPG"},{"photoUrl":"http://www.aumsu.ru/images/photogalereay/5/mk23.JPG"},{"photoUrl":"http://www.aumsu.ru/images/photogalereay/5/mk25.JPG"},{"photoUrl":"http://www.aumsu.ru/images/photogalereay/5/mk32.JPG"},{"photoUrl":"http://www.aumsu.ru/images/photogalereay/5/mk33.JPG"},{"photoUrl":"http://www.aumsu.ru/images/photogalereay/5/mk35.JPG"},{"photoUrl":"http://www.aumsu.ru/images/photogalereay/5/mk39.JPG"},{"photoUrl":"http://www.aumsu.ru/images/photogalereay/5/mk43.JPG"}]"""
    }
}