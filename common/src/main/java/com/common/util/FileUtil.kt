package com.common.util

import com.common.ext.enqueue
import com.dibus.DiBus
import okhttp3.*
import okio.buffer
import okio.sink
import java.io.File
import java.io.IOException

object FileUtil {

    fun saveFile(url:String,savePath:String,onCallback:(Exception?)->Unit){
        val request = Request.Builder()
            .url(url)
            .get()
            .build()
        val okHttpClient: OkHttpClient = DiBus.load()
        okHttpClient.newCall(request).enqueue(object :Callback{
            override fun onFailure(call: Call, e: IOException) {
                onCallback.invoke(e)
            }

            override fun onResponse(call: Call, response: Response) {
                val target = File(savePath).sink().buffer()
               if(response.isSuccessful){
                   try {
                       target.writeAll(response.body!!.source())
                       target.close()
                       onCallback.invoke(null)
                   }catch (e:Exception){
                       onCallback.invoke(e)
                   }finally {
                       target.close()
                   }
               }else{
                   onCallback.invoke(IOException(response.message?:"未知错误"))
               }
            }

        })
    }
}