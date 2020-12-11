package com.vlog.ui.messageList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
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

        fun bind(msgConv: MsgConv){
            val time = Util.getTime(msgConv.message.sendTime)
            binding.contentText.text = when(msgConv.message.messageType){
                Message.MESSAGE_IMAGE->{
                    val content = msgConv.message.content
                    val index = content.lastIndexOf("/")
                    "图片：${content.substring(index+1)}"
                }
                Message.MESSAGE_WRITE->{
                    "手写消息"
                }
                else->{
                    msgConv.message.content
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
                    titleText.text = it.roomName
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