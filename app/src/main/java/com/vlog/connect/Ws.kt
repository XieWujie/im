package com.vlog.connect

import android.util.Log
import android.view.ViewGroup
import com.dibus.*
import com.google.gson.Gson
import com.vlog.database.Message
import com.vlog.database.MsgDao
import com.vlog.database.MsgWithUser
import com.vlog.user.Owner
import okhttp3.*
import okio.ByteString

private const val TAG = "WsListener"
@Service(createModel = CREATE_SINGLETON)
class WsListener:WebSocketListener() {

    @AutoWire
    lateinit var okHttpClient :OkHttpClient

    @AutoWire
    lateinit var msgDao: MsgDao

    private val request = Request.Builder()
        .url("ws://192.168.137.1:8000/ws?userId=${Owner().userId}")
        .build()

    private var ws:WebSocket? = null

    @AutoWire
    lateinit var gson: Gson

    private lateinit var p:ViewGroup.LayoutParams



    @BusEvent
    fun handleMessage(m: Message, messageSend: MessageSend){
        ws?:connect()
        val json = gson.toJson(m)
        messageSend.sendState(ws?.send(json)?:false)
    }



    fun connect(){
        ws = okHttpClient.newWebSocket(request,this)
    }

    @Provide(CREATE_SINGLETON)
    fun provideWs():WebSocket{
        ws?:connect()
        return ws!!
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
        Log.d(TAG,"onClosed")
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosing(webSocket, code, reason)
        Log.d(TAG,"onClosing")
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response)
        Log.d(TAG,"onFailure:${t.message}")
        t.printStackTrace()
        ws = null
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        val m = gson.fromJson(text, MsgWithUser::class.java)
        if(m.message.sendFrom != Owner().userId)
        DiBus.postEvent(m)
        Log.d(TAG,"onMessage:$text")
        msgDao.insert(m)
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        super.onMessage(webSocket, bytes)
        Log.d(TAG,"onMessage:$bytes")
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        Log.d(TAG,"onOpen:")
    }

}