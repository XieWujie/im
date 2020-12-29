package com.vlog.conversation.phone

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.dibus.AutoWire
import com.vlog.R
import com.vlog.database.Friend
import com.vlog.database.Message
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
        dispatchEvent()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?:return
        init(intent)
    }

   private fun dispatchEvent(){
        binding.closeCallLayout.setOnClickListener {
            viewModel.closeMediaCapturer()
        }
       val rotAni = ObjectAnimator.ofFloat(binding.cameraSwitchView,"rotationY",0f,180f)
       rotAni.duration = 1000
       binding.cameraSwitchLayout.setOnClickListener {
           rotAni.start()
           viewModel.changeVideoCapturer()
       }
       binding.voiceStateLayout.setOnClickListener {
           val view = binding.voiceStateView
           view.isSelected = !view.isSelected
           viewModel.setIsVoice(view.isSelected)
       }
       binding.cameraStateLayout.setOnClickListener {
           val view = binding.cameraStateView
           view.isSelected = !view.isSelected
           viewModel.setVideoOrVoice(view.isSelected)
       }
       binding.micStateLayout.setOnClickListener {
           val view = binding.micStateView
           view.isSelected = !view.isSelected
           viewModel.setMic(view.isSelected)
       }

    }

    private fun init(intent: Intent){
        val conversationId = intent.getIntExtra("conversationId",-1)
        viewModel.init(conversationId,Message.FROM_TYPE_FRIEND,this)
        viewModel.attachView(binding.localSurface,binding.remoteSurface)
        when(intent.getIntExtra(ACTION,0)){
            ACTION_CALL->{
                viewModel.call()
            }
            ACTION_ANSWER->{
                viewModel.reCall()
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


        fun launchVoicePhone(context: Context,friend:Friend){
            val intent = Intent(context,PhoneActivity::class.java).apply {
                putExtra("user",friend.user)
                putExtra("conversationId",friend.conversationId)
                putExtra(ACTION, ACTION_CALL)
            }
            context.startActivity(intent)
        }

        fun launchAnswerPhone(context: Context,user:User,conversationId:Int){
            val intent = Intent(context,PhoneActivity::class.java).apply {
                putExtra("user",user)
                putExtra("conversationId",conversationId)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtra(ACTION, ACTION_ANSWER)
            }
            context.startActivity(intent)
        }
    }
}