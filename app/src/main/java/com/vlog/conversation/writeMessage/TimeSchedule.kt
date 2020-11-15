package com.vlog.conversation.writeMessage

import com.common.scheduleExecutors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

private const val TAG = "TimeSchedule"
class TimeSchedule(private val task:()->Unit, private val millionTimeSchedule:Long) {


    private var lock = AtomicInteger(0)


    fun scheduleDown(){
        scheduleExecutors.schedule(Runnable {
           if(lock.get() == 1){
               task.invoke()
           }
            lock.decrementAndGet()
        },millionTimeSchedule,TimeUnit.MILLISECONDS)
    }

    fun scheduleUp(){
        lock.incrementAndGet()
    }
}