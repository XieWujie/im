package com.vlog.conversation.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.common.ext.launch
import com.vlog.photo.load
import com.vlog.database.MsgWithUser
import com.vlog.databinding.LeftImgMessageBinding
import com.vlog.databinding.RightImgMessageBinding
import com.vlog.user.Owner
import com.vlog.user.UserHomeActivity
import com.vlog.user.UserItemEditActivity

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
            if(m.message.messageId == 0){
                binding.sendIng.visibility = View.VISIBLE
            }else{
                binding.sendIng.visibility = View.GONE
            }
        }
    }
}