package com.vlog.room

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.common.Result
import com.common.ext.afterTextChanged
import com.common.ext.toast
import com.dibus.DiBus
import com.vlog.R
import com.vlog.conversation.room.RoomEditSource
import com.vlog.database.Room
import com.vlog.databinding.ItemEditLayoutBinding
import com.vlog.user.Owner
import dibus.app.RoomEditSourceCreator
import java.lang.IllegalArgumentException
import java.lang.RuntimeException

class RoomNameEditActivity : AppCompatActivity() {

    private lateinit var binding: ItemEditLayoutBinding
    private lateinit var room:Room

    private val source = RoomEditSourceCreator.get()

    private var isChange = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.item_edit_layout)
        room = intent.getParcelableExtra("room")?:throw IllegalArgumentException()
        binding.titleText.text = "更改名字"
        binding.editText.setText(room.roomName)
        dispatchEvent()
    }


    private fun dispatchEvent(){
        binding.editText.afterTextChanged {
            if(!isChange){
                isChange = true
                binding.submitBt.apply {
                    setBackgroundResource(R.drawable.yellow_rectangle_bg)
                    setTextColor(Color.WHITE)
                }
            }
        }

        binding.submitBt.setOnClickListener {
            val text =binding.editText.text.toString()
            if(isChange && text.isNotEmpty()){
                val newRoom = room.copy(roomName= text)
                source.update(newRoom).observe(this){
                    when(it){
                        is Result.Error->{
                            it.error.printStackTrace()
                            toast(it.toString())
                        }
                        is Result.Data->{
                            DiBus.postEvent(it.data)
                            onBackPressed()
                        }
                    }
                }
            }
        }
        binding.leftView.setOnClickListener {
            onBackPressed()
        }
    }

    companion object{
        fun launch(context: Context,room: Room){
            val intent = Intent(context,RoomNameEditActivity::class.java)
            intent.putExtra("room",room)
            context.startActivity(intent)
        }
    }
}