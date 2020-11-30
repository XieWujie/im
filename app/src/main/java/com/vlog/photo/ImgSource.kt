package com.vlog.photo

import com.common.HOST
import com.common.ext.enqueue
import com.common.ext.sync
import com.dibus.Service
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

@Service
class ImgSource {

    fun upLoad(path: String,callBack:(String)->Unit,onFail:(Exception)->Unit){
        val url = "$HOST/file/post"
        val file = File(path)
        val body = file.asRequestBody("image/png".toMediaTypeOrNull())
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()
        return request.enqueue(callBack,onFail)
    }

    fun upLoad(path: String):String{
        val url = "$HOST/file/post"
        val file = File(path)
        val body = file.asRequestBody("image/png".toMediaTypeOrNull())
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()
        return request.sync()
    }

}