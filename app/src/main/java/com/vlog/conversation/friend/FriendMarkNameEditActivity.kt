package com.vlog.conversation.friend

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.common.Result
import com.common.ext.afterTextChanged
import com.common.ext.toast
import com.vlog.R
import com.vlog.database.Friend
import com.vlog.database.Room
import com.vlog.databinding.ActivityFriendMarkNameEditBinding
import com.vlog.databinding.ActivityRoomMarkNameBinding
import com.vlog.photo.load
import com.vlog.room.RoomMarkNameActivity
import com.vlog.user.Owner
import dibus.app.FriendSourceCreator
import dibus.app.RoomSourceCreator

class FriendMarkNameEditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFriendMarkNameEditBinding
    private val source = FriendSourceCreator.get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_friend_mark_name_edit)
        val friend = intent.getParcelableExtra<Friend>("friend")!!
        binding.markNameText.setText(friend.markName?:"")
        binding.avatarView.load(friend.user.avatar)
        dispatchEvent(friend)
    }

    private fun dispatchEvent(friend: Friend){
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
                friend.markName = text
                source.updateFriendMarkName(Owner().userId,friend).observe(this){
                    when(it){
                        is Result.Error->toast(it.toString())
                        is Result.Data->onBackPressed()
                    }
                }
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
        fun launch(friend: Friend, context: Context){
            val intent = Intent(context, FriendMarkNameEditActivity::class.java)
            intent.putExtra("friend",friend)
            context.startActivity(intent)
        }
    }
}