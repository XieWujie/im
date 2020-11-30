package com.vlog.connect

interface WsConnectionListener {

    fun open(ws:WsConnection)

    fun failure(ws: WsConnection,t:Exception)

    fun onMessage(text:String)


}