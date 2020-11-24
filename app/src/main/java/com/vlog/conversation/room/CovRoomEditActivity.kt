package com.vlog.conversation.room

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.common.base.BaseActivity
import com.vlog.R
import com.vlog.database.Room
import com.vlog.databinding.ActivityCovRoomEditBinding

class CovRoomEditActivity :BaseActivity() {

    private lateinit var binding:ActivityCovRoomEditBinding
    private lateinit var adapter:CovREditAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_cov_room_edit)
        val room = intent.getParcelableExtra<Room>("room")!!
        init(room)
    }

    fun init(room: Room){
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter =  CovREditAdapter(this,room)
        binding.recyclerView.adapter =adapter
        binding.titleText.text = room.roomName
    }

    override fun onStart() {
        super.onStart()
        adapter.notifyDataSetChanged()
    }

    companion object{
        fun launch(context:Context,room:Room){
            val intent = Intent(context,CovRoomEditActivity::class.java)
            intent.putExtra("room",room)
            context.startActivity(intent)
        }
    }
}