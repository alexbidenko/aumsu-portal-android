package admire.aumsu.portal.application.ui.gallery

import admire.aumsu.portal.application.models.GalleryPhoto
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.jsibbold.zoomage.ZoomageView

class GalleryFragmentPagerAdapter(fm: FragmentManager, private val photos: Array<GalleryPhoto>) : FragmentStatePagerAdapter(fm) {

    var activeView: View? = null
    lateinit var containerView: View

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

    override fun setPrimaryItem(container: ViewGroup, position: Int, v: Any) {
        if (activeView != (v as Fragment).view) (activeView as ZoomageView?)?.reset()
        activeView = v.view
    }
}
