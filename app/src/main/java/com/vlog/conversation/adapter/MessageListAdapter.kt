package com.vlog.conversation.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.common.util.Util
import com.dibus.AutoWire
import com.dibus.CREATE_PER
import com.dibus.Service
import com.google.gson.Gson
import com.vlog.R
import com.vlog.database.Message
import com.vlog.database.MsgWithUser
import com.vlog.databinding.*
import com.vlog.user.Owner
import java.util.*
import kotlin.collections.ArrayList

private const val TAG = "MessageListAdapter"

@Service(createModel = CREATE_PER)
class MessageListAdapter :RecyclerView.Adapter<MessageHolder>(){

    @AutoWire
    lateinit var gson: Gson

    private val mList = ArrayList<MessageWrap>()

    private val temptList = LinkedList<MsgWithUser>()


    fun getFirstItemBefore():Long{
        for(msg in mList){
            if(msg.message != null){
                return msg.message!!.message.createAt
            }
        }
        return Long.MAX_VALUE
    }




    private val haftHour = 60*3*1000
    private var lastTime = 0L

    fun flashList(list: List<MsgWithUser>){
        Log.d("list",list.toString())
        val lastSize = mList.size
        val midTime = if(mList.isEmpty()) 0 else mList[mList.size-1].message!!.message.sendTime
        var i = 0
        while(i<list.size){
            val time = list[i].message.sendTime
            if(time<=midTime){
                i++
                continue
            }
            val m:MessageWrap
            if(time-lastTime>haftHour){
                m = MessageWrap(null, Util.getTime(time))
                lastTime = time
            }else{
                 m = MessageWrap(list[i],null)
                i++
            }
            mList.add(m)
        }
        notifyItemRangeInserted(lastSize,mList.size-lastSize)
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
           TYPE_LEFT_TEXT->{
               val binding = LeftTextMessageBinding.inflate(inflater,parent,false)
               TextHolder.L(binding)
           }
           TYPE_TIME->{
               val view = inflater.inflate(R.layout.cov_time_item,parent,false)
               TimeHolder(view)
           }
           TYPE_LEFT_IMG->{
               val binding = LeftImgMessageBinding.inflate(inflater,parent,false)
               ImageHolder.L(binding)
           }
           TYPE_RIGHT_IMG->{
               val binding = RightImgMessageBinding.inflate(inflater,parent,false)
               ImageHolder.R(binding)
           }
           else->throw RuntimeException("no such type")
        }
    }

    override fun onBindViewHolder(holder: MessageHolder, position: Int) {
        val msg = mList[position]
        if(msg.message != null){
            holder.bind(msg.message)
        }else{
            holder.bindTime(msg.time?:Timer().toString())
        }
    }

    override fun getItemViewType(position: Int): Int {

        val message = mList[position].message?.message?:return TYPE_TIME
        val ownerId = Owner().userId
         return if(message.sendFrom == ownerId && message.messageType == Message.MESSAGE_WRITE){
            TYPE_RIGHT_WRITE
        }else if(message.sendFrom != ownerId && message.messageType == Message.MESSAGE_WRITE){
            TYPE_LEFT_WRITE
        }else if(message.sendFrom == ownerId && message.messageType == Message.MESSAGE_TEXT){
             TYPE_RIGHT_TEXT
         }else if(message.sendFrom != ownerId && message.messageType == Message.MESSAGE_TEXT){
             TYPE_LEFT_TEXT
         }else if(message.sendFrom == ownerId && message.messageType == Message.MESSAGE_IMAGE){
             TYPE_RIGHT_IMG
         }else{
             TYPE_LEFT_IMG
         }
    }

    override fun getItemCount(): Int {
        return mList.size+temptList.size
    }



    companion object{
        private const val TYPE_LEFT_WRITE = 0
        private const val TYPE_RIGHT_WRITE = 1
        private const val TYPE_LEFT_TEXT = 2
        private const val TYPE_RIGHT_TEXT = 3
        private const val TYPE_RIGHT_IMG = 4
        private const val TYPE_LEFT_IMG = 5
        private const val TYPE_TIME = 11

    }

    data class MessageWrap(val message:MsgWithUser?,val time:String?)

}