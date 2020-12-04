package com.vlog.conversation.message

import android.os.Parcel
import android.os.Parcelable
import com.common.HOST_PORT
import com.common.ext.sync
import com.dibus.AutoWire
import com.dibus.Service
import com.google.gson.Gson
import com.vlog.database.Message
import com.vlog.database.MsgDao
import com.vlog.database.MsgWithUser
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
            .setType("multipart/form-data".toMediaTypeOrNull()!!)
            .build()
        val url = "$HOST_PORT/message/fileMsg"
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()
        return request.sync()
    }

    fun getMsgWithUser(messageId:Int):MsgWithUser{
       return msgDao.getByMessageId(messageId)
    }

}

interface ProgressListener{


    fun callback(contentLength:Long, upLoadLength:Long, isComplete:Boolean)

}

interface MsgCallback{


    fun callback(message:Message?, e:IOException?)

}