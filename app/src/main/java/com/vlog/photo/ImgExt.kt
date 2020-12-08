package com.vlog.photo

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.transition.Transition
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
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

fun ImageView.showBigView(url:String){
    val realUrl = if (url.startsWith("/file/get")) {
        "$HOST_PORT$url"
    } else {
        url
    }
    val dialog = Dialog(context, R.style.big_photo)
    val parent = RelativeLayout(context)
    val view = ImageView(context)
    view.scaleType = ImageView.ScaleType.FIT_XY
    val p = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT)
    p.addRule(RelativeLayout.CENTER_IN_PARENT)
    parent.addView(view,p)
    parent.setOnClickListener {
        dialog.dismiss()
    }
    dialog.setContentView(parent)
    dialog.setCancelable(true)
    Glide.with(view).load(realUrl).dontAnimate().into(view)
    dialog.window?.setGravity(Gravity.CENTER)
    view.setOnLongClickListener {
        val dialog = AlertDialog.Builder(context,R.style.long_click_dialog)
            .setItems(arrayOf("保存图片")) { _, _ ->
                Glide.with(it).asFile().load(realUrl).dontAnimate().into(PhotoDownloadTarget(context))
            }
        dialog.show()
        true
    }

    dialog.show()
}