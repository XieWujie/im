package com.vlog.conversation.adapter

import android.util.Log
import android.util.LongSparseArray
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.util.containsKey
import androidx.core.util.valueIterator
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
import kotlin.Comparator
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

private const val TAG = "MessageListAdapter"

@Service(createModel = CREATE_PER)
class MessageListAdapter : RecyclerView.Adapter<MessageHolder>() {

    @AutoWire
    lateinit var gson: Gson

    private val mList = ArrayList<MessageWrap>()

    private val temptList = LinkedList<MessageWrap>()

    private val originMap = LongSparseArray<MsgWithUser>()


    private val changeList = ArrayList<MsgWithUser>()
    private val removeList = ArrayList<MsgWithUser>()
    private val insertList= ArrayList<MsgWithUser>()

    fun getFirstItemBefore(): Long {
       return if(mList.isEmpty()) Long.MAX_VALUE else mList[mList.size-1].time
    }


    private val timeSegment = 60 * 3 *1000L


    fun flashList(list: List<MsgWithUser>) {
       if(mList.isEmpty()){
           initList(list)
       }else{
           findDiff(list)
           notifyDataChange()
       }
    }

    fun addBefore(list: List<MsgWithUser>){
        var lastTime = getFirstItemBefore()
        val newList = list.filter { it.message.createAt*1000<lastTime  }
        Log.d(TAG,newList.toString())
        val originSize = mList.size
        for(msg in newList){
            val time = if(msg.message.createAt == 0L){
                msg.message.sendTime
            }else{
                msg.message.createAt*1000
            }
            mList.add(MessageWrap(msg,time))
            if(lastTime-time>timeSegment){
                mList.add(MessageWrap(null,time-1))
                lastTime = time
            }
        }
        if(newList.isNotEmpty() && mList.isNotEmpty() && mList[mList.size-1].message != null){
            mList.add(MessageWrap(null,mList[mList.size-1].time-1))
        }
        notifyItemRangeInserted(originSize,mList.size-originSize)

    }

    private fun initList(newList: List<MsgWithUser>){
        var lastTime = Long.MAX_VALUE
        for(msg in newList){
            val time = if(msg.message.createAt == 0L){
                msg.message.sendTime
            }else{
                msg.message.createAt*1000
            }
            Log.d(TAG,"init${msg.message.createAt*1000-lastTime}")
            mList.add(MessageWrap(msg,time))
            if(lastTime-time>timeSegment){
                mList.add(MessageWrap(null,time-1))
                lastTime = time
            }
        }
        for(msg in newList){
            originMap.put(msg.message.sendTime,msg)
        }
        if(mList.isNotEmpty() && mList[mList.size-1].message != null){
            mList.add(MessageWrap(null,mList[mList.size-1].time-1))
        }
        notifyItemRangeInserted(0,mList.size)
    }

    private fun findDiff(newList: List<MsgWithUser>){
        insertList.clear()
        changeList.clear()
        removeList.clear()
        for(msg in newList){
            val key = msg.message.sendTime
            val originMsg = originMap.get(key)
            if(originMsg == null){
                insertList.add(msg)
            }else{
                if(msg != originMsg){
                    changeList.add(msg)
                }
                originMap.remove(key)
            }
        }
        for(msg in originMap.valueIterator()){
            removeList.add(msg)
        }
        originMap.clear()
        for(msg in newList){
            originMap.put(msg.message.sendTime,msg)
        }
    }



    private fun notifyDataChange(){
        Log.d(TAG,"change:${changeList.toString()}")
        Log.d(TAG,"remove:${removeList.toString()}")
        Log.d(TAG,"insert:${insertList.toString()}")
        if(insertList.isNotEmpty()){
            for(msg in insertList){
                val time = if(msg.message.createAt == 0L){
                    msg.message.sendTime
                }else{
                    msg.message.createAt*1000
                }
               val index =  mList.indexOfFirst {  it.time<time }
                val lastTime = mList.first { it->it.message == null }.time
                Log.d(TAG,"insert:${msg.message.createAt*1000-time}")
                var insertPos = if(index == -1) mList.size else index
                val startPosition = insertPos
                mList.add(insertPos, MessageWrap(msg,time))
                if(time-lastTime>timeSegment){
                    mList.add(++insertPos,MessageWrap(null,time-1))
                }
                notifyItemRangeInserted(startPosition,insertPos-startPosition+1)
            }
        }
        if(changeList.isNotEmpty()){
            for(msg in changeList){
                val index =  mList.indexOfFirst {  it.message!= null && it.message.message.sendTime == msg.message.sendTime }
                if(index == -1){
                    continue
                }else{
                    val time = if(msg.message.createAt == 0L)msg.message.sendTime else msg.message.createAt
                    mList[index] = MessageWrap(msg,time)
                    notifyItemChanged(index)
                }
            }
        }

        if(removeList.isNotEmpty()){
            for(msg in changeList){
                val index =  mList.indexOfFirst {  it.message!= null && it.message.message.sendTime == msg.message.sendTime }
                if(index == -1){
                    continue
                }else{
                   mList.removeAt(index)
                    notifyItemRemoved(index)
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_RIGHT_WRITE -> {
                val binding = RightWriteMessageBinding.inflate(inflater, parent, false)
                WriteMessageHolder.r(binding)
            }
            TYPE_LEFT_WRITE -> {
                val binding = LeftWriteMessageBinding.inflate(inflater, parent, false)
                WriteMessageHolder.l(binding)
            }
            TYPE_RIGHT_TEXT -> {
                val binding = RightTextMessageBinding.inflate(inflater, parent, false)
                TextHolder.R(binding)
            }
            TYPE_LEFT_TEXT -> {
                val binding = LeftTextMessageBinding.inflate(inflater, parent, false)
                TextHolder.L(binding)
            }
            TYPE_TIME -> {
                val view = inflater.inflate(R.layout.cov_time_item, parent, false)
                TimeHolder(view)
            }
            TYPE_LEFT_IMG -> {
                val binding = LeftImgMessageBinding.inflate(inflater, parent, false)
                ImageHolder.L(binding)
            }
            TYPE_RIGHT_IMG -> {
                val binding = RightImgMessageBinding.inflate(inflater, parent, false)
                ImageHolder.R(binding)
            }
            else -> throw RuntimeException("no such type")
        }
    }

    override fun onBindViewHolder(holder: MessageHolder, position: Int) {
        val msg = mList[position]
        if (msg.message != null) {
            holder.bind(msg.message)
        } else {
            holder.bindTime(Util.getTime(msg.time))
        }
    }

    override fun getItemViewType(position: Int): Int {

        val message = mList[position].message?.message ?: return TYPE_TIME
        val ownerId = Owner().userId
        return if (message.sendFrom == ownerId && message.messageType == Message.MESSAGE_WRITE) {
            TYPE_RIGHT_WRITE
        } else if (message.sendFrom != ownerId && message.messageType == Message.MESSAGE_WRITE) {
            TYPE_LEFT_WRITE
        } else if (message.sendFrom == ownerId && message.messageType == Message.MESSAGE_TEXT) {
            TYPE_RIGHT_TEXT
        } else if (message.sendFrom != ownerId && message.messageType == Message.MESSAGE_TEXT) {
            TYPE_LEFT_TEXT
        } else if (message.sendFrom == ownerId && message.messageType == Message.MESSAGE_IMAGE) {
            TYPE_RIGHT_IMG
        } else {
            TYPE_LEFT_IMG
        }
    }

    override fun getItemCount(): Int {
        return mList.size + temptList.size
    }


    companion object {
        private const val TYPE_LEFT_WRITE = 0
        private const val TYPE_RIGHT_WRITE = 1
        private const val TYPE_LEFT_TEXT = 2
        private const val TYPE_RIGHT_TEXT = 3
        private const val TYPE_RIGHT_IMG = 4
        private const val TYPE_LEFT_IMG = 5
        private const val TYPE_TIME = 11
        private val TAG = "MessageListAdapter"

    }

    data class MessageWrap(val message: MsgWithUser?, val time: Long)

}