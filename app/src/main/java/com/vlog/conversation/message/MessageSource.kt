package com.vlog.conversation.message

import androidx.lifecycle.LiveData
import com.common.HOST
import com.common.Result
import com.common.ext.toLiveData
import com.dibus.AutoWire
import com.dibus.Service
import com.google.gson.Gson
import com.vlog.database.Message
import com.vlog.photo.ProgressRequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody
import java.io.File


@Service
class MessageSource {

    @AutoWire
    lateinit var gson: Gson


    fun postFileMessage(message:Message,file:File,progress:(Long,Long,Boolean)->Unit):LiveData<Result<Message>>{
        val json = gson.toJson(message)
        val fileBody = ProgressRequestBody("image/png".toMediaTypeOrNull(),file,progress)
        val body = MultipartBody.Builder()
            .addFormDataPart("message",json)
            .addFormDataPart("file",file.name,fileBody)
            .build()
        val url = "$HOST/message/fileMsg"
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()
        return request.toLiveData()
    }
}