package com.vlog.photo

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.common.HOST_PORT
import com.common.util.ScreenUtils

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
    val height = ScreenUtils.getScreenHeight(this.context)
    val width = ScreenUtils.getScreenWidth(context)
    Glide.with(this).asDrawable().load(realUrl).centerCrop().override(width,height).into(DrawableTarget(this))
}

fun View.setBg(id:Int){
    val height = ScreenUtils.getScreenHeight(this.context)
    val width = ScreenUtils.getScreenWidth(context)
    Glide.with(this).asDrawable().load(id).centerCrop().override(width,height).into(DrawableTarget(this))
}

