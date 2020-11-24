package com.vlog.adapter

import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.common.ext.launch
import com.dibus.AutoWire
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vlog.avatar.load
import com.vlog.database.Message
import com.vlog.database.MsgWithUser
import com.vlog.databinding.LeftWriteMessageBinding
import com.vlog.databinding.RightWriteMessageBinding
import com.vlog.conversation.writeMessage.MessageWriteWord
import com.vlog.conversation.writeMessage.WordListLayout
import com.vlog.user.Owner
import com.vlog.user.UserHomeActivity
import com.vlog.user.UserItemEditActivity
import dibus.app.WriteMessageHolderCreator


class WriteMessageHolder{
    @AutoWire
    lateinit var gson: Gson

    init {
        WriteMessageHolderCreator.inject(this)
    }



    fun handleMessage(message: Message, layout:WordListLayout){
        val token = object :TypeToken<List<MessageWriteWord>>(){}.type
        val m = gson.fromJson<List<MessageWriteWord>>(message.content,token)
        layout.receiveWriteEvent(m)
        Log.d("writeSize","size:${m.size}")
    }


    fun load(avatarView:ImageView, usernameText: TextView, m: MsgWithUser){
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

    class LWriteViewHolder(val binding:LeftWriteMessageBinding):MessageHolder(binding.root){

        private val help = WriteMessageHolder()

       override fun bind(m: MsgWithUser) {
           help.load(binding.userAvatarView,binding.usernameText,m)
           help.handleMessage(m.message,binding.wordListLayout)
       }
    }

    class RWriteViewHolder(val binding: RightWriteMessageBinding): MessageHolder(binding.root){
        private val help = WriteMessageHolder()

        override fun bind(m: MsgWithUser) {
            help.load(binding.userAvatarView,binding.usernameText,m)
            help.handleMessage(m.message,binding.wordListLayout)
       }
   }

    companion object{
        fun l(binding:LeftWriteMessageBinding) = LWriteViewHolder(binding)

        fun r(binding: RightWriteMessageBinding) = RWriteViewHolder(binding)
    }
}