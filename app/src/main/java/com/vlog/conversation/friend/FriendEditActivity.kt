package com.vlog.conversation.friend

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.common.Result
import com.common.base.BaseActivity
import com.common.ext.toast
import com.common.util.Util
import com.dibus.AutoWire
import com.vlog.R
import com.vlog.database.Friend
import com.vlog.databinding.ActivityFriendEditBinding
import com.vlog.user.Owner
import dibus.app.FriendEditActivityCreator
import java.io.File

class FriendEditActivity : BaseActivity() {

    private lateinit var binding:ActivityFriendEditBinding

    private lateinit var friend: Friend

    @AutoWire
    lateinit var source: FriendSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_friend_edit)
        FriendEditActivityCreator.inject(this)
        friend = intent.getParcelableExtra<Friend>("friend")!!
        dispatchEvent()
        source.friendDao.getFriendByCovId(friend.conversationId).observe(this, Observer{
            friend = it
            binding.notifySwitch.isChecked = !friend.notify
            binding.markNameText.text = friend.markName?:""
        })
    }


    private fun dispatchEvent(){
        binding.changeBackgroundLayout.setOnClickListener {
            reqPermission {
                val photoPickerIntent = Intent(Intent.ACTION_PICK, null)
                photoPickerIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
                startActivityForResult(photoPickerIntent, 1)
            }
        }
        binding.icBackView.setOnClickListener {
            onBackPressed()
        }
        binding.notifySwitch.setOnCheckedChangeListener { _, isChecked ->
            source.friendNotify(friend,!isChecked,Owner().userId)
        }
        binding.markNameLayout.setOnClickListener {
            FriendMarkNameEditActivity.launch(friend,it.context)
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
            source.updateCustomerFriendBg(Owner().userId,friend, File(realPath))
                .observe(this, Observer{
                    when(it){
                        is Result.Error->{
                            it.error.printStackTrace()
                            toast(it.toString())
                        }
                        is Result.Data->{
                            toast("更换成功")
                        }
                    }
                })
        }
    }

    companion object{
        fun launch(friend:Friend,context: Context){
            val intent = Intent(context,FriendEditActivity::class.java)
            intent.putExtra("friend",friend)
            context.startActivity(intent)
        }
    }
}