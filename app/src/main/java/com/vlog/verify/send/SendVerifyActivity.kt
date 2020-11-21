package com.vlog.verify.send

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.WorkSource
import android.view.View
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.common.Result
import com.common.base.BaseActivity
import com.dibus.AutoWire
import com.dibus.DiBus
import com.google.gson.Gson
import com.vlog.R
import com.vlog.connect.MessageSend
import com.vlog.database.Message

import com.vlog.database.User
import com.vlog.databinding.ActivitySendVerifyBinding
import com.vlog.user.Owner
import com.vlog.database.Verify
import com.vlog.database.VerifyWithUser
import com.vlog.verify.VerifySource
import dibus.app.SendVerifyActivityCreator

class SendVerifyActivity :BaseActivity() {

    private lateinit var binding:ActivitySendVerifyBinding

    @AutoWire
    lateinit var source: VerifySource


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SendVerifyActivityCreator.inject(this)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_send_verify)
        val user = intent.getParcelableExtra<User>("user")!!
        initView(user)
        binding.comeBackView.setOnClickListener {
            finish()
        }
        binding.sendVerifyText.setOnClickListener {
            val verify = Verify(0,Verify.noAction,binding.verifyEdit.text.toString(),Owner().userId,user.userId,0)
            val wrap = VerifyWithUser(verify,user)
            binding.loadBar.visibility = View.VISIBLE
            source.sendVerify(wrap).observe(this){
                binding.loadBar.visibility = View.GONE
                onBackPressed()
            }
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