package com.vlog.connect

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import com.common.HOST
import com.common.pushExecutors
import com.dibus.AutoWire
import com.dibus.BusEvent
import com.dibus.DiBus
import com.google.gson.Gson
import com.vlog.connect.notify.Notify
import com.vlog.connect.notify.ReplyBroadcast
import com.vlog.conversation.ConversationActivity
import com.vlog.conversation.MessageChangeEvent
import com.vlog.conversation.MessageRemoveEvent
import com.vlog.database.*
import com.vlog.user.Owner
import com.vlog.user.UserSource
import dibus.app.WsConnectionServiceCreator

class WsConnectionService:JobIntentService(),WsConnectionListener {

    private val connection = WsConnection()
    private var userId = -1


    private lateinit var notify: Notify


    @AutoWire
    lateinit var friendDao: FriendDao

    @AutoWire
    lateinit var gson: Gson
    @AutoWire
    lateinit var userSource:UserSource
    @AutoWire
    lateinit var msgDao:MsgDao

    @AutoWire
    lateinit var roomDao:RoomDao

    init {
        WsConnectionServiceCreator.inject(this)
    }

    override fun onCreate() {
        super.onCreate()
        notify = Notify(applicationContext)
        ReplyBroadcast.register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        ReplyBroadcast.unRegister(this)
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
        Owner().isLogout = false
    }

    override fun failure(ws: WsConnection, t: Exception) {
        Log.d(TAG,"failure")
        t.printStackTrace()
        if(!Owner().isLogout){
            connection.connect()
        }
    }

    data class MsgWrap(val message:Message,val user:User,val room: Room?)

    override fun onMessage(text: String) {
        val m = gson.fromJson(text, MsgWrap::class.java)
        pushExecutors {
            val msg = m.message

            //撤回消息，发送事件并删除数据库的消息
            if(msg.messageType == Message.MESSAGE_WITHDRAW){
                DiBus.postEvent(MessageRemoveEvent(msg))
                msgDao.delete(msg)
                return@pushExecutors
            }
            //自己发送的消息，更新数据库并发送数据更新事件
            if(msg.sendFrom == Owner().userId){
                DiBus.postEvent(MessageChangeEvent(MsgWithUser(m.message, m.user)))
                msgDao.insert(m.message.apply { isSend = true })
                return@pushExecutors
            }
            //通过好友认证，数据库插入联系人
            if(msg.messageType == Message.Agree){
                val friend = Friend(m.user,msg.conversationId,Owner().userId)
                friendDao.insert(friend)
            }
            //消息通知
            notifyEvent(m)
            if(m.room != null){
                //roomDao.insert(m.room)
            }
            userSource.insert(m.user)
            msgDao.insert(m.message.apply { isSend = true })
        }
        Log.d(TAG,"onMessage:$text")
    }

    private fun notifyEvent(m:MsgWrap){
        val msg = m.message
        if(msg.sendFrom != Owner().userId &&msg.notify
            && (ConversationActivity.currentConversationId != msg.conversationId ||!ConversationActivity.isAlive)) {
            when (m.message.fromType) {
                Message.FROM_TYPE_FRIEND -> {
                    notify.sendNotification(m.user, m.message)
                }
                Message.FROM_TYPE_ROOM -> {
                    notify.sendNotification(m.room!!, m.message)
                }
            }
        }
    }

    override fun onClose(ws: WsConnection) {
        Log.d(TAG,"onClosed")
        if(!Owner().isLogout){
            connection.connect()
        }
    }
}
