package com.vlog.conversation.phone

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dibus.BusEvent
import com.dibus.DiBus
import com.dibus.THREAD_POLICY_MAIN
import com.dibus.ViewModelService
import com.google.gson.Gson
import com.vlog.connect.MessageSend
import com.vlog.database.Message
import com.vlog.database.User
import com.vlog.user.UserSource
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription
import org.webrtc.SurfaceViewRenderer


class PhoneViewModel @ViewModelService(PhoneActivity::class)internal constructor(private val source: VoicePhoneSource,val userSource: UserSource):ViewModel(){

    private var sessionInitial:SessionInitial? = null
    private var conversationId = -1
    private var fromType = -1

    val calledLiveData = MutableLiveData<Int>()
    val closeLiveData = MutableLiveData<Boolean>()

    private lateinit var local: SurfaceViewRenderer
    private lateinit var remote: SurfaceViewRenderer


    val onlineLiveData = MutableLiveData<Boolean>()

    fun init(conversationId:Int, type:Int, context: Context){
        this.conversationId = conversationId
        fromType = type
        sessionInitial = SessionInitial(type,conversationId,context)
    }


    fun reCall(type: Int){
        val newType = if(type == PhoneActivity.PHONE_TYPE_VIDEO) Message.RTC_AGREE_VIDEO else Message.RTC_AGREE_AUDIO
        DiBus.postEvent(Message.obtain(conversationId,newType,"",fromType),MessageSend{
            if(it == null){
                sessionInitial?.apply {
                    createPeerConnection()
                    attachView(local,remote)
                }
                sessionInitial
            }
        })
    }

    fun call(type: Int){
        val newType = if(type == PhoneActivity.PHONE_TYPE_VIDEO) Message.RTC_REGISTER_VIDEO else Message.RTC_REGISTER_AUDIO
        DiBus.postEvent(Message.obtain(conversationId,newType,"",fromType),MessageSend{

        })
    }

    @BusEvent(threadPolicy = THREAD_POLICY_MAIN)
    fun rtcEvent(event:RTCEvent){
        if(event.message.conversationId != conversationId ){
            return
        }
        Log.d("RTC_EVENT",event.toString())
        when(event.message.messageType){
            Message.RTC_NOT_ONLINE->onlineLiveData.value = false
            Message.RTC_ONLINE->{}
            Message.RTC_AGREE_AUDIO->{
               connect()
                calledLiveData.value = PhoneActivity.PHONE_TYPE_AUDIO
            }
            Message.RTC_AGREE_VIDEO->{
                connect()
                calledLiveData.value = PhoneActivity.PHONE_TYPE_VIDEO
            }
            Message.RTC_DES_OFFER->answer(event.message.content)
            Message.RTC_ICE_OFFER->addIce(event.message.content)
            Message.RTC_DEFY->{
                sessionInitial?.closeMediaCapturer()
                closeLiveData.value = true
            }
            Message.RTC_CLOSE->{
                sessionInitial?.closeMediaCapturer()
                closeLiveData.value = true
            }
        }
    }

    fun defyPhone(){
        DiBus.postEvent(Message.obtain(conversationId,Message.RTC_DEFY,"",fromType),MessageSend{

        })
    }

    fun closePhone(){
        DiBus.postEvent(Message.obtain(conversationId,Message.RTC_CLOSE,"",fromType),MessageSend{

        })
    }

    private fun connect(){
        sessionInitial?.apply {
            createPeerConnection()
            attachView(local,remote)
            createOffer()
        }
    }

    fun attachView(local:SurfaceViewRenderer,remote:SurfaceViewRenderer){
        this.local = local
        this.remote = remote
    }

    private fun answer(des:String){
        val g:Gson = DiBus.load()
        val d = g.fromJson<SessionDescription>(des,SessionDescription::class.java)
        sessionInitial?.setDes(d)
    }

    private fun addIce(c:String){
        val g:Gson = DiBus.load()
        val d = g.fromJson<IceCandidate>(c,IceCandidate::class.java)
        sessionInitial?.addIce(d)
    }

    /**
    *切换镜头
     */
    fun changeVideoCapturer()  = sessionInitial?.changeVideoCapturer()

    /**
     * 结束通话
     */
    fun closeMediaCapturer() = sessionInitial?.closeMediaCapturer()

    /**
     * 视频转语音
     */
    fun setVideoOrVoice(video: Boolean)  = sessionInitial?.setVideoOrVoice(video)


    /**
     * 静音切换
     */
    fun setMic(mic: Boolean)  = sessionInitial?.setMic(mic)

    fun setIsVoice(voice: Boolean) = sessionInitial?.setIsVoice(voice)

}


data class RTCEvent(val message: Message,val fromUser: User)