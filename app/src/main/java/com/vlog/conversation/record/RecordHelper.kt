package com.vlog.conversation.record

import android.content.Context
import android.media.MediaRecorder
import com.common.util.ScreenUtils
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class RecordHelper(private val context: Context,) {

    private var mMediaRecorder = MediaRecorder()
    private var isStarted = false
    private var filePath:String? = null

    fun createRecord(){
        val file = getFile(context)
        filePath = file.absolutePath
        isStarted = false
        try {
            mMediaRecorder.apply {
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
            try {
                mMediaRecorder.start()
                isStarted = true
            }catch (i:IllegalStateException){
                i.printStackTrace()
            }catch (e:IOException){
                e.printStackTrace()
            }
        }
    }


    fun pauseRecord(){
//        if(Build.VERSION.SDK_INT>24 && isStarted) {
//            mMediaRecorder.pause()
//            isStarted = false
//        }
    }

    fun showDialog(){
        val width = ScreenUtils.getScreenWidth(context)/2
        val sH = ScreenUtils.getScreenHeight(context)
    }

    fun stopRecord(){
        if(isStarted) {
            try {
                mMediaRecorder.stop()
                mMediaRecorder.reset()
                isStarted = false
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }


    fun getFilePath():String{
        return filePath?:""
    }

    companion object{
        private fun getFile(context: Context):File{
            val fileName = SimpleDateFormat("yyyyMMdd_HH_mm_ss",Locale.CHINA).format(Date()).toString()
            val basePath =   context.cacheDir.absolutePath
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