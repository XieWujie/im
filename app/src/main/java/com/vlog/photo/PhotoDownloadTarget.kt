package com.vlog.photo

import android.content.Context
import android.os.Environment
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.common.ext.toast
import com.common.pushMainThread
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class PhotoDownloadTarget(private val context: Context):SimpleTarget<File>() {

    private lateinit var savePath:String

    init {
       val basePath = if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED){
           Environment.getExternalStorageDirectory().absolutePath
       }else{
           context.externalCacheDir?.absolutePath
       }
        savePath = "$basePath/avlog/图片"
    }


    override fun onResourceReady(resource: File, transition: Transition<in File>?) {
        val file = File(savePath, resource.name)
        if(!file.exists()){
            try {
                file.createNewFile()
            }catch (e:IOException){
                e.printStackTrace()
                pushMainThread {
                    context.toast("保存失败:${e.message}")
                }
            }
        }
        copy(file,resource)
        context.toast("保存成功")
    }

    fun copy(source: File?, target: File?) {
        var fileInputStream: FileInputStream? = null
        var fileOutputStream: FileOutputStream? = null
        try {
            fileInputStream = FileInputStream(source)
            fileOutputStream = FileOutputStream(target)
            val buffer = ByteArray(1024)
            while (fileInputStream.read(buffer) > 0) {
                fileOutputStream.write(buffer)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close()
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

}