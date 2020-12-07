package com.vlog.photo

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import com.common.util.Util

class ImgProgressFilter:View {


    constructor(context: Context):super(context)

    constructor(context: Context,attributeSet: AttributeSet):super(context,attributeSet)

    private val darkPaint = Paint()

    private val progressTextPaint = TextPaint()

    private var progress = 0

    init {
        darkPaint.apply {
            color = Color.parseColor("#000000")
            alpha = 180
            style = Paint.Style.FILL
        }
        progressTextPaint.apply {
            color = Color.WHITE
            style = Paint.Style.FILL
            textSize = Util.dp2dx(context,16).toFloat()
        }
    }

    fun setProgress(progress: Int){
        post {
            this.progress = progress
            invalidate()
        }
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?:return
        val x = measuredWidth/2f-16
        val y = measuredHeight/2f+16
        canvas.drawText(progress.toString(),x,y,progressTextPaint)
        val drawHeight = measuredHeight*(100-progress)/100f
        canvas.drawRect(0f,0f,width.toFloat(),drawHeight,darkPaint)
    }

}