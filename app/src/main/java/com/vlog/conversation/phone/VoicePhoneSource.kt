package com.vlog.conversation.phone

import androidx.lifecycle.LiveData
import com.common.HOST_PORT
import com.common.Result
import com.common.ext.sync
import com.common.ext.toLiveData
import com.dibus.Service
import okhttp3.Request
import java.io.IOException
import kotlin.Throws

@Service
class VoicePhoneSource {

    @Throws(IOException::class)
    fun fetchNetAddressInfo(userId:Int):NetAddress{
        val url = "$HOST_PORT/user/netAddress?userId=$userId"
        val request = Request.Builder()
            .url(url)
            .get()
            .build()
        return request.sync()
    }

}

data class NetAddress(val host:String,val port:Int)