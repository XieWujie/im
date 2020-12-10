package com.vlog.conversation.adapter

import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.common.ext.launch
import com.common.ext.toast
import com.common.pushMainThread
import com.common.util.Util
import com.vlog.App
import com.vlog.conversation.message.MessageService
import com.vlog.conversation.message.MsgCallback
import com.vlog.conversation.message.ProgressListener
import com.vlog.database.Message
import com.vlog.database.MsgWithUser
import com.vlog.databinding.LeftImgMessageBinding
import com.vlog.databinding.RightImgMessageBinding
import com.vlog.photo.load
import com.vlog.photo.loadWithMaxSize
import com.vlog.photo.showBigView
import com.vlog.user.Owner
import com.vlog.user.UserHomeActivity
import com.vlog.user.UserItemEditActivity
import java.io.IOException
import kotlin.math.roundToInt

class ImageHolder {

    val dp200 = Util.dp2dx(App.get(),200).toInt()

    fun load(
        contentView: ImageView,
        avatarView: ImageView,
        usernameText: TextView,
        m: MsgWithUser
    ) {
        val width = Util.dp2dx(contentView.context, 200).toInt()
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        val l = contentView.layoutParams.apply {
            this.width = width
            this.height = height
        }
        contentView.layoutParams = l
        contentView.loadWithMaxSize(m.message.content,dp200)
        contentView.setOnClickListener {
            contentView.showBigView(m.message.content)
        }
        contentView.setLongClick(m.message, m.user)
        avatarView.load(m.user.avatar)
        usernameText.text = m.user.username
        avatarView.setOnClickListener {
            if (m.user.userId != Owner().userId) {
                UserHomeActivity.launch(it.context, m.user)
            } else {
                it.context.launch<UserItemEditActivity>()
            }
        }
    }


    class L(private val binding: LeftImgMessageBinding) : MessageHolder(binding.root) {

        private val holder = ImageHolder()

        override fun bind(m: MsgWithUser) {
            holder.load(binding.contentImg, binding.userAvatarView, binding.usernameText, m)
            val msg = m.message
            loadCite(msg.citeMessageId, binding.citeLayout)
        }
    }

    class R(private val binding: RightImgMessageBinding) : MessageHolder(binding.root) {

        private val holder = ImageHolder()

        override fun bind(m: MsgWithUser) {
            holder.load(binding.contentImg, binding.userAvatarView, binding.usernameText, m)
            val msg = m.message
            loadCite(msg.citeMessageId, binding.citeLayout)
            if (msg.isSend) {
                binding.progressView.visibility = View.GONE
            } else {
                binding.progressView.visibility = View.VISIBLE
                binding.contentImg.post {
                    binding.progressView.layoutParams= binding.contentImg.layoutParams
                }
                binding.progressView.setProgress(0)
                MessageService.sendMessage(itemView.context, msg, object : MsgCallback {
                    override fun callback(message: Message?, e: IOException?) {
                        if (e != null) {
                            e.printStackTrace()
                            itemView.context.toast(e.message ?: "")
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
                            binding.progressView.setProgress(text)
                            if(isComplete){
                                binding.progressView.visibility = View.GONE
                            }
                        }

                    }
                })
            }
        }
    }
}