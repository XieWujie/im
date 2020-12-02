package com.vlog.conversation.adapter

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.common.ext.launch
import com.common.ext.toast
import com.vlog.conversation.message.MessageService
import com.vlog.conversation.message.MsgCallback
import com.vlog.conversation.message.ProgressListener
import com.vlog.database.Message
import com.vlog.database.MsgWithUser
import com.vlog.databinding.LeftImgMessageBinding
import com.vlog.databinding.RightImgMessageBinding
import com.vlog.photo.load
import com.vlog.user.Owner
import com.vlog.user.UserHomeActivity
import com.vlog.user.UserItemEditActivity
import java.io.IOException

class ImageHolder{

    fun load(contentView:ImageView,avatarView:ImageView,usernameText:TextView,m: MsgWithUser){
        contentView.load(m.message.content)
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


    class L(private val binding:LeftImgMessageBinding):MessageHolder(binding.root){

        private val holder = ImageHolder()

        override fun bind(m: MsgWithUser) {
            holder.load(binding.contentImg,binding.userAvatarView,binding.usernameText,m)
        }
    }

    class R(private val binding:RightImgMessageBinding):MessageHolder(binding.root){

        private val holder =ImageHolder()

        override fun bind(m: MsgWithUser) {
            holder.load(binding.contentImg,binding.userAvatarView,binding.usernameText,m)
            val msg = m.message
            if(msg.isSend){
                binding.sendIng.visibility = View.GONE
                binding.errorState.visibility = View.GONE
            }else {
                binding.sendIng.visibility = View.VISIBLE
                MessageService.sendMessage(itemView.context, msg, object : MsgCallback {
                    override fun callback(message: Message?, e: IOException?) {
                        binding.sendIng.visibility = View.GONE
                        if (e == null) {
                            msg.isSend = true
                        } else {
                            itemView.context.toast(e.message ?: "")
                        }
                    }
                }, object : ProgressListener {

                    override fun callback(
                        contentLength: Long,
                        upLoadLength: Long,
                        isComplete: Boolean
                    ) {
                        Log.d("progress", "contentLength:$contentLength,uploadLength:$upLoadLength")
                    }
                })
            }
        }
    }
}