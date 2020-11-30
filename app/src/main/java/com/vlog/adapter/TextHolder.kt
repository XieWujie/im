package com.vlog.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.common.ext.launch
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
        }
    }

    class R(private val binding:RightTextMessageBinding):MessageHolder(binding.root){

        private val holder = TextHolder()

        override fun bind(m: MsgWithUser) {
            holder.load(binding.contentText,binding.userAvatarView,binding.usernameText,m)
            if(m.message.messageId == 0){
                binding.sendIng.visibility = View.VISIBLE
            }else{
                binding.sendIng.visibility = View.GONE
            }
        }
    }
}