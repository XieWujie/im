package com.vlog.user

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.common.util.Util
import com.common.ext.launch
import com.vlog.R
import com.vlog.avatar.UserAvatarActivity
import com.vlog.avatar.load
import com.vlog.database.User
import com.vlog.databinding.ActivityUserItemEditBinding

class UserItemEditActivity : AppCompatActivity() {

    lateinit var binding:ActivityUserItemEditBinding
    private lateinit var user:User


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Util.setLightBar(this,Color.WHITE)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_user_item_edit)
        initView()
        dispatchEvent()
    }

    private fun initView(){
        user = Owner().getUser()
        binding.usernameText.text = user.username
        binding.avatarView.load(user.avatar)
    }

    override fun onStart() {
        super.onStart()
        initView()
    }

    private fun dispatchEvent(){
        binding.avatarLayout.setOnClickListener {
            UserAvatarActivity.launch(this,user)
        }
        binding.usernameLayout.setOnClickListener {
            launch<UserNameEditActivity>()
        }
    }

}