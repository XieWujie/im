package com.vlog.conversation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.common.base.BaseActivity
import com.dibus.AutoWire
import com.dibus.BusEvent
import com.vlog.R
import com.vlog.conversation.adapter.MessageListAdapter
import com.vlog.conversation.room.CovRoomEditActivity
import com.vlog.database.Friend
import com.vlog.database.Message
import com.vlog.database.MsgWithUser
import com.vlog.database.Room
import com.vlog.databinding.ActivityConversationBinding
import com.vlog.util.onClick
import dibus.app.ConversationActivityCreator

class ConversationActivity : BaseActivity() {

    private lateinit var binding: ActivityConversationBinding
    private var isLoading = false

    @AutoWire
    lateinit var adapter: MessageListAdapter

    private var isRoom = false

    private var maxTime = Long.MAX_VALUE


    @AutoWire
    lateinit var viewModel: ConversationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ConversationActivityCreator.inject(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_conversation)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, true).apply {
            stackFromEnd = true
        }
        init()
        dispatchEvent()
    }

    override fun onStart() {
        super.onStart()
        isAlive = true
    }

    override fun onStop() {
        super.onStop()
        isAlive = false
    }

    @BusEvent
    fun roomChangeEvent(room: Room) {
        binding.titleText.text = room.roomName
    }


    private fun dispatchEvent() {
        binding.recyclerView.setOnClickListener {
            binding.bottomInputLayout.hide()
        }

        binding.recyclerView.onClick {
            binding.bottomInputLayout.hide()
        }
        var minHeight = 0
        binding.listLayout.post {
            minHeight = binding.listLayout.height
        }


        SoftKeyBoardListener.setListener(this,
            object : SoftKeyBoardListener.OnSoftKeyBoardChangeListener {
                override fun keyBoardShow(height: Int) {
                    binding.bottomInputLayout.softKeyHeight = height
                    binding.bottomInputLayout.showSoftKey()
                    if (adapter.itemCount > 0) {
                        binding.recyclerView.scrollToPosition(0)
                    }
                    minHeight = binding.listLayout.height
                }

                override fun keyBoardHide(height: Int) {

                }

            })
        binding.bottomInputLayout.setBottomContentShowListener {
            if (it) {
                binding.recyclerView.scrollToPosition(0)
            }
        }

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (positionStart < 3) {
                    binding.recyclerView.scrollToPosition(0)
                }
            }

        })
    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        init()
    }

    @BusEvent
    fun imageLoadFinishEvent(event:ImageLoadFinish){
        binding.recyclerView.scrollToPosition(0)
    }

    private fun init() {
        var conversationId = -1
        val friend = intent.getParcelableExtra<Friend>("friend")
        if (friend != null) {
            conversationId = friend.conversationId
            binding.titleText.text = friend.user.username
            isRoom = false
        } else {
            val room =
                intent.getParcelableExtra<Room>("room") ?: throw RuntimeException("传入friend 或者room")
            isRoom = true
            conversationId = room.conversationId
            binding.titleText.text = room.roomName
            binding.moreActionView.setOnClickListener {
                CovRoomEditActivity.launch(this, room)
            }
        }
        if (isRoom) {
            binding.bottomInputLayout.fromType = Message.FROM_TYPE_ROOM
        } else {
            binding.bottomInputLayout.fromType = Message.FROM_TYPE_FRIEND
        }

        binding.icBack.setOnClickListener {
            onBackPressed()
        }
        if (conversationId != -1) {
            binding.bottomInputLayout.setConversationId(conversationId)
        }
        currentConversationId = conversationId
        dispatchEvent(conversationId)
    }

    private fun dispatchEvent(conversationId: Int) {
        viewModel.queryMessage(conversationId, maxTime).apply {
            observe(this@ConversationActivity) {
                adapter.flashList(it)
                removeObservers(this@ConversationActivity)
                viewModel.getLatest(conversationId).observe(this@ConversationActivity){
                    if(it != null){
                        adapter.messageInsert(it)
                    }
                }
            }
        }

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!isLoading && !recyclerView.canScrollVertically(-1)) {
                    isLoading = true
                    val originLiveData =
                        viewModel.queryBeforeMessage(conversationId, adapter.getFirstItemBefore())
                    originLiveData.apply {
                        observe(this@ConversationActivity) {
                            if (it.isNotEmpty()) {
                                adapter.addBefore(it)
                            }
                            if (it.size < 15) {
                                val originNetLiveData = viewModel.query(
                                    adapter.getFirstItemBefore() / 1000,
                                    conversationId
                                )
                                originNetLiveData.observe(this@ConversationActivity) {
                                    isLoading = false
                                    originNetLiveData.removeObservers(this@ConversationActivity)
                                }
                            } else {
                                isLoading = false
                            }
                            removeObservers(this@ConversationActivity)
                        }
                    }
                }
            }
        })

    }

    companion object {

        var isAlive = false
        var currentConversationId = -1


        fun launch(context: Context, friend: Friend) {
            val intent = Intent(context, ConversationActivity::class.java)
            intent.putExtra("friend", friend)
            context.startActivity(intent)
        }

        fun launch(context: Context, room: Room) {
            val intent = Intent(context, ConversationActivity::class.java)
            intent.putExtra("room", room)
            context.startActivity(intent)
        }

    }
}