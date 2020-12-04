package com.vlog.conversation.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.common.ext.launch
import com.dibus.DiBus
import com.vlog.connect.MessageSend
import com.vlog.photo.load
import com.vlog.database.MsgWithUser
import com.vlog.databinding.LeftTextMessageBinding
import com.vlog.databinding.RightTextMessageBinding
import com.vlog.user.Owner
import com.vlog.user.UserHomeActivity
import com.vlog.user.UserItemEditActivity

class TextHolder{

    fun load(contentView:TextView,avatarView:ImageView,usernameText:TextView,m: MsgWithUser){
        contentView.text = m.message.content
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


    class L(private val binding:LeftTextMessageBinding):MessageHolder(binding.root){

        private val holder = TextHolder()

        override fun bind(m: MsgWithUser) {
            holder.load(binding.contentText,binding.userAvatarView,binding.usernameText,m)
            binding.contentCard.setLongClick(m.message,m.user)
        }
    }

    class R(private val binding:RightTextMessageBinding):MessageHolder(binding.root){

        private val holder = TextHolder()

        override fun bind(m: MsgWithUser) {
            holder.load(binding.contentText,binding.userAvatarView,binding.usernameText,m)
            loadCite(m.message.citeMessageId,binding.citeLayout)
            val msg = m.message
            binding.contentCard.setLongClick(m.message,m.user)
            if(msg.isSend ||msg.createAt != 0L){
                binding.sendIng.visibility = View.GONE
            }else{
                binding.sendIng.visibility = View.VISIBLE
                DiBus.postEvent(msg,MessageSend{
                    if(it != null){
                        binding.errorState.visibility = View.VISIBLE
                        binding.sendIng.visibility = View.GONE
                    }
                })
            }
        }
    }
}