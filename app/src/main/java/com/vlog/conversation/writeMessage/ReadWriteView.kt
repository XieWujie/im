package com.vlog.conversation.writeMessage

import android.R
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.vlog.connect.MessageOnTime
import kotlin.math.abs

class ReadWriteView :View{
    private var startX:Int = 0
    private var startY:Int = 0
    private val d = 3f
    private lateinit var schedule: TimeSchedule


    constructor(context: Context,schedule: TimeSchedule):super(context){
        this.schedule = schedule
    }
    constructor(context: Context, att: AttributeSet):super(context, att)

    lateinit var paint: Paint
    lateinit var path: Path

    init {

        initPathOrPaint()
    }



    private fun initPathOrPaint() {
        paint = Paint()
        path = Path()
        //这个Paint是作为我们的背景bitmap的


        //设置用于我们绘画的Paint颜色为黑色
        paint.color = resources.getColor(R.color.black)
        //设置抗锯齿
        paint.isAntiAlias = true
        // 设置类型为画笔
        paint.style = Paint.Style.STROKE
        //设置画笔变为圆滑状
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeWidth = 4f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(path,paint)
    }


    fun addPath(p: MessageOnTime.P){
        val x = p.x/d
        val y = p.y/d
        when(p.type){
            MotionEvent.ACTION_DOWN->canvasStart(x,y)
            MotionEvent.ACTION_MOVE->cavasMove(x,y)
            MotionEvent.ACTION_UP->cavasEnd()
        }
    }



    private fun canvasStart(x: Float, y: Float) {
        //将path的起点移动到x,y的坐标处
        path.moveTo(x, y)
        //这个记录上一次的坐标点
        startX = x.toInt()
        startY = y.toInt()
        schedule.scheduleUp()
    }

    private fun cavasMove(x2: Float, y2: Float) {
        Log.d("actionMove:",x.toString())
        val dx = abs(x2 - startX)
        val dy = abs(y2 - startY)
        if (dx >= MOVE_WIDTH || dy >= MOVE_WIDTH) {
            val endx = (x2 + startX) / 2
            val endy = (y2 + startY) / 2
            //画出一条二次贝塞尔曲线，。。。我的理解就是 画出一条平滑的曲线（贝塞尔曲线就是这样的一条曲线，它是依据四个位置任意的点坐标绘制出的一条光滑曲线。baidu）
            path.quadTo(startX.toFloat(), startY.toFloat(), endx, endy)
            startX = x2.toInt()
            startY = y2.toInt()
        }
    }

    private fun cavasEnd() {
        path.lineTo(startX.toFloat(), startY.toFloat())
        schedule.scheduleDown()
    }

    companion object{
        private const val MOVE_WIDTH = 0.1

    }

}