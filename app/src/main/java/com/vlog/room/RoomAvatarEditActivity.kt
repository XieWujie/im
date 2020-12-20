package com.vlog.room

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.common.HOST_PORT
import com.common.Result
import com.common.util.Util
import com.common.base.BaseActivity
import com.common.ext.toast
import com.dibus.DiBus
import com.vlog.R
import com.vlog.photo.load
import com.vlog.database.Room
import com.vlog.photo.PhotoDownloadTarget
import dibus.app.RoomEditSourceCreator

class RoomAvatarEditActivity : BaseActivity() {
    private lateinit var imageView: ImageView


    private var source = RoomEditSourceCreator.get()

    lateinit var room: Room

    override var customerBar = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Util.setDarkBar(this,Color.BLACK)
        setContentView(R.layout.activity_room_avatar_edit)
        imageView = findViewById(R.id.avatar_view)
        room= intent.getParcelableExtra("room")?:throw IllegalArgumentException()
        imageView.load(room.roomAvatar)
        dispatchEvent()
    }

    private fun dispatchEvent(){
        imageView.setOnLongClickListener {
            showDialog()
            true
        }
    }

    private fun showDialog(){
        val dialog = AlertDialog.Builder(this)
            .setItems(arrayOf("从相册中选取","保存图片")){_,i->
                when(i){
                    0->{
                        reqPermission{
                            dispatchPictureIntent()
                        }
                    }
                    1-> {
                        val realUrl = if (room.roomAvatar.startsWith("/file/get")) {
                            "$HOST_PORT${room.roomAvatar}"
                        } else {
                            room.roomAvatar
                        }
                        Glide.with(this).asFile().load(realUrl).into(PhotoDownloadTarget(this))
                    }
                }
            }.show()
    }

    private fun dispatchPictureIntent() {
        val photoPickerIntent = Intent(Intent.ACTION_PICK, null)
        photoPickerIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        startActivityForResult(photoPickerIntent, 1)
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
            Glide.with(this).load(uri).into(imageView)
            source.changeAvatar(realPath,room).observe(this){
                when(it){
                    is Result.Error->{
                        it.error.printStackTrace()
                        toast(it.toString())
                    }
                    is Result.Data->{
                        toast("更换成功")
                        DiBus.postEvent(it.data)
                        room = it.data
                    }
                }
            }
        }
    }

    companion object{
        fun launch(context: Context, room: Room){
            val intent = Intent(context,RoomAvatarEditActivity::class.java)
            intent.putExtra("room",room)
            context.startActivity(intent)
        }
    }
}