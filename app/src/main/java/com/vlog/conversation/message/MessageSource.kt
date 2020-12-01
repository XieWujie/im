package com.vlog.conversation.message

import android.os.Parcelable
import com.common.HOST_PORT
import com.common.ext.sync
import com.dibus.AutoWire
import com.dibus.Service
import com.google.gson.Gson
import com.vlog.database.Message
import com.vlog.database.MsgDao
import com.vlog.photo.ProgressRequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.Request
import java.io.File
import java.io.IOException


@Service
class MessageSource {

    @AutoWire
    lateinit var gson: Gson

    @AutoWire
    lateinit var msgDao: MsgDao


    @Throws(IOException::class)
    fun postFileMessage(message:Message, file:File, listener:ProgressListener?):Message{
        val json = gson.toJson(message)
        val fileBody = ProgressRequestBody("image/png".toMediaTypeOrNull(),file,listener)
        val body = MultipartBody.Builder()
            .addFormDataPart("message",json)
            .addFormDataPart("file",file.name,fileBody)
            .build()
        val url = "$HOST_PORT/message/fileMsg"
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()
        return request.sync()
    }

}

interface ProgressListener :Parcelable{
    fun callback(contentLength:Long,upLoadLength:Long,isComplete:Boolean)
}

interface MsgCallback:Parcelable{
    fun callback(message:Message?,e:IOException?)
}