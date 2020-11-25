package com.vlog.ui.messageList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.common.Util
import com.vlog.avatar.load
import com.vlog.conversation.ConversationActivity
import com.vlog.conversation.MsgConv
import com.vlog.databinding.MessageListItemBinding

class MessageListAdapter:RecyclerView.Adapter<MessageListAdapter.ViewHolder>() {

    private val mList = ArrayList<MsgConv>()

    fun refreshList(list: List<MsgConv>){
        mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()
    }


    class ViewHolder(val binding:MessageListItemBinding):RecyclerView.ViewHolder(binding.root){

        fun bind(msgConv: MsgConv){
            binding.apply {

            }
            val time = Util.getTime(msgConv.message.createAt)
            binding.contentText.text = msgConv.message.content
            binding.timeText.text = time
            msgConv.friend?.also {
                binding.apply {
                    titleText.text = it.user.username
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