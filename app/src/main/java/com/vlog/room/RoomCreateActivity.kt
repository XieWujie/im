package com.vlog.room

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.common.Result
import com.common.ext.toast
import com.dibus.AutoWire
import com.vlog.R
import com.vlog.conversation.ConversationActivity
import com.vlog.database.FriendDao
import com.vlog.database.Room
import com.vlog.databinding.ActivityRoomCreateBinding
import com.vlog.user.Owner
import dibus.app.RoomCreateActivityCreator

class RoomCreateActivity : AppCompatActivity() {


    private lateinit var binding:ActivityRoomCreateBinding
    private val adapter = RoomCreateAdapter()

    @AutoWire
    lateinit var source: RoomCreateSource

    @AutoWire
    lateinit var friendDao: FriendDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RoomCreateActivityCreator.inject(this)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_room_create)
        init()
    }

    fun init(){
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        friendDao.getFriend(Owner().userId).observe(this){
            adapter.setList(it)
        }
        binding.nextBt.setOnClickListener {
            source.create(Room(0,"","",Owner().userId),adapter.checkList.toList())
                .observe(this){
                    when(it){
                        is Result.Error->toast(it.toString())
                        is Result.Data->{
                            ConversationActivity.launch(this,it.data)
                        }
                    }
                }
        }
        binding.cancelAction.setOnClickListener {
            onBackPressed()
        }
    }
}