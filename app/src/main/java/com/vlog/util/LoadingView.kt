package com.vlog.util

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.vlog.R
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class LoadingView:View{

    private var radius = 0f

    private var color = Color.WHITE

    private var degree = 0.0
    private var d = 0

    private val paint = Paint()

    constructor(context:Context,attributeSet: AttributeSet):super(context,attributeSet){
        val ta = context.obtainStyledAttributes(attributeSet,R.styleable.LoadingView)
        color = ta.getColor(R.styleable.LoadingView_color,Color.WHITE)
        initPaint()
    }
    constructor(context: Context):super(context){
        initPaint()
    }

    private fun initPaint(){
        paint.color = color
        paint.style = Paint.Style.FILL
        paint.isDither = true
        paint.isAntiAlias = true
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var r = min(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec))
        radius = r.toFloat()
        r = MeasureSpec.makeMeasureSpec(r,MeasureSpec.EXACTLY)
        super.onMeasure(r, r)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?:return
        val rad = radius/15f
        val hashR = radius/2
        val maxR = radius/3
        val g = Math.PI/6
        degree = d/12.0*Math.PI
        for(i in 0..10) {
            val deg = degree-i*g
            val x = hashR+cos(deg)*maxR
            val y = sin(deg)*maxR+hashR
            val minR = (16-i)/16f*rad
            paint.alpha = 255-i*25
            canvas.drawCircle(x.toFloat(),y.toFloat(),minR,paint)
        }
        ++d
        postInvalidateDelayed(100)
    }

}