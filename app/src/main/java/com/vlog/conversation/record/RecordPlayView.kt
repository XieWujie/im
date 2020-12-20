package com.vlog.conversation.record

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.annotation.RequiresApi
import java.time.Duration

class RecordPlayView:View {

    constructor(context:Context):super(context)

    constructor(context: Context,attributeSet: AttributeSet):super(context,attributeSet)

    private var degree = 3
    private val valueAnimator = ValueAnimator.ofInt(1,3).apply {
        repeatMode = ValueAnimator.RESTART
    }


    private val paint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.BLACK
        strokeWidth = 5f
    }




    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?:return
        val h = height.toFloat()
        val maxG = h/4f
        val midG = h/3f
        val minG = h/2f
        if(Build.VERSION.SDK_INT<21){
            return
        }
        if(degree == 3){
            canvas.drawArc(maxG,maxG,h,h,250f,290f,true,paint)
        }
        if(degree>=2){
            canvas.drawArc(midG,midG,h,h,250f,290f,true,paint)
        }
        if(degree>=1) {
            canvas.drawArc(minG, minG, h, h, 250f, 290f, true, paint)
        }
    }

    fun startPlay(duration: Long){
        post {
            valueAnimator.removeAllUpdateListeners()
            valueAnimator.duration = duration
            valueAnimator.addUpdateListener {
                degree = it.animatedValue as Int
                invalidate()
            }
            valueAnimator.start()

        }
    }

}