package com.common.customer

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.text.Html
import android.view.View
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.Request
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.common.R
import com.common.util.Util
import java.util.*


class GlideImageGetter(private val mContext: Context, private val mTextView: TextView) :
    Html.ImageGetter, Drawable.Callback {

    private val mTargets: MutableSet<ImageGetterViewTarget>

    override fun getDrawable(url: String): Drawable {

        val dp16 = Util.dp2dx(mContext, 20).toInt()
        val resource = Glide.with(mContext)
            .asDrawable()
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .submit()
            .get()
        val rect = Rect(0, 0, dp16,dp16)
        resource.bounds = rect
        if (resource is GifDrawable) {
            resource.callback = this
            if(resource.isRunning)
            resource.isFilterBitmap = true
            resource.setLoopCount(GifDrawable.LOOP_FOREVER)
            resource.start()
        }

        return resource
    }

    override fun invalidateDrawable(who: Drawable) {
        mTextView.invalidate()
    }

    override fun scheduleDrawable(who: Drawable, what: Runnable, `when`: Long) {}
    override fun unscheduleDrawable(who: Drawable, what: Runnable) {}

    private inner class ImageGetterViewTarget(
        private val view: TextView,
        drawable: UrlDrawable_Glide
    ) :
        SimpleTarget<Drawable>() {
        private val mDrawable: UrlDrawable_Glide


        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
            val rect = Rect(0, 0, resource.intrinsicWidth * 2, resource.intrinsicHeight * 2)
            mDrawable.bounds = rect
            resource.bounds = rect
            mDrawable.setDrawable(resource)
            if (resource is GifDrawable) {
                mDrawable.callback = Companion[view]
                resource.setLoopCount(GifDrawable.LOOP_FOREVER)
                resource.start()
            }
            view.text = view.text
            view.invalidate()
        }

        private var request: Request? = null
        override fun getRequest(): Request? {
            return request
        }

        override fun setRequest(request: Request?) {
            this.request = request
        }

        init {
            mTargets.add(this)
            mDrawable = drawable
        }
    }

    companion object {
        private val TAG = R.id.glide_custom_view_target_tag
        operator fun get(view: View): GlideImageGetter {
            return view.getTag(TAG) as GlideImageGetter
        }
    }

    init {

//        clear(); 屏蔽掉这句在TextView中可以加载多张图片
        mTargets = HashSet()
        mTextView.setTag(TAG, this)
    }
}