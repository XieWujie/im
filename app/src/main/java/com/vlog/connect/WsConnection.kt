
package com.vlog.connect

import com.dibus.Service
import com.google.gson.Gson
import com.vlog.user.Owner
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.connection.Exchange
import okio.ByteString
import okio.ByteString.Companion.encodeUtf8
import okio.ByteString.Companion.toByteString
import okio.buffer
import okio.sink
import okio.source
import java.io.IOException
import java.net.ConnectException
import java.net.InetSocketAddress
import java.net.ProtocolException
import java.net.Socket
import java.util.*
import kotlin.collections.HashMap

@Service
class WsConnection:WsReader.FrameCallback{

    val key:String
    private var reader:WsReader? = null
    private var writer:WsWriter? = null
    private val random = Random()

    private val request = Request.Builder()
        .url("ws://10.17.221.69:8000/ws?userId=${Owner().userId}")
        .build()

    lateinit var gson: Gson
    lateinit var client: OkHttpClient

    init {
        this.key = ByteArray(16).apply { random.nextBytes(this) }.toByteString().base64()
    }


    fun connect() {

    }


    @Throws(IOException::class)
    internal fun checkUpgradeSuccess(response: Response, exchange: Exchange?) {
        if (response.code != 101) {
            throw ProtocolException(
                "Expected HTTP 101 response but was '${response.code} ${response.message}'")
        }

        val headerConnection = response.header("Connection")
        if (!"Upgrade".equals(headerConnection, ignoreCase = true)) {
            throw ProtocolException(
                "Expected 'Connection' header value 'Upgrade' but was '$headerConnection'")
        }

        val headerUpgrade = response.header("Upgrade")
        if (!"websocket".equals(headerUpgrade, ignoreCase = true)) {
            throw ProtocolException(
                "Expected 'Upgrade' header value 'websocket' but was '$headerUpgrade'")
        }

        val headerAccept = response.header("Sec-WebSocket-Accept")
        val acceptExpected = (key + ACCEPT_MAGIC).encodeUtf8().sha1().base64()
        if (acceptExpected != headerAccept) {
            throw ProtocolException(
                "Expected 'Sec-WebSocket-Accept' header value '$acceptExpected' but was '$headerAccept'")
        }

        if (exchange == null) {
            throw ProtocolException("Web Socket exchange missing: bad interceptor?")
        }
    }


     fun build(host:String,port:Int,url:String,listener: WsConnectionListener,headers:Map<String,String> = HashMap()) {
        val socket = Socket()
         try {
             socket.connect(InetSocketAddress(host,port))
         }catch (e:Exception){
             throw ConnectException("fail to connect to $host:$port")
         }
         reader = WsReader(true,
             socket.source().buffer(),
             this,
             false,
             false)
         writer = WsWriter(true,
             socket.sink().buffer(),
             random,
             false,
             false,
             1024L)
    }






    companion object{
        const val ACCEPT_MAGIC = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11"
    }

    override fun onReadMessage(text: String) {
        TODO("Not yet implemented")
    }

    override fun onReadMessage(bytes: ByteString) {
        TODO("Not yet implemented")
    }

    override fun onReadPing(payload: ByteString) {
        TODO("Not yet implemented")
    }

    override fun onReadPong(payload: ByteString) {
        TODO("Not yet implemented")
    }

    override fun onReadClose(code: Int, reason: String) {
        TODO("Not yet implemented")
    }

}
