package com.vlog.conversation.writeMessage

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dibus.AutoWire
import com.dibus.BusEvent
import com.dibus.DiBus
import com.dibus.THREAD_POLICY_MAIN
import com.google.gson.Gson
import com.vlog.database.Message
import com.vlog.database.MsgWithUser
import com.vlog.user.Owner
import com.vlog.adapter.WordWriteAdapter
import com.vlog.connect.MessageSend
import com.vlog.conversation.writeMessage.event.SingleWriteEvent
import com.vlog.conversation.writeMessage.event.WordCacheState
import com.vlog.database.User
import dibus.app.FinalReadViewListCreator

private const val TAG = "FinalReadViewList"
class FinalReadViewList:RecyclerView {

    private val list = ArrayList<MessageWriteWord>()
    private val mAdapter = WordWriteAdapter(list)

    var conversationId = -1


    @AutoWire
    lateinit var gson: Gson

    constructor(context: Context):super(context)

    constructor(context: Context,attributeSet: AttributeSet):super(context,attributeSet)




    init {
        FinalReadViewListCreator.inject(this)
        this.adapter = mAdapter
        layoutManager =LinearLayoutManager(context,RecyclerView.HORIZONTAL,false)

    }


    fun sendWordCache(user: User){
        val content = gson.toJson(list)
        val message = Message(0, Owner().userId,conversationId, Message.MESSAGE_WRITE,content,0)
        list.clear()
        mAdapter.notifyDataSetChanged()
        DiBus.postEvent(MsgWithUser(message,user))
        DiBus.postEvent(message,MessageSend{

        })
        DiBus.postEvent(WordCacheState(true))
    }


    @BusEvent(threadPolicy = THREAD_POLICY_MAIN)
    fun receiveWriteEvent(singleWriteEvent: SingleWriteEvent){
      //  Log.d(TAG,singleWriteEvent.list.toString())
        var minH = Float.MAX_VALUE
        var minV = Float.MAX_VALUE
        var maxV = Float.MIN_VALUE
        var maxH = Float.MIN_VALUE
        for(v in singleWriteEvent.list){
            minH = minH.coerceAtMost(v.x)
            minV = minV.coerceAtMost(v.y)
            maxH = maxH.coerceAtLeast(v.x)
            maxV = maxV.coerceAtLeast(v.y)
        }
        Log.d(TAG,"x:$minH,y:$minV")
        list.add(MessageWriteWord(minH,minV,maxH,maxV,singleWriteEvent.list))
        mAdapter.notifyItemInserted(list.size-1)
        if(list.size == 1){
            DiBus.postEvent(WordCacheState(false))
        }
    }

    companion object{
        const val WORD_G = 8f
    }
}