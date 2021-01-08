package com.vlog.connect.notify

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.core.app.RemoteInput
import com.common.ext.toast
import com.dibus.DiBus
import com.vlog.connect.MessageSend
import com.vlog.database.Message

class ReplyBroadcast:BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("reply","start")
        context?:return
        intent?:return
        val bundle: Bundle = RemoteInput.getResultsFromIntent(intent)?:return
        val reply: String = bundle.getString("reply")?:return
        Log.d("reply",reply)
        val message = intent.getParcelableExtra<Message>("message")?:return
        val m = Message.obtain(message.conversationId,Message.MESSAGE_TEXT,reply,message.fromType)
        DiBus.postEvent(m,MessageSend{
            if(it == null){
                Notify(context).notifyUpdate(message,reply)
                context?.toast("发送成功")
            }else{
                context?.toast("发送失败")
            }
        })
    }


    companion object{

        private var instance:ReplyBroadcast? = null

        fun register(context: Context){
            if(instance != null){
               // context.unregisterReceiver(instance)
            }else{
                val newInstance = ReplyBroadcast()
                val intentFilter = IntentFilter()
                intentFilter.addAction("com.vlog.message.reply")
                context.registerReceiver(newInstance, intentFilter)
                instance = newInstance
            }
        }

        fun unRegister(context: Context){
            if(instance != null) {
                context.unregisterReceiver(instance)
                instance = null
            }
        }
    }
}