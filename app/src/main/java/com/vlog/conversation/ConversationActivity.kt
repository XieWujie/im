package com.vlog.conversation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.common.base.BaseActivity
import com.dibus.AutoWire
import com.dibus.BusEvent
import com.vlog.R
import com.vlog.adapter.MessageListAdapter
import com.vlog.conversation.room.CovRoomEditActivity
import com.vlog.database.Friend
import com.vlog.database.Room
import com.vlog.database.User
import com.vlog.databinding.ActivityConversationBinding
import dibus.app.ConversationActivityCreator

class ConversationActivity :BaseActivity() {

    private lateinit var binding: ActivityConversationBinding
    private var isLoading = false

    @AutoWire
    lateinit var adapter: MessageListAdapter

    private var isRoom = false

    @AutoWire
    lateinit var viewModel: ConversationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ConversationActivityCreator.inject(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_conversation)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter.notifyDataSetChanged()
        init()
    }

    @BusEvent
    fun roomChangeEvent(room: Room){
        binding.titleText.text = room.roomName
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        init()
    }

    private fun init(){
        var conversationId = -1
        val user = intent.getParcelableExtra<User>("user")
        if(user != null){
            conversationId = user.userId
            binding.titleText.text = user.username
            isRoom = false
        }else{
            val room = intent.getParcelableExtra<Room>("room")?:throw RuntimeException("传入friend 或者room")
            isRoom = true
            conversationId = room.conversationId
            binding.titleText.text = room.roomName
            binding.moreActionView.setOnClickListener {
               CovRoomEditActivity.launch(this,room)
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
        binding.recyclerView.addOnScrollListener(object :RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if(!isLoading && !recyclerView.canScrollVertically(-1)){
                    isLoading = true
                    viewModel.query(adapter.getFirstItemBefore(),conversationId).observe(this@ConversationActivity){
                        isLoading = false
                    }

                }
            }
        })
        viewModel.queryMessage(conversationId).observe(this){
            adapter.flashList(it)
            Log.d("messageList",it.toString())
        }
       adapter.registerAdapterDataObserver(object :RecyclerView.AdapterDataObserver(){
           override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
               if(positionStart>0){
                   binding.recyclerView.scrollToPosition(adapter.itemCount-1)
               }
           }
       })

    }

    companion object{

        fun launch(context: Context, friend: Friend){
            val intent = Intent(context,ConversationActivity::class.java)
            intent.putExtra("user",friend.user)
            context.startActivity(intent)
        }

        fun launch(context: Context, room:Room){
            val intent = Intent(context,ConversationActivity::class.java)
            intent.putExtra("room",room)
            context.startActivity(intent)
        }
    }
}