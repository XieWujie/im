package com.vlog.conversation.message

import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService
import com.dibus.AutoWire
import com.vlog.connect.WsListener
import com.vlog.database.Message

class MessageService: JobIntentService() {


    @AutoWire
    lateinit var ws:WsListener


    init {

    }



    override fun onHandleWork(intent: Intent) {
        when(intent.getStringExtra(TYPE)){
            WS_CONNECT->{
                ws.connect()
            }
        }
    }




    companion object{

        private const val JOB_ID = 2088
        private const val TYPE = "message_type"
        private const val WS_CONNECT = "wsConnect"
        private const val MSG_SEND = "msg_send"

        fun connectWs(context: Context){
            val intent = Intent()
            intent.putExtra(TYPE, WS_CONNECT)
            enqueue(context,intent)
        }

        fun sendMessage(context: Context,message:Message){
            val intent = Intent()
            intent.putExtra(MSG_SEND,message)
            enqueue(context,intent)
        }

         private fun enqueue(context: Context,intent:Intent){
            enqueueWork(context,MessageService::class.java, JOB_ID,intent)
        }
    }

}