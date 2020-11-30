package com.vlog.user

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.common.base.BaseActivity
import com.dibus.AutoWire
import com.vlog.R
import com.vlog.conversation.ConversationActivity
import com.vlog.database.User
import com.vlog.databinding.ActivityUserHomeBinding
import com.vlog.photo.load
import com.vlog.verify.send.SendVerifyActivity
import dibus.app.UserHomeActivityCreator

class UserHomeActivity : BaseActivity() {

    private lateinit var binding:ActivityUserHomeBinding

    @AutoWire
    lateinit var viewModel: UserHomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UserHomeActivityCreator.inject(this)
       binding = DataBindingUtil.setContentView(this,R.layout.activity_user_home)
        val user = intent.getParcelableExtra<User>("user")!!
        binding.user = user
        binding.sendMessageBt.isClickable = false
        viewModel.userRelation(user.userId).observe(this,  {relation->
            if(relation != null) {
                binding.addFriendBt.visibility = View.GONE
                binding.sendMessageBt.setOnClickListener {
                    user.also { ConversationActivity.launch(this, relation) }
                }
            }

        })
        binding.addFriendBt.setOnClickListener {
            SendVerifyActivity.launch(this,user)
        }
        loadView(user)
    }

    private fun loadView(user: User){
        binding.avatarView.load(user.avatar)
    }






    companion object{

        fun launch(context: Context,user: User){
            val intent = Intent(context,UserHomeActivity::class.java)
            intent.putExtra("user",user)
            context.startActivity(intent)
        }
    }
}