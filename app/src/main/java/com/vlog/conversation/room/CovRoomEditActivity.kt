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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_cov_room_edit)
        val room = intent.getParcelableExtra<Room>("room")!!
        init(room)
    }

    fun init(room: Room){
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = CovREditAdapter(this,room)
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