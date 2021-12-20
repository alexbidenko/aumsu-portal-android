package admire.aumsu.portal.application.ui.gallery

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager
import com.jsibbold.zoomage.ZoomageView

class HorizontalViewPager(context: Context, attrs: AttributeSet) : ViewPager(context, attrs) {

    var isDragged = false

    init {
        addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                isDragged = positionOffset != 0f
            }

            override fun onPageSelected(position: Int) {}

            override fun onPageScrollStateChanged(state: Int) {}

        })
    }

    private var touchListener: OnTouchListener? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        val galleryAdapter = adapter as GalleryFragmentPagerAdapter
        val view = galleryAdapter.activeView as ZoomageView?
        val values = FloatArray(9)
        view?.imageMatrix?.getValues(values)
        if (view != null && !(values[0] == 1f && values[4] == 1f && values[8] == 1f)) {
            view.onTouchEvent(ev)
            currentItem = currentItem
            if (ev?.action == MotionEvent.ACTION_UP) {
                touchListener?.onTouch(this, ev)
                super.onTouchEvent(ev)
            }
            return true
        }
        if (view != null && view.currentScaleFactor < 1.3F && ev?.action == MotionEvent.ACTION_UP) view.reset()

        if (galleryAdapter.containerView.y != 0F) {
            touchListener?.onTouch(this, ev)
            return false
        }

        if (!isDragged) touchListener?.onTouch(this, ev)
        try {
            return super.onTouchEvent(ev)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return true
    }

    fun addAdditionTouchListener(listener: OnTouchListener) {
        touchListener = listener
    }
}