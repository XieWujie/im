package com.vlog.conversation.record

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.os.Environment
import android.os.FileUtils
import android.util.Log
import androidx.annotation.RequiresApi
import com.common.pushExecutors
import io.reactivex.internal.schedulers.IoScheduler
import java.io.File
import java.io.IOException
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import java.text.SimpleDateFormat
import java.util.*

class RecordHelper(private val context: Context,) {

    private var mMediaRecorder:MediaRecorder? = null
    private var isStarted = false
    private var filePath:String? = null

    fun createRecord(){
        val file = getFile(context)
        filePath = file.absolutePath
        try {
            mMediaRecorder ?: MediaRecorder().also { mMediaRecorder = it }.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.AMR_NB)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                setOutputFile(file.absolutePath)
                prepare()
            }
        }catch (i:IllegalStateException){
            i.printStackTrace()
        }catch (e:IOException){
            e.printStackTrace()
        }
    }

    fun start(){
        if(!isStarted) {
            mMediaRecorder?.apply {
                try {
                    start()
                    isStarted = true
                }catch (i:IllegalStateException){
                    i.printStackTrace()
                }catch (e:IOException){
                    e.printStackTrace()
                }
            }
        }
    }


    fun pauseRecord(){
        if(Build.VERSION.SDK_INT>24 && isStarted) {
            mMediaRecorder?.pause()
            isStarted = false
        }
    }

    fun stopRecord(){
        if(isStarted) {
            mMediaRecorder?.apply {
                stop()
                release()
                isStarted = false
            }
        }
    }


    fun getFilePath():String{
        return filePath?:""
    }

    companion object{
        private fun getFile(context: Context):File{
            val fileName = SimpleDateFormat("yyyyMMdd_HH_mm_ss",Locale.CHINA).format(Date()).toString()
            val basePath =  if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED){
                Environment.getExternalStorageDirectory().absolutePath
            }else{
                context.externalCacheDir?.absolutePath!!
            }
            val path = File("$basePath/record")
            try {
                if(!path.exists()){
                    path.mkdirs()
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
            val file = File("$basePath/$fileName.3gp")
            try {
                if(!file.exists()){
                    file.createNewFile()
                }
            }catch (e:IOException){
                e.printStackTrace()
            }
            return file
        }
    }

}