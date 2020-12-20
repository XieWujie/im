package com.vlog.conversation.adapter

import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.widget.TextView
import com.vlog.conversation.record.RecordPlayView
import com.vlog.database.MsgWithUser
import com.vlog.databinding.RightRecordMessageBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class RecordHolder() {


    private fun formatDuration(duration:Long):String{
        return SimpleDateFormat("mm:ss SSS", Locale.CHINA).format(duration)
    }


    fun b(mediaPlayer: MediaPlayer,timeText:TextView,recordPlayView: RecordPlayView){
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            timeText.text =formatDuration(mediaPlayer.duration.toLong())
            recordPlayView.setOnClickListener {
                recordPlayView.startPlay(mediaPlayer.duration.toLong())
                mediaPlayer.start()
            }
        }
    }

    class R(private val binding:RightRecordMessageBinding):MessageHolder(binding.root){

        private val recordHolder = RecordHolder()

        override fun bind(message: MsgWithUser) {
            super.bind(message)
            Log.d("playPath",message.message.content)
            val mediaPlayer =  MediaPlayer();
            try {
                mediaPlayer.setDataSource(message.message.content)
            }catch (e:Exception){
                e.printStackTrace()
                return
            }
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            recordHolder.b(mediaPlayer,binding.timeText,binding.recordPlayView)
        }
    }
}