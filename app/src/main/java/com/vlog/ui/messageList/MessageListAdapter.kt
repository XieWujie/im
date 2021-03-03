package com.vlog.ui.messageList

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.common.ext.setEmotionText
import com.common.util.Util
import com.vlog.conversation.ConversationActivity
import com.vlog.conversation.MsgConv
import com.vlog.database.Message
import com.vlog.databinding.MessageListItemBinding
import com.vlog.photo.load

class MessageListAdapter:RecyclerView.Adapter<MessageListAdapter.ViewHolder>() {

    private val mList = ArrayList<MsgConv>()

    fun refreshList(list: List<MsgConv>){
        mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()
    }

    fun removeItem(position: Int){
        mList.removeAt(position)
        notifyItemRemoved(position)
    }


    class ViewHolder(val binding:MessageListItemBinding):RecyclerView.ViewHolder(binding.root){

        @SuppressLint("SetTextI18n")
        fun bind(msgConv: MsgConv){
            val time = Util.getTime(msgConv.message.sendTime)
            val content = msgConv.message.content
            when(msgConv.message.messageType){
                Message.MESSAGE_IMAGE->{
                    val index = content.lastIndexOf("/")
                    binding.contentText.text =  "图片：${content.substring(index+1)}"
                }
                Message.MESSAGE_WRITE->{
                    binding.contentText.text  =  "[手写消息]"
                }
                Message.MESSAGE_RECORD->{
                    binding.contentText.text  =  "[语音]"
                }
                Message.MESSAGE_TEXT->{
                    binding.contentText.setEmotionText(content)
                }
                else->{
                    binding.contentText.text = content
                }
            }
            binding.timeText.text = time
            msgConv.friend?.also {
                binding.apply {
                    titleText.text = if(it.markName.isNullOrEmpty()) it.user.username else it.markName
                    avatarView.load(it.user.avatar)
                    root.setOnClickListener {view->
                        ConversationActivity.launch(view.context,it)
                    }
                }
            }
            msgConv.room?.also {
                binding.apply {
                    titleText.text = if(it.markName.isNullOrEmpty()) it.roomName else it.markName
                    avatarView.load(it.roomAvatar)
                    root.setOnClickListener { view->
                        ConversationActivity.launch(view.context,it)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = MessageListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemCount(): Int {
        return mList.size
    }
}