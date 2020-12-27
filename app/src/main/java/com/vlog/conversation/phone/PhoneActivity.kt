package com.vlog.conversation.phone

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.common.ext.toast
import com.dibus.AutoWire
import com.vlog.R
import com.vlog.database.User
import com.vlog.databinding.ActivityPhoneBinding
import dibus.app.PhoneActivityCreator

class PhoneActivity : AppCompatActivity() {

    private lateinit var binding:ActivityPhoneBinding

    @AutoWire
    lateinit var viewModel: PhoneViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_phone)
        PhoneActivityCreator.inject(this)
        init(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?:return
        init(intent)
    }

    private fun init(intent: Intent){
        when(intent.getIntExtra(ACTION,0)){
            ACTION_CALL->{
                val user = intent.getParcelableExtra<User>("user")?:return
                initUi(user)
              viewModel.call(user).observe(this, Observer {
                  if(it != null){
                      toast(it.message?:"")
                  }
              })
            }
            ACTION_ANSWER->{
                val userId = intent.getIntExtra("userId",0)
                viewModel.getUser(userId).observe(this, Observer {
                    viewModel.start()
                    initUi(it.user)
                })
            }
        }
    }

    private fun initUi(user: User){
        binding.usernameText.text = user.username
    }


    companion object{
        private const val ACTION = "action"
        private const val ACTION_ANSWER = 1
        private const val ACTION_CALL = 2

        fun launchVoicePhone(context: Context,user: User){
            val intent = Intent(context,PhoneActivity::class.java).apply {
                putExtra("user",user)
                putExtra(ACTION, ACTION_CALL)
            }
            context.startActivity(intent)
        }

        fun launchAnswerPhone(context: Context,userId:Int){
            val intent = Intent(context,PhoneActivity::class.java).apply {
                putExtra("userId",userId)
                putExtra(ACTION, ACTION_ANSWER)
            }
            context.startActivity(intent)
        }
    }
}