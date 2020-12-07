package com.vlog.conversation.adapter

import android.widget.ImageView
import android.widget.TextView
import com.common.ext.launch
import com.vlog.database.MsgWithUser
import com.vlog.databinding.LeftWithdrawMessageBinding
import com.vlog.databinding.RightWithdrawMessageBinding
import com.vlog.photo.load
import com.vlog.user.Owner
import com.vlog.user.UserHomeActivity
import com.vlog.user.UserItemEditActivity

class WithDrawHolder {


    fun load(contentView: TextView, avatarView: ImageView, usernameText: TextView, m: MsgWithUser){
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

    class L(private val binding:LeftWithdrawMessageBinding):MessageHolder(binding.root){


        private val holder = WithDrawHolder()

        override fun bind(m: MsgWithUser) {
            holder.load(binding.contentText,binding.userAvatarView,binding.usernameText,m)
        }


    }

    class R(private val binding:RightWithdrawMessageBinding):MessageHolder(binding.root){
        private val holder = WithDrawHolder()

        override fun bind(m: MsgWithUser) {
            holder.load(binding.contentText, binding.userAvatarView, binding.usernameText, m)
        }
    }
}