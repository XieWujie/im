package com.vlog.photo

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.transition.Transition
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
import com.common.HOST_PORT
import com.common.pushExecutors
import com.common.util.Util
import com.vlog.R

fun ImageView.load(url: String) {
    if (url.isEmpty()) {
        Glide.with(this).load(R.drawable.avater_default).into(this)
    } else {
        if (url.startsWith("/file/get")) {
            Glide.with(this).load("$HOST_PORT$url").into(this)
        } else {
            Glide.with(this).load(url).into(this)
        }

    }
}

fun ImageView.loadWithMaxSize(url: String, maxSize: Int) {
    val realUrl = if (url.startsWith("/file/get")) {
        "$HOST_PORT$url"
    } else {
        url
    }
    Glide.with(this).asBitmap().load(realUrl).into(TransformationOfMaxSize(this,maxSize))

}