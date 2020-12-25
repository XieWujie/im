package com.vlog.conversation.phone

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.dibus.AutoWire
import com.vlog.R
import com.vlog.database.User
import com.vlog.databinding.ActivityPhoneBinding

class PhoneActivity : AppCompatActivity() {

    private lateinit var binding:ActivityPhoneBinding

    @AutoWire
    lateinit var viewModel: PhoneViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_phone)

    }

    private fun init(intent: Intent){
        val user = intent.getParcelableExtra<User>("user")?:return
        when(intent.getIntExtra(ACTION,0)){
            ACTION_CALL->{
              viewModel.call(user).observe(this){

              }
            }
        }
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
    }
}