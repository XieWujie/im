package com.vlog.conversation.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.common.ext.launch
import com.dibus.AutoWire
import com.dibus.DiBus
import com.google.gson.Gson
import com.vlog.connect.MessageSend
import com.vlog.conversation.writeMessage.WordListLayout
import com.vlog.database.MsgWithUser
import com.vlog.databinding.LeftWriteMessageBinding
import com.vlog.databinding.RightWriteMessageBinding
import com.vlog.photo.load
import com.vlog.user.Owner
import com.vlog.user.UserHomeActivity
import com.vlog.user.UserItemEditActivity
import com.vlog.util.onLongClick
import dibus.app.WriteMessageHolderCreator


class WriteMessageHolder{
    @AutoWire
    lateinit var gson: Gson

    init {
        WriteMessageHolderCreator.inject(this)
    }



    fun handleMessage(m: MsgWithUser, layout:WordListLayout){
        layout.handleWrite(m.message.content)
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
           help.handleMessage(m,binding.wordListLayout)
           loadCite(m.message.citeMessageId,binding.citeLayout)
           binding.contentCard.setLongClick(m.message,m.user)
       }
    }

    class RWriteViewHolder(val binding: RightWriteMessageBinding): MessageHolder(binding.root){
        private val help = WriteMessageHolder()

        override fun bind(m: MsgWithUser) {
            help.load(binding.userAvatarView,binding.usernameText,m)
            help.handleMessage(m,binding.wordListLayout)
            val msg = m.message
            loadCite(msg.citeMessageId,binding.citeLayout)
            binding.contentCard.setLongClick(m.message,m.user)
            binding.wordListLayout.onLongClick {
                longClick(binding.contentCard,m.message,m.user)
            }
            if(msg.isSend){
                binding.sendIng.visibility = View.GONE
            }else{
                binding.sendIng.visibility = View.VISIBLE
                DiBus.postEvent(msg, MessageSend{
                    if(it == null){
                        binding.sendIng.visibility = View.GONE
                        msg.isSend = true
                    }else{
                        binding.errorState.visibility = View.VISIBLE
                        binding.sendIng.visibility = View.GONE
                    }
                })
            }
       }
   }

    companion object{
        fun l(binding:LeftWriteMessageBinding) = LWriteViewHolder(binding)

        fun r(binding: RightWriteMessageBinding) = RWriteViewHolder(binding)
    }
}