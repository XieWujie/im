package com.vlog.photo

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.request.target.ImageViewTarget
import com.dibus.DiBus
import com.vlog.conversation.ImageLoadFinish
import java.io.File

class TransformationOfMaxSize(private val target: ImageView,private val maxSize:Int) : ImageViewTarget<Bitmap?>(target) {


    override fun setResource(resource: Bitmap?) {
        resource?:return

        //获取imageView的宽
        var imageViewWidth: Int = resource.width
        var imageViewHeight:Int = resource.height


        while (imageViewWidth  >maxSize){
            imageViewWidth/=2
            imageViewHeight/=2
        }

        val params: ViewGroup.LayoutParams = target.layoutParams
        params.height = imageViewHeight
        params.width = imageViewWidth
        target.layoutParams = params
        target.setImageBitmap(resource)
    }
}