package com.vlog.conversation.phone

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.common.ext.toast
import com.dibus.AutoWire
import com.vlog.R
import com.vlog.database.Friend
import com.vlog.database.Message
import com.vlog.database.User
import com.vlog.databinding.ActivityPhoneBinding
import dibus.app.PhoneActivityCreator
import org.webrtc.Camera1Enumerator
import org.webrtc.Camera2Enumerator
import org.webrtc.CameraEnumerator
import org.webrtc.VideoCapturer


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
        val conversationId = intent.getIntExtra("conversationId",-1)
        val user = intent.getParcelableExtra<User>("user")?:return
        initUi(user)
        viewModel.connect(conversationId,Message.FROM_TYPE_FRIEND,this)
        val videoCapturer = createVideoCapturer()
        if(videoCapturer == null){
            toast("获取不到摄像头")
            return
        }
        viewModel.attachView(binding.localSurface,binding.remoteSurface,videoCapturer)
        when(intent.getIntExtra(ACTION,0)){
            ACTION_CALL->{

            }
            ACTION_ANSWER->{

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
                putExtra(ACTION, ACTION_ANSWER)
            }
            context.startActivity(intent)
        }
    }

    private fun createVideoCapturer(): VideoCapturer? {
        return if (Camera2Enumerator.isSupported(this)) {
            createCameraCapturer(Camera2Enumerator(this))
        } else {
            createCameraCapturer(Camera1Enumerator(true))
        }
    }

    private fun createCameraCapturer(enumerator: CameraEnumerator): VideoCapturer? {
        val deviceNames = enumerator.deviceNames


        for (deviceName in deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {

                val videoCapturer: VideoCapturer? = enumerator.createCapturer(deviceName, null)
                if (videoCapturer != null) {
                    return videoCapturer
                }
            }
        }

        for (deviceName in deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {

                val videoCapturer: VideoCapturer? = enumerator.createCapturer(deviceName, null)
                if (videoCapturer != null) {
                    return videoCapturer
                }
            }
        }
        return null
    }
}