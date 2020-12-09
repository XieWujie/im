package com.vlog.conversation.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.common.util.Util
import com.dibus.*
import com.google.gson.Gson
import com.vlog.R
import com.vlog.conversation.MessageChangeEvent
import com.vlog.conversation.MessageRemoveEvent
import com.vlog.database.Message
import com.vlog.database.MsgWithUser
import com.vlog.databinding.*
import com.vlog.user.Owner
import kotlin.collections.ArrayList

private const val TAG = "MessageListAdapter"

@Service(createModel = CREATE_PER)
class MessageListAdapter : RecyclerView.Adapter<MessageHolder>() {

    @AutoWire
    lateinit var gson: Gson

    private val mList = ArrayList<MessageWrap>()

    fun getFirstItemBefore(): Long {
        return if (mList.isEmpty()) Long.MAX_VALUE else mList[mList.size - 1].time
    }


    private val timeSegment = 60 * 3 * 1000L


    fun flashList(list: List<MsgWithUser>) {
        if (mList.isEmpty()) {
            initList(list)
        }else{
            mList.clear()
            initList(list)
        }
    }

    fun addBefore(list: List<MsgWithUser>) {
        var lastTime = getFirstItemBefore()
        val newList = list.filter { it.message.createAt * 1000 < lastTime }
        val originSize = mList.size
        for (msg in newList) {
            val time = if (msg.message.createAt == 0L) {
                msg.message.sendTime
            } else {
                msg.message.createAt * 1000
            }

            mList.add(MessageWrap(msg, time))
            if (lastTime - time > timeSegment) {
                mList.add(MessageWrap(null, time - 1))
                lastTime = time
            }
        }
        if (newList.isNotEmpty() && mList.isNotEmpty() && mList[mList.size - 1].message != null) {
            mList.add(MessageWrap(null, mList[mList.size - 1].time - 1))
        }
        notifyItemRangeInserted(originSize, mList.size - originSize)

    }

    private fun initList(newList: List<MsgWithUser>) {
        var lastTime = Long.MAX_VALUE
        for (msg in newList) {
            val time = if (msg.message.createAt == 0L) {
                msg.message.sendTime
            } else {
                msg.message.createAt * 1000
            }
            mList.add(MessageWrap(msg, time))
            if (lastTime - time > timeSegment) {
                mList.add(MessageWrap(null, time - 1))
                lastTime = time
            }
        }
        if (mList.isNotEmpty() && mList[mList.size - 1].message != null) {
            mList.add(MessageWrap(null, mList[mList.size - 1].time - 1))
        }
        notifyItemRangeInserted(0, mList.size)
    }

    @BusEvent(threadPolicy = THREAD_POLICY_MAIN)
    fun messageChange(event: MessageChangeEvent){
        val msg = event.msg.message
        val index =
            mList.indexOfFirst { it.message != null && it.message.message.sendTime == msg.sendTime }
        if (index != -1) {
            val time =
                if (msg.createAt == 0L) msg.sendTime else msg.createAt
            mList[index] = MessageWrap(event.msg, time)
            notifyItemChanged(index)
        }
    }

    fun messageInsert(msgs: List<MsgWithUser>){
        val firstTime =
            try {
                mList.first { it.message != null }.message?.message?.sendTime?:-1L
            }catch (e:Exception){
                0L
            }
        val newMsg = msgs.filter { it.message.sendTime>firstTime }
        for(msg in newMsg) {
            val time = if (msg.message.createAt == 0L) {
                msg.message.sendTime
            } else {
                msg.message.createAt * 1000
            }
            val index = mList.indexOfFirst { it.time < time }
            Log.d(TAG, "index:$index")
            val lastTime = try {
                mList.first { it -> it.message == null }.time
            }catch (e:Exception){
                0L
            }
            var insertPos = if (index == -1) mList.size else index
            val startPosition = insertPos
            mList.add(insertPos, MessageWrap(msg, time))
            if (time - lastTime > timeSegment) {
                mList.add(++insertPos, MessageWrap(null, time - 1))
            }
            notifyItemRangeInserted(startPosition, insertPos - startPosition + 1)
        }
    }



    @BusEvent(threadPolicy = THREAD_POLICY_MAIN)
    fun messageRemove(event: MessageRemoveEvent){
        val sendTime = event.msg.sendTime
        val index =
            mList.indexOfFirst { it.message != null && it.message.message.sendTime ==  sendTime }
        if (index != -1) {
            mList.removeAt(index)
            if(index != mList.size-1 && mList[index].message == null){
                mList.removeAt(index)
                notifyItemRangeRemoved(index,2)
            }else{
                notifyItemRemoved(index)
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
            TYPE_LEFT_WITHDRAW->{
                val binding = LeftWithdrawMessageBinding.inflate(inflater,parent,false)
                WithDrawHolder.L(binding)
            }
            TYPE_RIGHT_WITHDRAW->{
                val binding = RightWithdrawMessageBinding.inflate(inflater,parent,false)
                WithDrawHolder.R(binding)
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
        return if (message.sendFrom == ownerId) {
            when (message.messageType) {
                Message.MESSAGE_IMAGE -> TYPE_RIGHT_IMG
                Message.MESSAGE_TEXT -> TYPE_RIGHT_TEXT
                Message.MESSAGE_WRITE -> TYPE_RIGHT_WRITE
                Message.MESSAGE_WITHDRAW -> TYPE_RIGHT_WITHDRAW
                else -> TYPE_RIGHT_TEXT
            }
        } else {
            when (message.messageType) {
                Message.MESSAGE_IMAGE -> TYPE_LEFT_IMG
                Message.MESSAGE_TEXT -> TYPE_LEFT_TEXT
                Message.MESSAGE_WRITE -> TYPE_LEFT_WRITE
                Message.MESSAGE_WITHDRAW -> TYPE_LEFT_WITHDRAW
                else -> TYPE_RIGHT_TEXT
            }
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }


    companion object {
        private const val TYPE_LEFT_WRITE = 0
        private const val TYPE_RIGHT_WRITE = 1
        private const val TYPE_LEFT_TEXT = 2
        private const val TYPE_RIGHT_TEXT = 3
        private const val TYPE_RIGHT_IMG = 4
        private const val TYPE_LEFT_IMG = 5
        private const val TYPE_TIME = 11
        private const val TYPE_LEFT_WITHDRAW = 6
        private const val TYPE_RIGHT_WITHDRAW = 7
        private const val TAG = "MessageListAdapter"

    }

    data class MessageWrap(val message: MsgWithUser?, val time: Long)

}