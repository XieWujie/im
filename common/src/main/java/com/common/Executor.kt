package com.common

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
