package admire.aumsu.portal.application.ui.gallery

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager
import com.jsibbold.zoomage.ZoomageView

class HorizontalViewPager(context: Context, attrs: AttributeSet) : ViewPager(context, attrs) {

    private var touchListener: OnTouchListener? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        val view = ((adapter as GalleryFragmentPagerAdapter).activeView as ZoomageView?)
        if (view?.currentScaleFactor != 1F) {
            view?.onTouchEvent(ev)
            currentItem = currentItem
            if (ev?.action == MotionEvent.ACTION_UP) {
                touchListener?.onTouch(this, ev)
                super.onTouchEvent(ev)
            }
            return true
        }
        if (view.currentScaleFactor < 1.3F && ev?.action == MotionEvent.ACTION_UP) view.reset()

        touchListener?.onTouch(this, ev)

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