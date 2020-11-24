package com.vlog.room

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.common.Result
import com.common.Util
import com.common.base.BaseActivity
import com.common.ext.toast
import com.common.pushExecutors
import com.dibus.AutoWire
import com.dibus.DiBus
import com.vlog.R
import com.vlog.avatar.UserAvatarActivity
import com.vlog.avatar.load
import com.vlog.database.Room
import com.vlog.database.User
import com.vlog.user.Owner
import com.vlog.user.UserSource
import dibus.app.RoomEditSourceCreator
import dibus.app.UserAvatarActivityCreator

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
                        checkPermission(){
                            dispatchPictureIntent()
                        }
                    }
                    1-> {
                        pushExecutors {
                            try {
                                Glide.with(this).load(imageView.drawable)
                                    .downloadOnly(200, 200).get()
                                runOnUiThread {
                                    toast("下载成功")
                                }

                            }catch (e :Exception){
                                runOnUiThread {
                                    toast("下载失败：${e.message}")
                                }
                            }
                        }
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