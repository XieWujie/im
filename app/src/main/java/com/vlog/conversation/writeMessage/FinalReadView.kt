package com.vlog.conversation.writeMessage

import android.R
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View


class FinalReadView :View{

    private var startX:Int = 0
    private var startY:Int = 0


    constructor(context: Context):super(context)
    constructor(context: Context, att: AttributeSet):super(context, att)

    lateinit var paint:Paint
    lateinit var path:Path

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
        paint.strokeWidth = 2f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(path,paint)
    }





    private fun canvasStart(x: Float, y: Float) {
        //将path的起点移动到x,y的坐标处
        path.moveTo(x, y)
        //这个记录上一次的坐标点
        startX = x.toInt()
        startY = y.toInt()
    }

    private fun cavasMove(x: Float, y: Float) {
        val endX = (x + startX) / 2
        val endY = (y + startY) / 2
        //画出一条二次贝塞尔曲线，。。。我的理解就是 画出一条平滑的曲线（贝塞尔曲线就是这样的一条曲线，它是依据四个位置任意的点坐标绘制出的一条光滑曲线。baidu）
        path.quadTo(startX.toFloat(), startY.toFloat(), endX, endY)
        startX = x.toInt()
        startY = y.toInt()
    }

    private fun cavasEnd() {
        path.lineTo(startX.toFloat(), startY.toFloat())
    }


    fun writeWord(messageWriteWord: MessageWriteWord){


       val dx = messageWriteWord.maxH - messageWriteWord.minH
        val dy =messageWriteWord.maxV - messageWriteWord.minV
        val max:Float
        var dpx = 0f
        var dpy = 0f
        if(dx>dy){
            max = dx
            dpy = (dx-dy)/2f
        }else{
            max = dy
            dpx = (dy-dx)/2f
        }

        post {
            path.reset()
            val dp = max/measuredHeight.toFloat()
            val minX = messageWriteWord.minH
            val minY = messageWriteWord.minV
            for(v in messageWriteWord.list){
                val nx = (v.x-minX+dpx)/dp
                val ny = (v.y-minY+dpy)/dp
                when(v.type){
                    MotionEvent.ACTION_DOWN->canvasStart(nx,ny)
                    MotionEvent.ACTION_MOVE->cavasMove(nx,ny)
                    MotionEvent.ACTION_UP->cavasEnd()
                }
            }
            invalidate()
        }

    }

}