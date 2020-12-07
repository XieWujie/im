package com.common

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

private val executor =
    ThreadPoolExecutor(1,
        1,
        100,
        TimeUnit.SECONDS,
        LinkedBlockingQueue())

fun pushExecutors(f:()->Unit){
    executor.execute(f)
}
 val scheduleExecutors = Executors.newScheduledThreadPool(1)
private val mMainHandler = Handler(Looper.getMainLooper())

fun pushMainThread(f: () -> Unit){
    mMainHandler.post(f)
}
