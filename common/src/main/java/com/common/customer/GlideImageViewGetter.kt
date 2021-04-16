package com.common.customer

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.text.Html
import android.widget.EditText
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.common.util.Util


class GlideImageGetter(private val mContext: Context, private val mTextView: TextView) :
    Html.ImageGetter, Drawable.Callback {

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
        if (resource is GifDrawable && mTextView !is EditText) {
            resource.callback = this
            if(resource.isRunning) {
                resource.isFilterBitmap = true
            }
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


}