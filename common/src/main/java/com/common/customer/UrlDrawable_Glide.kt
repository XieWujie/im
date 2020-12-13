package com.common.customer

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.drawable.Drawable


class UrlDrawable_Glide : Drawable(), Drawable.Callback {
    private var mDrawable: Drawable? = null
    override fun draw(canvas: Canvas) {
        if (mDrawable != null) {
            mDrawable!!.draw(canvas)
        }
    }

    override fun setAlpha(alpha: Int) {
        if (mDrawable != null) {
            mDrawable!!.alpha = alpha
        }
    }

    override fun setColorFilter(cf: ColorFilter?) {
        if (mDrawable != null) {
            mDrawable!!.colorFilter = cf
        }
    }

    @SuppressLint("WrongConstant")
    override fun getOpacity(): Int {
        return if (mDrawable != null) {
            mDrawable!!.opacity
        } else 0
    }

    fun setDrawable(drawable: Drawable) {
        if (mDrawable != null) {
            mDrawable!!.callback = null
        }
        drawable.callback = this
        mDrawable = drawable
    }

    override fun invalidateDrawable(who: Drawable) {
        if (callback != null) {
            callback!!.invalidateDrawable(who)
        }
    }

    override fun scheduleDrawable(who: Drawable, what: Runnable, `when`: Long) {
        if (callback != null) {
            callback!!.scheduleDrawable(who, what, `when`)
        }
    }

    override fun unscheduleDrawable(who: Drawable, what: Runnable) {
        if (callback != null) {
            callback!!.unscheduleDrawable(who, what)
        }
    }
}