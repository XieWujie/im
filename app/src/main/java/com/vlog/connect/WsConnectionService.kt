package com.vlog.connect

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import com.common.HOST
import com.common.pushExecutors
import com.dibus.AutoWire
import com.dibus.BusEvent
import com.google.gson.Gson
import com.vlog.database.Message
import com.vlog.database.MsgDao
import com.vlog.database.MsgWithUser
import com.vlog.user.Owner
import com.vlog.user.UserSource
import dibus.app.WsConnectionServiceCreator

class WsConnectionService:JobIntentService(),WsConnectionListener {

    private val connection = WsConnection()
    private var userId = -1

    @AutoWire
    lateinit var gson: Gson
    @AutoWire
    lateinit var userSource:UserSource
    @AutoWire
    lateinit var msgDao:MsgDao

    init {
        WsConnectionServiceCreator.inject(this)
    }

    override fun onHandleWork(intent: Intent) {
        when(intent.getIntExtra(ACTION,-1)){
            ACTION_CONNECT->{
                userId = intent.getIntExtra("userId",-1)
                connection.build(HOST,8000,"/ws?userId=$userId",this, emptyMap())
                connection.connect()
            }
        }
    }


    @BusEvent
    fun handleMessage(m: Message, messageSend: MessageSend){
        val json = gson.toJson(m)
        connection.writeText(json,messageSend.sendState)
    }


    companion object{

        private const val ACTION = "ACTION"
        private const val ACTION_CONNECT = 1
        private const val JOB_ID = 2078
        private const val TAG = "WsConnectionService"

        fun connect(context: Context,userId:Int){
            val intent = Intent()
            intent.putExtra(ACTION, ACTION_CONNECT)
            intent.putExtra("userId",userId)
            enqueueWork(context,WsConnectionService::class.java, JOB_ID,intent)
        }
    }

    override fun open(ws: WsConnection) {

    }

    override fun failure(ws: WsConnection, t: Exception) {
        Log.d(TAG,"failure")
        t.printStackTrace()
        connection.connect()
    }

    override fun onMessage(text: String) {
        val m = gson.fromJson(text, MsgWithUser::class.java)

        pushExecutors {
            userSource.insert(m.user)
            msgDao.insert(m.message.apply { isSend = true })
        }
        Log.d(TAG,"onMessage:$text")
    }

    override fun onClose(ws: WsConnection) {
        Log.d(TAG,"onClosed")
    }

}