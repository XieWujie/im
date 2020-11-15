package com.vlog.connect

import android.view.MotionEvent
import com.dibus.AutoWire
import com.dibus.CREATE_SINGLETON
import com.dibus.Service
import com.google.gson.Gson
import com.vlog.user.Owner
import com.vlog.user.UserFetcher
import com.vlog.database.Message
import okhttp3.WebSocket
import java.util.concurrent.LinkedBlockingQueue


@Service(CREATE_SINGLETON)
class MessageOnTime :Runnable{

    @AutoWire
    lateinit var ws:WebSocket
    @AutoWire
    lateinit var userFetcher: UserFetcher
    @AutoWire
    lateinit var gson: Gson
    private var started = false

    private val p = P(0f,0f,MotionEvent.ACTION_DOWN)

    private val queue = LinkedBlockingQueue<P>()
    private val message = Message(0, Owner().userId,0, Message.MESSAGE_WRITE,"",0)




    private fun sendWriteMessage(){
        val json = gson.toJson(message)
        ws.send(json)
    }

    fun readWrite(x:Float,y:Float,type: Int){
        if(started){
            queue.offer(p.copy(x,y,type))
        }else{
            Thread(this).start()
            started = true
        }
    }

    override fun run() {
        var p: P?
        val list = ArrayList<P>()
       while (true){
           p = queue.poll()
           if(p !=null){
               list.add(p)
           }else{
               sendWriteMessage()
               list.clear()
               p = queue.take()
               list.add(p)
           }
       }
    }

    fun setDestination(user:Int){
        message.destination = user
    }

    data class P(val x:Float,val y:Float,val type:Int)
}