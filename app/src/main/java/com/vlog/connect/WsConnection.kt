package com.vlog.connect

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import com.common.HOST
import com.dibus.Service
import com.vlog.connect.WsProtocol.OPCODE_TEXT
import okhttp3.internal.readBomAsCharset
import okio.*
import okio.ByteString.Companion.EMPTY
import okio.ByteString.Companion.encodeUtf8
import okio.ByteString.Companion.toByteString
import java.io.Closeable
import java.io.IOException
import java.net.ConnectException
import java.net.InetSocketAddress
import java.net.ProtocolException
import java.net.Socket
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap

@Service
class WsConnection : WsReader.FrameCallback,Closeable {

    private val key: String
    private var reader: WsReader? = null
    private var writer: WsWriter? = null
    private val random = Random()
    private var builder: WsConnectionBuilder? = null
    private var listener: WsConnectionListener? = null
    private val mainHandler = Handler(Looper.getMainLooper())
    private val msgHandler = Handler(HandlerThread("wsMessageQueue").apply {
        start()
    }.looper)

    private var receivedCloseCode = -1


    init {
        this.key = ByteArray(16).apply { random.nextBytes(this) }.toByteString().base64()
    }


    fun connect() {
        val b = builder ?: throw RuntimeException("have no init")
        val socket = Socket()
        try {
            socket.connect(InetSocketAddress(b.host, b.port))
            Log.d(TAG,"connected")
        } catch (e: Exception) {
            e.printStackTrace()
            val newE = ConnectException("fail to connect to ${b.host}:${b.port}")
            listener?.failure(this, newE)
        }
        listener?.open(this)
        val sink = socket.sink().buffer()
        val source = socket.source().buffer()
        try {
            firstRequest(sink, source, b.url, b.headers)
        } catch (e: IOException) {
            e.printStackTrace()
            listener?.failure(this, e)
            return
        }
        reader = WsReader(
            true,
            source,
            this,
            false,
            false
        )
        writer = WsWriter(
            true,
            sink,
            random,
            false,
            false,
            1024L
        )
        socket.soTimeout = 0
        listener?.open(this)
        Executors.newScheduledThreadPool(1).scheduleWithFixedDelay({
            writer?.writePing(EMPTY)
        },20,30,TimeUnit.SECONDS)
        try {
            loopReader()
        }catch (e:IOException){
            e.printStackTrace()
            close()
            listener?.failure(this,e)
        }
    }

    fun writeText(text: String, listener: (e:IOException?)->Unit) {
        if(writer == null){
            listener.invoke(IOException("have not connect to server"))
            return
        }
        val b = text.encodeUtf8()
        msgHandler.post {
            if(writer == null){
                listener.invoke(IOException("have not connect to server"))
                return@post
            }
            var ex:IOException? = null
            try {
                writer?.writeMessageFrame(OPCODE_TEXT, b)
                ex = null
            } catch (e: IOException) {
                e.printStackTrace()
              ex = e
            }
            mainHandler.post {
                listener(ex)
            }
        }
    }



    fun build(
        host: String,
        port: Int,
        url: String,
        listener: WsConnectionListener,
        headers: Map<String, String> = HashMap()
    ) {
        builder = WsConnectionBuilder(host, port, url, headers)
        this.listener = listener
    }

    data class WsConnectionBuilder(
        val host: String,
        val port: Int,
        val url: String,
        val headers: Map<String, String>
    )


    @Throws(IOException::class)
    fun firstRequest(
        sink: BufferedSink,
        source: BufferedSource,
        url: String,
        headers: Map<String, String>
    ) {
        sink.writeUtf8("GET $url HTTP/1.1\n")
        val newHeaders = HashMap<String, String>()
        newHeaders.putAll(headers)
        newHeaders.apply {
            this["Upgrade"] = "websocket"
            this["Connection"] = "Upgrade"
            this["Sec-WebSocket-Key"] = key
            this["Sec-WebSocket-Version"] = "13"
            this["Sec-WebSocket-Extensions"] = "permessage-deflate"
            this["HOST"] = HOST
        }
        for ((key, value) in newHeaders) {
            sink.writeUtf8("$key:$value\n")
        }
        sink.writeUtf8("\n")
        sink.flush()
        val firstLine = source.readUtf8Line()!!.split(" ")
        if (firstLine.size < 3) {
            throw ProtocolException("error response")
        }
        val code = firstLine[1].toInt()
        if (code != 101) {
            throw ProtocolException(
                "Expected HTTP 101 response but was '${firstLine} ${firstLine[2]}'"
            )
        }
        val hs = HashMap<String, String>()
        while(true) {
            val line = source.readUtf8Line()?:break
            val index = line.indexOf(":")
            if (index == -1) break
            val key = line.substring(0, index)
            val value = line.substring(index + 1)
            hs[key] = value
        }
        val headerConnection = hs["Connection"]?.trim()
        if (!"Upgrade".equals(headerConnection, ignoreCase = true)) {
            throw ProtocolException(
                "Expected 'Connection' header value 'Upgrade' but was '$headerConnection'"
            )
        }

        val headerUpgrade = hs["Upgrade"]?.trim()
        if (!"websocket".equals(headerUpgrade, ignoreCase = true)) {
            throw ProtocolException(
                "Expected 'Upgrade' header value 'websocket' but was '$headerUpgrade'"
            )
        }

        val headerAccept = hs["Sec-WebSocket-Accept"]?.trim()
        val acceptExpected = (key + ACCEPT_MAGIC).encodeUtf8().sha1().base64()
        if (acceptExpected != headerAccept) {
            throw ProtocolException(
                "Expected 'Sec-WebSocket-Accept' header value '$acceptExpected' but was '$headerAccept'"
            )
        }
    }

    @Throws(IOException::class)
   private fun loopReader() {
        while (receivedCloseCode == -1) {
            // This method call results in one or more onRead* methods being called on this thread.
            reader!!.processNextFrame()
        }
    }


    companion object {
        const val ACCEPT_MAGIC = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11"
        private const val TAG = "WsConnection"
    }

    override fun onReadMessage(text: String) {
       listener?.onMessage(text)
    }

    override fun onReadMessage(bytes: ByteString) {

    }

    override fun onReadPing(payload: ByteString) {
        msgHandler.post {
            writer?.writePong(EMPTY)
        }
    }

    override fun onReadPong(payload: ByteString) {
        Log.d(TAG,"pong")
    }

    override fun onReadClose(code: Int, reason: String) {
        receivedCloseCode = code
        listener?.onClose(this)
    }


    override fun close() {
        reader?.close()
        writer?.close()
        reader = null
        writer =null
    }

}
