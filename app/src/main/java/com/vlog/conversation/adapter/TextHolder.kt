package com.vlog.conversation.adapter

import android.app.Dialog
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.children
import com.common.ext.launch
import com.common.ext.setEmotionText
import com.common.util.Util
import com.dibus.DiBus
import com.vlog.connect.MessageSend
import com.vlog.database.MsgWithUser
import com.vlog.databinding.LeftTextMessageBinding
import com.vlog.databinding.RightTextMessageBinding
import com.vlog.photo.load
import com.vlog.user.Owner
import com.vlog.user.UserHomeActivity
import com.vlog.user.UserItemEditActivity

class TextHolder{

    fun load(contentView: TextView, avatarView: ImageView, usernameText: TextView, m: MsgWithUser){
        contentView.setEmotionText(m.message.content)
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

    fun getSaveTextActionView(contentText: TextView, dialog: Dialog, viewGroup: GridLayout) =  TextView(
        contentText.context
    ).apply {
        text = "复制"
        addActionView(this,viewGroup)
        setOnClickListener {
            Util.clipText(context, contentText.text.toString())
            dialog.dismiss()
        }
    }


    class L(private val binding: LeftTextMessageBinding):MessageHolder(binding.root){


        private val holder = TextHolder()

        override fun bind(m: MsgWithUser) {
            holder.load(binding.contentText, binding.userAvatarView, binding.usernameText, m)
            binding.contentCard.setLongClick(m.message, m.user){ dialog, v->
                holder.getSaveTextActionView(binding.contentText, dialog, v)
            }
            loadCite(m.message.citeMessageId, binding.citeLayout)
        }
    }

    class R(private val binding: RightTextMessageBinding):MessageHolder(binding.root){

        private val holder = TextHolder()

        override fun bind(m: MsgWithUser) {
            holder.load(binding.contentText, binding.userAvatarView, binding.usernameText, m)
            loadCite(m.message.citeMessageId, binding.citeLayout)
            binding.contentCard.setLongClick(m.message, m.user){ dialog, v->
                 holder.getSaveTextActionView(binding.contentText, dialog, v)
            }
            val msg = m.message
            if(msg.isSend ||msg.createAt != 0L){
                binding.sendIng.visibility = View.GONE
            }else{
                binding.sendIng.visibility = View.VISIBLE
                DiBus.postEvent(msg, MessageSend {
                    if (it != null) {
                        binding.errorState.visibility = View.VISIBLE
                        binding.sendIng.visibility = View.GONE
                    }
                })
            }
        }
    }
}