package com.vlog.util

import android.annotation.SuppressLint
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

@SuppressLint("ClickableViewAccessibility")
fun RecyclerView.onClick(click:()->Unit){
    val gestureDetector = GestureDetector(this.context,object : GestureDetector.OnGestureListener{
        override fun onDown(e: MotionEvent?): Boolean {
            return false
        }

        override fun onShowPress(e: MotionEvent?) {

        }

        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            click.invoke()
            return true
        }

        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent?,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            return false
        }

        override fun onLongPress(e: MotionEvent?) {

        }

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent?,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            return false
        }

    })
    this.setOnTouchListener { v, event ->
        gestureDetector.onTouchEvent(event)
    }
}

@SuppressLint("ClickableViewAccessibility")
fun RecyclerView.onLongClick(click:()->Unit){
    val gestureDetector = GestureDetector(this.context,object : GestureDetector.SimpleOnGestureListener(){
        override fun onLongPress(e: MotionEvent?) {
            super.onLongPress(e)
            click.invoke()
        }

        override fun onDoubleTap(e: MotionEvent?): Boolean {
            click.invoke()
            return true
        }

        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            click.invoke()
            return true
        }
    })

    this.setOnTouchListener { v, event ->
        gestureDetector.onTouchEvent(event)
    }
}