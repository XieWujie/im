package com.vlog.conversation

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.common.base.BaseActivity
import com.common.util.ScreenUtils
import com.common.util.Util
import com.dibus.AutoWire
import com.dibus.BusEvent
import com.vlog.R
import com.vlog.adapter.MessageListAdapter
import com.vlog.conversation.room.CovRoomEditActivity
import com.vlog.database.Friend
import com.vlog.database.Room
import com.vlog.databinding.ActivityConversationBinding
import dibus.app.ConversationActivityCreator

class ConversationActivity :BaseActivity() {

    private lateinit var binding: ActivityConversationBinding
    private var isLoading = false

    @AutoWire
    lateinit var adapter: MessageListAdapter

    private var isRoom = false

    private var softKeyHeight= 0

    @AutoWire
    lateinit var viewModel: ConversationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ConversationActivityCreator.inject(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_conversation)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        init()
        dispatchEvent()
    }

    @BusEvent
    fun roomChangeEvent(room: Room){
        binding.titleText.text = room.roomName
    }

    private fun dispatchEvent(){
        var rcHeight = binding.recyclerView.height
        SoftKeyBoardListener.setListener(this,object :SoftKeyBoardListener.OnSoftKeyBoardChangeListener{
            override fun keyBoardShow(height: Int) {
                binding.bottomInputLayout.softKeyHeight = height
                binding.bottomInputLayout.showSoftKey()
                rcHeight = height
                if(adapter.itemCount>0){
                    binding.recyclerView.scrollToPosition(adapter.itemCount - 1)
                }
            }

            override fun keyBoardHide(height: Int) {

            }

        })
        binding.bottomInputLayout.setBottomContentShowListener {
            if(it){
                binding.recyclerView.scrollToPosition(adapter.itemCount-1)
            }
        }
    }



    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        init()
    }

    private fun init(){
        var conversationId = -1
        val friend = intent.getParcelableExtra<Friend>("friend")
        if(friend != null){
            conversationId = friend.conversationId
            binding.titleText.text = friend.user.username
            isRoom = false
        }else{
            val room = intent.getParcelableExtra<Room>("room")?:throw RuntimeException("传入friend 或者room")
            isRoom = true
            conversationId = room.conversationId
            binding.titleText.text = room.roomName
            binding.moreActionView.setOnClickListener {
               CovRoomEditActivity.launch(this, room)
            }
        }

        binding.icBack.setOnClickListener {
            onBackPressed()
        }
        if(conversationId != -1){
            binding.bottomInputLayout.setConversationId(conversationId)
        }
        dispatchEvent(conversationId)
    }

   private fun dispatchEvent(conversationId: Int){
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!isLoading && !recyclerView.canScrollVertically(-1)) {
                    isLoading = true
                    viewModel.query(adapter.getFirstItemBefore(), conversationId)
                        .observe(this@ConversationActivity) {
                            isLoading = false
                        }

                }
            }
        })
        viewModel.queryMessage(conversationId).observe(this){
            adapter.flashList(it)
        }
       adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
           override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
               if (positionStart > 0) {
                   binding.recyclerView.scrollToPosition(adapter.itemCount - 1)
               }
           }
       })

    }

    companion object{

        fun launch(context: Context, friend: Friend){
            val intent = Intent(context, ConversationActivity::class.java)
            intent.putExtra("friend", friend)
            context.startActivity(intent)
        }

        fun launch(context: Context, room: Room){
            val intent = Intent(context, ConversationActivity::class.java)
            intent.putExtra("room", room)
            context.startActivity(intent)
        }

    }
}