package com.vlog.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.widget.FrameLayout
import com.vlog.R
import kotlin.math.min

class LoadingView:FrameLayout {

    private var radius = 0f

    private var color = Color.WHITE

    constructor(context:Context,attributeSet: AttributeSet):super(context,attributeSet){
        val ta = context.obtainStyledAttributes(attributeSet,R.styleable.LoadingView)
        color = ta.getColor(R.styleable.LoadingView_color,Color.WHITE)
    }
    constructor(context: Context):super(context)


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var r = min(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec))
        radius = r.toFloat()
        r = MeasureSpec.makeMeasureSpec(r,MeasureSpec.EXACTLY)
        super.onMeasure(r, r)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }
}