package com.vlog.verify.send

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.common.base.BaseActivity
import com.dibus.DiBus
import com.vlog.database.Message
import com.vlog.user.Owner
import com.vlog.R
import com.vlog.connect.MessageSend
import com.vlog.databinding.ActivitySendVerifyBinding
import com.vlog.database.User

class SendVerifyActivity :BaseActivity() {

    private lateinit var binding:ActivitySendVerifyBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_send_verify)
        val user = intent.getParcelableExtra<User>("user")!!
        initView(user)
        binding.comeBackView.setOnClickListener {
            finish()
        }
        binding.sendVerifyText.setOnClickListener {
            val message = Message(
                0,
                Owner().userId,user.userId,
                Message.SendVerifyAdd,binding.verifyEdit.text.toString(),0)
            DiBus.postEvent(message,MessageSend{})
            finish()
        }
    }


    private fun initView(user: User){
        binding.usernameText.text = user.username
        binding.descriptionText.text = user.description
        Glide.with(this).load(user.avatar)
            .placeholder(R.drawable.avater_default)
            .into(binding.avatarView)
    }
    companion object{
        fun launch(context: Context,user: User){
            val intent = Intent(context,SendVerifyActivity::class.java)
            intent.putExtra("user",user)
            context.startActivity(intent)
        }
    }
}