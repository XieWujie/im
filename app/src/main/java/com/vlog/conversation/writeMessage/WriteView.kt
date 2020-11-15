package com.vlog.conversation.writeMessage

import android.R
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.dibus.AutoWire
import com.dibus.DiBus
import com.vlog.connect.MessageOnTime
import com.vlog.conversation.writeMessage.event.SingleWriteEvent
import dibus.app.WriteViewCreator
import kotlin.collections.ArrayList
import kotlin.math.abs

private const val TAG = "WriteView"

class WriteView :View{

    private var startX = 0f
    private var startY = 0f
    private var firstX = -1f
    private var firstY = -1f
    private var list = ArrayList<MessageOnTime.P>()

    private val schedule = TimeSchedule({
        Log.d(TAG,"timeSchedule")
        path.reset()
        postInvalidate()
        DiBus.postEvent(SingleWriteEvent(list))
        list = ArrayList<MessageOnTime.P>()
    },500)


    @AutoWire
    lateinit var messageOnTime: MessageOnTime

    constructor(context: Context):super(context)
    constructor(context: Context, att: AttributeSet):super(context, att)

    private lateinit var paint:Paint
    private lateinit var path:Path

    init {
        WriteViewCreator.inject(this)
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
        paint.strokeWidth = 16f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(path,paint)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {

        val x = event.x
        val y = event.y
        if(firstX <0){
            firstX = x
            firstY = y
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                canvasStart(x, y)
            }
            MotionEvent.ACTION_MOVE -> cavasMove(x, y)
            MotionEvent.ACTION_UP -> canvasEnd()
            else -> {
                schedule.scheduleDown()
            }
        }
        invalidate()
        return true
    }



    private fun canvasStart(x: Float, y: Float) {
        //将path的起点移动到x,y的坐标处
        path.moveTo(x, y)
        //这个记录上一次的坐标点
        startX = x
        startY = y
        val p = MessageOnTime.P(x,y,MotionEvent.ACTION_DOWN)
        list.add(p)
        schedule.scheduleUp()
    }

    private fun cavasMove(x: Float, y: Float) {
        val dx = abs(x - startX)
        val dy = abs(y - startY)
        if (dx >= MOVE_WIDTH || dy >= MOVE_WIDTH) {
            val endx = (x + startX) / 2
            val endy = (y + startY) / 2
            //画出一条二次贝塞尔曲线，。。。我的理解就是 画出一条平滑的曲线（贝塞尔曲线就是这样的一条曲线，它是依据四个位置任意的点坐标绘制出的一条光滑曲线。baidu）
            path.quadTo(startX, startY, endx, endy)
            startX = x
            startY = y
            val p = MessageOnTime.P(x,y,MotionEvent.ACTION_MOVE)
            list.add(p)
        }
    }

    private fun canvasEnd() {

        path.lineTo(startX, startY)
        val p = MessageOnTime.P(startX,startY,MotionEvent.ACTION_UP)
        list.add(p)
        schedule.scheduleDown()
    }

    companion object{
        private const val MOVE_WIDTH = 0.1

    }

}