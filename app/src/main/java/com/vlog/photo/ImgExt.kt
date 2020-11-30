package com.vlog.photo

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.common.HOST
import com.vlog.R

fun ImageView.load(url:String){
    if(url.isEmpty()) {
        Glide.with(this).load(R.drawable.avater_default).into(this)
    }else{
        Glide.with(this).load("$HOST$url").placeholder(R.drawable.avater_default).into(this)
    }
}