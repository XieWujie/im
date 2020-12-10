package com.vlog.conversation.room

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.common.Result
import com.common.base.BaseActivity
import com.common.ext.toast
import com.common.util.Util
import com.dibus.AutoWire
import com.vlog.R
import com.vlog.database.Room
import com.vlog.database.RoomDao
import com.vlog.databinding.ActivityCovRoomEditBinding
import com.vlog.room.RoomSource
import com.vlog.user.Owner
import dibus.app.CovRoomEditActivityCreator
import java.io.File

class CovRoomEditActivity :BaseActivity() {

    private lateinit var binding:ActivityCovRoomEditBinding
    private lateinit var adapter:CovREditAdapter


    @AutoWire
    lateinit var roomSource:RoomSource

    private var conversationId = -1

    private lateinit var room: Room

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CovRoomEditActivityCreator.inject(this)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_cov_room_edit)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        room = intent.getParcelableExtra<Room>("room")!!
        conversationId = room.conversationId
    }

    override fun onStart() {
        super.onStart()
        init()
    }

    fun init(){
        roomSource.roomDao.getRoomById(conversationId).apply {
            observe(this@CovRoomEditActivity){
                room = it
                binding.titleText.text = it.roomName
                adapter =  CovREditAdapter(this@CovRoomEditActivity,it)
                binding.recyclerView.adapter = adapter
                adapter.changeRoomBackgroundListener = {
                    val photoPickerIntent = Intent(Intent.ACTION_PICK, null)
                    photoPickerIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
                    startActivityForResult(photoPickerIntent, 1)
                }
                removeObservers(this@CovRoomEditActivity)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val d = data?.data
        if (Activity.RESULT_OK == resultCode) {
            if (d !=null){
                initAvatar(d)
            }
        }
    }

    private fun initAvatar(uri: Uri){
        val realPath = Util.getRealPathFromURI(this,uri)
        if (realPath == null){
            toast("获取图片失败")
        }else {
            roomSource.updateCustomerRoomBg(Owner().userId,room,File(realPath))
                .observe(this){
                when(it){
                    is Result.Error->{
                        it.error.printStackTrace()
                        toast(it.toString())
                    }
                    is Result.Data->{
                        toast("更换成功")
                    }
                }
            }
        }
    }


    companion object{
        fun launch(context:Context,room:Room){
            val intent = Intent(context,CovRoomEditActivity::class.java)
            intent.putExtra("room",room)
            context.startActivity(intent)
        }
    }
}