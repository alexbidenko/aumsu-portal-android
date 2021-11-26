package admire.aumsu.portal.application.ui.gallery

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_gallery.view.*
import admire.aumsu.portal.application.R
import admire.aumsu.portal.application.models.GalleryPhoto
import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.graphics.Matrix
import android.util.Log
import android.view.*
import android.view.View.GONE
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import com.jsibbold.zoomage.ZoomageView
import kotlin.math.abs

import kotlin.math.min

class GalleryFragment : Fragment() {

    private lateinit var galleryViewModel: GalleryViewModel

    private var previousFingerPosition = 0
    private var baseLayoutPosition = 0F
    private var defaultViewHeight = 0

    private var isClosing = false
    private var isScrollingUp = false
    private var isScrollingDown = false

    private var x1 = 0f
    private var x2 = 0f
    private var y1 = 0f
    private var y2 = 0f
    private var dx = 0f
    private var dy = 0f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        galleryViewModel =
            ViewModelProvider(this).get(GalleryViewModel::class.java)
        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getPhotos(view)

        view.opened_gallery.addAdditionTouchListener { _, event ->
            val y = event.rawY.toInt()

            val currentChild = (view.opened_gallery.adapter as GalleryFragmentPagerAdapter).activeView as ZoomageView?

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    x1 = event.x
                    y1 = event.y

                    defaultViewHeight = view.gallery_layout.height

                    previousFingerPosition = y
                    baseLayoutPosition = view.gallery_layout.y
                    currentChild?.onTouchEvent(event)
                }
                MotionEvent.ACTION_UP -> {
                    view.gallery_layout.y = 0F
                    isScrollingUp = false
                    isScrollingDown = false
                    currentChild?.onTouchEvent(event)
                }
                MotionEvent.ACTION_MOVE -> if (!isClosing) {
                    x2 = event.x
                    y2 = event.y
                    dx = x1 - x2
                    dy = y1 - y2
                    x1 = event.x
                    y1 = event.y

                    if (abs(dx) > abs(dy)) {
                        previousFingerPosition = y
                        currentChild?.onTouchEvent(event)
                        return@addAdditionTouchListener false
                    }

                    val currentYPosition = view.gallery_layout.y

                    if (previousFingerPosition > y) {
                        if (!isScrollingUp) {
                            isScrollingUp = true
                        }

                        view.gallery_layout.y = view.gallery_layout.y + (y - previousFingerPosition)

                        if (baseLayoutPosition - currentYPosition > defaultViewHeight / 12) {
                            closeUpAndDismissDialog(currentYPosition)
                            return@addAdditionTouchListener true
                        }
                    } else if (isScrollingUp) {

                        if (!isScrollingDown) {
                            isScrollingDown = true
                        }

                        view.gallery_layout.y = min(baseLayoutPosition, view.gallery_layout.y + (y - previousFingerPosition))
                        view.gallery_layout.requestLayout()
                    }

                    previousFingerPosition = y
                    return@addAdditionTouchListener true
                }
            }
            false
        }
    }


    private fun closeUpAndDismissDialog(currentPosition: Float) {
        isClosing = true
        val positionAnimator: ObjectAnimator =
            ObjectAnimator.ofFloat(requireView().gallery_layout, "y", currentPosition, -requireView().gallery_layout.height.toFloat())
        positionAnimator.duration = 300
        positionAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationEnd(animator: Animator?) {
                requireView().gallery_layout.visibility = GONE
                requireView().gallery_layout.y = baseLayoutPosition
            }

            override fun onAnimationStart(animation: Animator?) {}

            override fun onAnimationCancel(animation: Animator?) {}

            override fun onAnimationRepeat(animation: Animator?) {}
        })
        positionAnimator.start()
    }

    private fun getPhotos(view: View) {
        val photos = Gson().fromJson(data, Array<GalleryPhoto>::class.java)
        view.gallery.adapter = GalleryTable(photos, object : GalleryTable.OnChoosePhoto {
            override fun onChoose(position: Int) {
                Log.i("Admire", "Open image: $position")
                openPhoto(position)
            }

        })
        view.gallery.layoutManager = GridLayoutManager(context, 3)

        val pagerAdapter = GalleryFragmentPagerAdapter(requireActivity().supportFragmentManager, photos)
        view.opened_gallery.adapter = pagerAdapter

        view.gallery_back.setOnClickListener {
            view.gallery_layout.visibility = GONE
        }
    }

    private fun openPhoto(position: Int) {
        isClosing = false
        requireView().opened_gallery.currentItem = position
        requireView().gallery_layout.visibility = View.VISIBLE
    }

    companion object {
        const val data = """[{"photoUrl":"https://www.aumsu.ru/images/news/04.2021/6.jpg"},{"photoUrl":"https://www.aumsu.ru/images/news/04.2021/7.jpg"},{"photoUrl":"https://www.aumsu.ru/images/news/04.2021/8.jpg"},{"photoUrl":"https://www.aumsu.ru/images/news/04.2021/9.jpg"},{"photoUrl":"https://www.aumsu.ru/images/news/04.2021/15.jpg"},{"photoUrl":"https://www.aumsu.ru/images/news/04.2021/16.jpg"},{"photoUrl":"https://www.aumsu.ru/images/news/04.2021/17.jpg"},{"photoUrl":"https://www.aumsu.ru/images/news/04.2021/18.jpg"},{"photoUrl":"https://www.aumsu.ru/images/news/04.2021/19.jpg"},{"photoUrl":"https://www.aumsu.ru/images/news/04.2021/20.jpg"},{"photoUrl":"https://www.aumsu.ru/images/news/04.2021/20.jpg"},{"photoUrl":"https://www.aumsu.ru/images/news/04.2021/1.jpg"},{"photoUrl":"https://www.aumsu.ru/images/news/04.2021/2.jpg"}]"""
    }
}
