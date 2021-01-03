package com.vlog.conversation.phone

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.dibus.AutoWire
import com.vlog.R
import com.vlog.connect.notify.Notify
import com.vlog.conversation.ConversationActivity
import com.vlog.database.Friend
import com.vlog.database.Message
import com.vlog.database.User
import com.vlog.databinding.ActivityPhoneBinding
import com.vlog.photo.load
import com.vlog.user.Owner
import com.vlog.util.AudioUtil
import dibus.app.PhoneActivityCreator


class PhoneActivity : AppCompatActivity() {

    private lateinit var binding:ActivityPhoneBinding
    private var type = PHONE_TYPE_AUDIO
    private lateinit var user:User
    private lateinit var newIntent: Intent
    private var conversationId = 0
    private lateinit var notify: Notify

    @AutoWire
    lateinit var viewModel: PhoneViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_phone)
        PhoneActivityCreator.inject(this)
        notify = Notify(this)
        init(intent)
        dispatchEvent()
    }

    override fun onBackPressed() {
       ConversationActivity.launch(this,Friend(user,conversationId,Owner().userId))
    }


    private fun closePhone(){
        binding.surfaceGroup.visibility = View.GONE
        binding.callingText.text = "结束通话"
        binding.callControllerGroup.visibility = View.GONE
        binding.receiveCallGroup.visibility = View.GONE
        binding.callingGroup.visibility = View.VISIBLE
        Handler(Looper.getMainLooper()).postDelayed({
            notify.removeNotification()
           finish()
        },500)
    }

   private fun dispatchEvent(){
        binding.closeCallLayout.setOnClickListener {
            viewModel.closeMediaCapturer()
            binding.surfaceGroup.visibility = View.GONE
            viewModel.closePhone()
            closePhone()
        }
       val rotAni = ObjectAnimator.ofFloat(binding.cameraSwitchView,"rotationY",0f,180f)
       rotAni.duration = 1000
       binding.cameraSwitchLayout.setOnClickListener {
           rotAni.start()
           viewModel.changeVideoCapturer()
       }
       binding.speakerStateLayout.setOnClickListener {
           val view = binding.voiceStateView
           view.isSelected = !view.isSelected
           if(view.isSelected){
               AudioUtil.openSpeaker(this)
           }else{
               AudioUtil.closeSpeaker(this)
           }
       }
       binding.cameraStateLayout.setOnClickListener {
           val view = binding.cameraStateView
           view.isSelected = !view.isSelected
           viewModel.setVideoOrVoice(view.isSelected)
           if(view.isSelected){
               binding.callingGroup.visibility = View.INVISIBLE
           }else{
               binding.callingGroup.visibility = View.VISIBLE
           }
       }
       binding.micStateView.isSelected = true
       binding.micStateLayout.setOnClickListener {
           val view = binding.micStateView
           view.isSelected = !view.isSelected
           viewModel.setMic(view.isSelected)
       }

       binding.agreeCallLayout.setOnClickListener {
           viewModel.reCall(type)
           viewModel.calledLiveData.value = type
       }

       viewModel.closeLiveData.observe(this, Observer {
           closePhone()
       })

       binding.defyCallLayout.setOnClickListener {
           closePhone()
           viewModel.defyPhone()
       }


       viewModel.calledLiveData.observe(this, Observer {
           binding.receiveCallGroup.visibility = View.GONE
           binding.videoGroup.visibility = View.VISIBLE
           binding.callingText.visibility = View.INVISIBLE
           binding.callingText.text = ""
           notify.sendPhoneNotification(user,newIntent, "正在通话中. . .")
           binding.callingGroup.visibility = if(it == PHONE_TYPE_AUDIO){
               viewModel.setVideoOrVoice(false)
               binding.cameraStateView.isSelected = false
               View.VISIBLE
           }else{
               binding.callingGroup.visibility = View.GONE
               viewModel.setVideoOrVoice(true)
               binding.cameraStateView.isSelected = true
               View.INVISIBLE
           }
       })

    }


    private fun init(intent: Intent){
        conversationId = intent.getIntExtra("conversationId",-1)
        user = intent.getParcelableExtra<User>("user")?:throw IllegalArgumentException("user can not be null")
        type = intent.getIntExtra(PHONE_TYPE, PHONE_TYPE_AUDIO)
        val action = intent.getIntExtra(ACTION,0)
         newIntent= Intent(this,PhoneActivity::class.java).apply {
            putExtra("user",user)
            putExtra("conversationId",conversationId)
            putExtra(ACTION, action)
            putExtra(PHONE_TYPE, type)
        }
        initUi(user)
        viewModel.init(conversationId,Message.FROM_TYPE_FRIEND,this)
        viewModel.attachView(binding.localSurface,binding.remoteSurface)
        when(action){
            ACTION_CALL->{
                binding.receiveCallGroup.visibility = View.GONE
                binding.videoGroup.visibility = View.GONE
                binding.callControllerGroup.visibility = View.VISIBLE
                binding.callingGroup.visibility = View.VISIBLE
                binding.callingText.text = "正在呼叫. . ."
                viewModel.call(type)
                notify.sendPhoneNotification(user,newIntent,"正在呼叫. . .")
            }
            ACTION_ANSWER->{
                binding.videoGroup.visibility = View.GONE
                binding.callControllerGroup.visibility = View.GONE
                binding.receiveCallGroup.visibility = View.VISIBLE
                binding.callingGroup.visibility = View.VISIBLE
                val typeDes = if(type == PHONE_TYPE_VIDEO)"视频" else "语音"
                binding.callingText.text = "邀请你进行${typeDes}通话"
                notify.sendPhoneNotification(user,newIntent, "邀请你进行${typeDes}通话")
            }
        }
    }



    private fun initUi(user: User){
        binding.usernameText.text = user.username
        binding.avatarView.load(user.avatar)
    }


    companion object{
        private const val ACTION = "action"
        private const val ACTION_ANSWER = 1
        private const val ACTION_CALL = 2
        private const val PHONE_TYPE = "phone_type"
        const val PHONE_TYPE_AUDIO = 0
        const val PHONE_TYPE_VIDEO = 1


        fun launchAudioPhone(context: Context, friend:Friend){
            val intent = Intent(context,PhoneActivity::class.java).apply {
                putExtra("user",friend.user)
                putExtra("conversationId",friend.conversationId)
                putExtra(ACTION, ACTION_CALL)
                putExtra(PHONE_TYPE, PHONE_TYPE_AUDIO)
            }
            context.startActivity(intent)
        }

        fun launchVideoPhone(context: Context,friend: Friend){
            val intent = Intent(context,PhoneActivity::class.java).apply {
                putExtra("user",friend.user)
                putExtra("conversationId",friend.conversationId)
                putExtra(ACTION, ACTION_CALL)
                putExtra(PHONE_TYPE, PHONE_TYPE_VIDEO)
            }
            context.startActivity(intent)
        }

        fun launchAnswerAudio(context: Context, user:User, conversationId:Int){
            val intent = Intent(context,PhoneActivity::class.java).apply {
                putExtra("user",user)
                putExtra("conversationId",conversationId)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtra(ACTION, ACTION_ANSWER)
                putExtra(PHONE_TYPE, PHONE_TYPE_AUDIO)
            }
            context.startActivity(intent)
        }

        fun launchAnswerVideo(context: Context, user:User, conversationId:Int){
            val intent = Intent(context,PhoneActivity::class.java).apply {
                putExtra("user",user)
                putExtra("conversationId",conversationId)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                putExtra(ACTION, ACTION_ANSWER)
                putExtra(PHONE_TYPE, PHONE_TYPE_VIDEO)
            }
            context.startActivity(intent)
        }
    }
}