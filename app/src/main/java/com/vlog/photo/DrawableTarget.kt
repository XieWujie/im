package com.vlog.photo

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.common.HOST_PORT

class DrawableTarget(private val view:View):SimpleTarget<Drawable>() {
    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
        if(resource is BitmapDrawable){
            resource
        }
        view.background = resource
    }
}

fun View.setBg(url:String){
    val realUrl = if (url.startsWith("/file/get")) {
        "$HOST_PORT$url"
    } else {
        url
    }
    Glide.with(this).asDrawable().load(realUrl).centerCrop().into(DrawableTarget(this))
}

