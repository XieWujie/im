package com.vlog.ui


import android.animation.ValueAnimator
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.LinearLayout
import android.widget.Scroller
import androidx.annotation.RequiresApi
import com.vlog.R
import kotlin.math.abs


/**
 * Description: <协调式折叠布局><br></br>
 * Author:      mxdl<br></br>
 * Date:        2019/10/9<br></br>
 * Version:     V1.0.0<br></br>
 * Update:     <br></br>
 * 主要功能：主要用它来协调headView和contentView的滚动冲突，Header是可以展开和收缩的，content是可以滚动的ListView
 * 难点：解决滑动冲突，因为header是可以上滑和下滑，并且可以收缩，而content也是可以上下滑动的，这样情况下，
 * 我们采用外部拦截法，来拦截header所需要的事件，不需要的事件就交给content处理了，我们主要思考的问题是：哪些事件是我们需要的？
 * 难题1：Acction_move是我们所需要的，哪些action_move是我们需要的？
 * 1.当前的y坐标小于headerHeight是不需要的
 * 2.当前的x的滑动距离大于y的滑动距离是不需要的
 * 3.展开情况下，上滑是需要的
 * 4.折叠情况下，下滑是需要的
 * 5.其他是不需要的
 * 难题2：
 * 需要的的事件已经拦截下来了，接下来的事情就是要事件处理了
 * 1.如果是摁下事件我们是不要处理的
 * 2.如果是移动的事件我们根据持续的y的距离来不断的是指header的高度即可
 * 3.需要手指抬起的需要有回弹的效果，如果当前距离大于一般则展开否则收缩
 *
 * ****常见错误****
 * 1.dx和dy比较大小条件搞错
 * 2.设置高度的是极端值条件搞错【第二个判断写错了】
 * 3.onTouchEvent方法最后好返回true
 * 4.setHeaderHeight方法最后要给currHeaderHeight重新赋值
 * 5.onInterceptTouchEvent方法mLastInterceptY赋值不对导致的事件没有被拦截
</协调式折叠布局> */
class ExpandedLayout : LinearLayout {
    private var headerView: View? = null
    private var mOriginHeaderHeight = 0
    private var mCurrHeaderHeight = 0
    private var mScaledTouchSlop = 0
    private var mLastInterceptX = 0
    private var mLastInterceptY = 0
    private var mLastX = 0
    private var mLastY = 0
    private val mStateExpand = 0
    private val scroller = Scroller(context)
    private val mStateCollapsed = 1
    private var mState = mStateExpand
    private var mGiveUpTouchEventListener: GiveUpTouchEventListener? = null
    fun setGiveUpTouchEventListener(giveUpTouchEventListener: GiveUpTouchEventListener?) {
        mGiveUpTouchEventListener = giveUpTouchEventListener
    }

    interface GiveUpTouchEventListener {
        fun giveUpTouchEvent(): Boolean
    }

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        if (hasWindowFocus && headerView == null) {
            headerView = getChildAt(0)
            if (headerView != null) {
                mOriginHeaderHeight = headerView!!.measuredHeight
                mCurrHeaderHeight = mOriginHeaderHeight
                mScaledTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
            }
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        var intercept = false
        val x = ev.x.toInt()
        val y = ev.y.toInt()
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                mLastInterceptX = x
                mLastInterceptY = y
                mLastX = x
                mLastY = y
                intercept = false
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = x - mLastInterceptX
                val dy = y - mLastInterceptY
                intercept = if (y <= mCurrHeaderHeight) {
                    false
                } else if (abs(dx) > abs(dy)) {
                    false
                } else if (mState == mStateExpand && dy <= -mScaledTouchSlop) {
                    //上滑
                    true
                } else if(dy > mScaledTouchSlop){
                    //下滑
                    true
                }else{
                    false
                }
            }
            MotionEvent.ACTION_UP -> {
                mLastInterceptX = 0
                mLastInterceptY = 0
                intercept = false
            }
        }
        return intercept
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x.toInt()
        val y = event.y.toInt()
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = x - mLastX
                val dy = y - mLastY
                mCurrHeaderHeight += dy
                setHeaderHeight(mCurrHeaderHeight)
            }
            MotionEvent.ACTION_UP -> {
                var dest = 0
                if (mCurrHeaderHeight <= mOriginHeaderHeight * 0.5) {
                    dest = 0
                    mState = mStateCollapsed
                } else {
                    dest = mOriginHeaderHeight
                    mState = mStateExpand
                }
                smoothSetHeaderHeight(mCurrHeaderHeight, dest, 500)
            }
        }
        mLastX = x
        mLastY = y
        return super.onTouchEvent(event)
    }

    private fun smoothSetHeaderHeight(from: Int, to: Int, duration: Int) {
        val valueAnimator = ValueAnimator.ofInt(from, to).setDuration(duration.toLong())
        valueAnimator.addUpdateListener { animation -> setHeaderHeight(animation.animatedValue as Int) }
        valueAnimator.start()
    }

    private fun setHeaderHeight(height: Int) {
        var height = height
        if (height <= 0) {
            height = 0
        } else if (height >= mOriginHeaderHeight) {
            height = mOriginHeaderHeight
        }
        mState = if (height == 0) {
            mStateCollapsed
        } else {
            mStateExpand
        }
        headerView!!.layoutParams.height = height
        headerView!!.requestLayout()

    }
}