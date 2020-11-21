package com.vlog.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dibus.*
import com.google.gson.Gson
import com.vlog.database.Message
import com.vlog.database.MsgWithUser
import com.vlog.user.Owner
import com.vlog.databinding.LeftTextMessageBinding
import com.vlog.databinding.LeftWriteMessageBinding
import com.vlog.databinding.RightTextMessageBinding
import com.vlog.databinding.RightWriteMessageBinding
import java.util.*

private const val TAG = "MessageListAdapter"

@Service
class MessageListAdapter :RecyclerView.Adapter<MessageHolder>(){

    @AutoWire
    lateinit var gson: Gson

    private val mList = LinkedList<MsgWithUser>()



    private fun addBefore(list:List<MsgWithUser>){
        val first = mList.first.message.createAt
        val newList = list.filter { it.message.createAt<first }
        mList.addAll(0,newList)
        notifyItemRangeInserted(0,newList.size)
    }

    fun getFirstItemBefore() = if(mList.isEmpty()) Long.MAX_VALUE else mList.first.message.createAt

    @BusEvent
    fun newMessage(mesWithUser: MsgWithUser){
        mList.addLast(mesWithUser)
        notifyItemInserted(mList.size-1)
    }

    fun flashList(list: List<MsgWithUser>){
        if(list.isEmpty()){
            mList.clear()
            notifyDataSetChanged()
            return
        }
        if(mList.isEmpty()){
            mList.addAll(list)
            notifyDataSetChanged()
            return
        }
        addBefore(list)
        addLast(list)
    }

    private fun addLast(list: List<MsgWithUser>){
        val last = mList.last.message.createAt
        val newList = list.filter { it.message.createAt> last }
        val startPos = mList.size
        mList.addAll(startPos,newList)
        notifyItemRangeInserted(startPos,newList.size)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageHolder {
        val inflater = LayoutInflater.from(parent.context)
       return when(viewType){
            TYPE_RIGHT_WRITE->{
                val binding = RightWriteMessageBinding.inflate(inflater,parent,false)
               WriteMessageHolder.r(binding)
            }
           TYPE_LEFT_WRITE->{
               val binding = LeftWriteMessageBinding.inflate(inflater,parent,false)
              WriteMessageHolder.l(binding)
           }
           TYPE_RIGHT_TEXT->{
               val binding = RightTextMessageBinding.inflate(inflater,parent,false)
               TextHolder.R(binding)
           }
           else->{
               val binding = LeftTextMessageBinding.inflate(inflater,parent,false)
               TextHolder.L(binding)
           }
        }
    }

    override fun onBindViewHolder(holder: MessageHolder, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemViewType(position: Int): Int {
        val message = mList[position].message
        val ownerId = Owner().userId
         return if(message.sendFrom == ownerId && message.messageType == Message.MESSAGE_WRITE){
            TYPE_RIGHT_WRITE
        }else if(message.sendFrom != ownerId && message.messageType == Message.MESSAGE_WRITE){
            TYPE_LEFT_WRITE
        }else if(message.sendFrom == ownerId && message.messageType == Message.MESSAGE_TEXT){
             TYPE_RIGHT_TEXT
         }else{
             TYPE_LEFT_TEXT
         }
    }

    override fun getItemCount(): Int {
        return mList.size
    }



    companion object{
        private const val TYPE_LEFT_WRITE = 0
        private const val TYPE_RIGHT_WRITE = 1
        private const val TYPE_LEFT_TEXT = 2
        private const val TYPE_RIGHT_TEXT = 3
    }
}