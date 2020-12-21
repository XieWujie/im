package com.vlog.conversation.record

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.os.Build
import android.util.AttributeSet
import android.view.View
import com.vlog.R
import kotlin.math.min

class CirclePlayBar :View{

    private var radius = 0f
    private lateinit var info:PaintInfo
    private var color = Color.WHITE

    constructor(context:Context):super(context)
    constructor(context: Context,attributeSet: AttributeSet):super(context,attributeSet){
        val ta = context.obtainStyledAttributes(attributeSet,R.styleable.CirclePlayBar)
        color = ta.getColor(R.styleable.CirclePlayBar_bar_color,Color.WHITE)
    }
    private val redPaint:Paint = Paint()
    private val barPaint:Paint = Paint()
    private var angle = 0f
    private var allTime = 0f
    private var isPlaying = false

    init {
        barPaint.color = color
        barPaint.strokeWidth = 4.0f
        barPaint.isDither = true
        barPaint.isAntiAlias = true
        barPaint.style = Paint.Style.STROKE
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var r = min(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec))
        radius = r.toFloat()
        cacheResult(radius)
        r = MeasureSpec.makeMeasureSpec(r,MeasureSpec.EXACTLY)
        super.onMeasure(r, r)
    }


    fun setTime(time:Int){
       val  angle = (time.toFloat()/allTime) *360
        post {
            this.angle = angle
            invalidate()
        }
    }



    fun setAllTime(time: Int){
        post {
            allTime = time.toFloat()
            angle = 0f
            invalidate()
        }
    }

    fun setIsPlaying(state:Boolean){
        post {
            isPlaying = state
        }
    }
    fun getPlayState() = isPlaying

    private fun cacheResult(radius:Float){
        val b = radius/10
        val left = b*3.2f
        val right = b*6.8f
        val top = b*3
        val bottom = b*7
        info = PaintInfo(left,top,bottom,right)
    }

    override fun onDraw(canvas: Canvas) {
        if(isPlaying){
            canvas.drawPath(info.playingPath,barPaint)
        }else{
            canvas.drawPath(info.pausePath,info.pausePaint)
        }
        val seg = 0.1f*radius
        val r = radius-seg
        if(Build.VERSION.SDK_INT>=21) {
            canvas.drawArc(seg, seg, r, r, 0f, angle, false, barPaint)
        }
    }


    private inner class PaintInfo(left:Float, top:Float,bottom:Float,right:Float){
        val pausePath:Path = Path()
        val playingPath = Path()
         val pausePaint = Paint().apply {
            color = color
            redPaint.style = Paint.Style.FILL
            redPaint.isDither = true
            redPaint.isAntiAlias = true
        }

        init {
            pausePath.moveTo(left,top)
            pausePath.fillType = Path.FillType.EVEN_ODD
            pausePath.lineTo(left,bottom)
            pausePath.lineTo(right,radius/2)
            pausePath.lineTo(left,top)
            playingPath.moveTo(left,top)
            playingPath.lineTo(left,bottom)
            playingPath.moveTo(right,top)
            playingPath.lineTo(right,bottom)
        }

    }
}