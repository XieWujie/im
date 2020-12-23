package com.vlog.conversation.adapter

import android.animation.ValueAnimator
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.common.HOST_PORT
import com.common.ext.animateEnd
import com.common.ext.toast
import com.common.pushMainThread
import com.common.util.FileUtil
import com.vlog.conversation.message.MessageService
import com.vlog.conversation.message.MsgCallback
import com.vlog.conversation.message.ProgressListener
import com.vlog.conversation.record.CirclePlayBar
import com.vlog.conversation.record.RecordPlayView
import com.vlog.database.Message
import com.vlog.database.MsgWithUser
import com.vlog.database.User
import com.vlog.databinding.LeftRecordMessageBinding
import com.vlog.databinding.RightRecordMessageBinding
import com.vlog.photo.load
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class RecordHolder {


    fun fillData(nameText: TextView, avatarView: ImageView, user: User) {
        nameText.text = user.username
        avatarView.load(user.avatar)
    }


    class L(private val binding: LeftRecordMessageBinding) : MessageHolder(binding.root) {

        private val recordHolder = RecordHolder()

        override fun bind(message: MsgWithUser) {
            super.bind(message)
            recordHolder.fillData(binding.usernameText, binding.userAvatarView, message.user)
            loadCite(message.message.citeMessageId, binding.citeLayout)
            b(message.message.content, binding.timeText, binding.recordPlayView)
            binding.contentCard.setLongClick(message.message, message.user)
        }
    }

    class R(private val binding: RightRecordMessageBinding) : MessageHolder(binding.root) {

        private val recordHolder = RecordHolder()

        override fun bind(message: MsgWithUser) {
            super.bind(message)
            recordHolder.fillData(binding.usernameText, binding.userAvatarView, message.user)
            loadCite(message.message.citeMessageId, binding.citeLayout)
            b(message.message.content, binding.timeText, binding.recordPlayView)
            binding.contentCard.setLongClick(message.message, message.user)
            val msg = message.message
            if (msg.isSend) {
                binding.sendIng.visibility = View.GONE
                binding.errorState.visibility = View.GONE
            } else {
                binding.sendIng.visibility = View.VISIBLE
                MessageService.sendMessage(itemView.context, msg, object : MsgCallback {
                    override fun callback(message: Message?, e: IOException?) {
                        binding.sendIng.visibility = View.GONE
                        if (e != null) {
                            e.printStackTrace()
                            itemView.context.toast(e.message ?: "")
                            binding.errorState.visibility = View.VISIBLE
                        } else {
                            msg.isSend = true
                        }
                    }
                }, object : ProgressListener {

                    override fun callback(
                        contentLength: Long,
                        upLoadLength: Long,
                        isComplete: Boolean
                    ) {
                        pushMainThread {
                            val progress = 100 * upLoadLength.toFloat() / contentLength.toFloat()
                            val text = progress.roundToInt()

                            if (isComplete) {

                            }
                        }

                    }
                })
            }
        }

    }

    companion object {

        private fun playPrepare(source: String,timeText: TextView,recordPlayView: CirclePlayBar){
            val mediaPlayer = MediaPlayer()
            try {
                mediaPlayer.setDataSource(source)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("playPath", e.message ?: "")
                return
            }
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                val animate = ValueAnimator.ofInt(0, mediaPlayer.duration)
                recordPlayView.setAllTime(mediaPlayer.duration)
                animate.duration = mediaPlayer.duration.toLong()
                animate.addUpdateListener {
                    val value = it.animatedValue as Int
                    recordPlayView.setTime(value)
                }
                timeText.text = formatDuration(mediaPlayer.duration.toLong())
                recordPlayView.setOnClickListener {
                    mediaPlayer.start()
                    animate.start()
                    recordPlayView.setIsPlaying(true)
                }
                animate.animateEnd {
                    recordPlayView.setTime(0)
                    recordPlayView.setIsPlaying(false)
                }
            }
        }
        fun b(source: String, timeText: TextView, recordPlayView: CirclePlayBar) {
             if(source.startsWith("/file/get")){
                val realUrl = "$HOST_PORT$source"
                val fileName = source.substring(source.indexOfLast { it == '/' }+1)
                val context = timeText.context
                val basePath =   context.cacheDir.absolutePath
                val path = File("$basePath/record")
                val file = File("$basePath/record/$fileName")
                try {
                    if(!path.exists() || !file.exists()){
                        path.mkdirs()
                        file.createNewFile()
                        FileUtil.saveFile(realUrl,"$path/$fileName"){
                            pushMainThread {
                                if(it == null){
                                    playPrepare(file.absolutePath,timeText,recordPlayView)
                                }else{
                                    playPrepare(realUrl,timeText,recordPlayView)
                                }
                            }
                        }
                    }else{
                        playPrepare(file.absolutePath,timeText,recordPlayView)
                    }
                }catch (e:Exception){
                    playPrepare(source,timeText,recordPlayView)
                }
            }else{
                 playPrepare(source,timeText,recordPlayView)
             }
        }

        private fun formatDuration(duration: Long): String {
            val second = duration / 1000
            val million = duration / 100 - second * 10
            return "$second″$million"

        }
    }
}