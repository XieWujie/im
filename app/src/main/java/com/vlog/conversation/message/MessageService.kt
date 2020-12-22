package com.vlog.conversation.message

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.core.app.JobIntentService
import com.dibus.AutoWire
import com.vlog.database.Message
import dibus.app.MessageServiceCreator
import java.io.File
import java.io.IOException
import java.lang.IllegalArgumentException
import java.util.*
import kotlin.collections.HashMap


class MessageService: JobIntentService() {


    @AutoWire
    lateinit var messageSource: MessageSource

    private val mainHandler = Handler(Looper.getMainLooper())

    init {
        MessageServiceCreator.inject(this)
    }


    override fun onHandleWork(intent: Intent) {
        when(intent.getIntExtra(TYPE,-1)){
            MSG_SEND->{
                val message = intent.getParcelableExtra<Message>("message")
                    ?:throw IllegalArgumentException("message cannot be null")
                fileMessage(message,intent)
            }
        }
    }

    private fun fileMessage(message: Message, intent: Intent){
        val listenerKey = intent.getStringExtra("listenerKey")
        val progress =  progresses[listenerKey]
        val msgCallback = callbacks[listenerKey]
        when(message.messageType){
            Message.MESSAGE_IMAGE,Message.MESSAGE_RECORD->{
                val file = File(message.content)
                try {
                    val msg = messageSource.postFileMessage(message,file,progress)
                    mainHandler.post {
                        msgCallback?.callback(msg,null)
                    }
                }catch (e:IOException){
                    mainHandler.post {
                        msgCallback?.callback(message,e)
                    }
                }

            }
        }
    }




    companion object{

        private const val JOB_ID = 2088
        private const val TYPE = "message_type"
        private const val MSG_SEND = 1
        private const val SEND_CALLBACK = "send_callback"
        private val random = Random()

        private val callbacks = HashMap<String,MsgCallback?>()
        private val progresses = HashMap<String,ProgressListener?>()


        fun sendMessage(context: Context,message:Message,messageCallback:MsgCallback,progress:ProgressListener? = null){
            val intent = Intent()
            intent.putExtra(TYPE, MSG_SEND)
            intent.putExtra("message",message)
            val pk = Date().time.toString()
            intent.putExtra("listenerKey",pk)
            progresses[pk] = progress
            callbacks[pk] = messageCallback
            enqueue(context,intent)
        }

         private fun enqueue(context: Context,intent:Intent){
            enqueueWork(context,MessageService::class.java, JOB_ID,intent)
        }
    }

}