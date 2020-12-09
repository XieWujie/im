package com.vlog.conversation.room

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.common.base.BaseActivity
import com.dibus.AutoWire
import com.vlog.R
import com.vlog.database.Room
import com.vlog.database.RoomDao
import com.vlog.databinding.ActivityCovRoomEditBinding
import dibus.app.CovRoomEditActivityCreator

class CovRoomEditActivity :BaseActivity() {

    private lateinit var binding:ActivityCovRoomEditBinding
    private lateinit var adapter:CovREditAdapter
    @AutoWire
    lateinit var roomDao: RoomDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CovRoomEditActivityCreator.inject(this)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_cov_room_edit)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        val room = intent.getParcelableExtra<Room>("room")!!
    }

    fun init(room: Room){
        adapter =  CovREditAdapter(this,room.conversationId)
        binding.recyclerView.adapter =adapter
        binding.titleText.text = room.roomName
    }


    companion object{
        fun launch(context:Context,room:Room){
            val intent = Intent(context,CovRoomEditActivity::class.java)
            intent.putExtra("room",room)
            context.startActivity(intent)
        }
    }
}