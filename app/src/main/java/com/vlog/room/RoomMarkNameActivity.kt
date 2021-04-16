package com.vlog.room

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.common.Result
import com.common.base.BaseActivity
import com.common.ext.afterTextChanged
import com.common.ext.toast
import com.vlog.R
import com.vlog.database.Room
import com.vlog.databinding.ActivityRoomMarkNameBinding
import com.vlog.photo.load
import com.vlog.user.Owner
import dibus.app.RoomSourceCreator

class RoomMarkNameActivity : BaseActivity() {

    private lateinit var binding:ActivityRoomMarkNameBinding
    private val source = RoomSourceCreator.get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_room_mark_name)
        val room = intent.getParcelableExtra<Room>("room")!!
        binding.markNameText.setText(room.markName?:"")
        binding.avatarView.load(room.roomAvatar)
        dispatchEvent(room)
    }

    private fun dispatchEvent(room: Room){
        binding.markNameText.afterTextChanged {
            if(it.isNotEmpty()){
                binding.submitBt.apply {
                    setBackgroundResource(R.drawable.yellow_rectangle_bg)
                    setTextColor(Color.WHITE)
                }
            }else{
                binding.submitBt.apply {
                    setBackgroundResource(R.drawable.grey_bt_rectangle)
                    setTextColor(Color.parseColor("#dadada"))
                }
            }
        }
        binding.submitBt.setOnClickListener {
            val text = binding.markNameText.text.toString()
            if(text.isNotEmpty()){
                room.markName = text
                source.updateRoomMarkNameOfMe(Owner().userId,room).observe(this, Observer{
                    when(it){
                        is Result.Error->toast(it.toString())
                        is Result.Data->onBackPressed()
                    }
                })
            }
        }
        binding.icBackView.setOnClickListener {
            onBackPressed()
        }
        binding.dismissCard.setOnClickListener {
            binding.markNameText.setText("")
        }
    }


    companion object{
        fun launch(room:Room,context: Context){
            val intent = Intent(context,RoomMarkNameActivity::class.java)
            intent.putExtra("room",room)
            context.startActivity(intent)
        }
    }
}