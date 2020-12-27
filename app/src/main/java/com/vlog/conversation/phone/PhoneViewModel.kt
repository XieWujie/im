package com.vlog.conversation.phone

import android.content.Context
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
import org.webrtc.VideoCapturer


class PhoneViewModel @ViewModelService(PhoneActivity::class)internal constructor(private val source: VoicePhoneSource,val userSource: UserSource):ViewModel(){

    private var sessionInitial:SessionInitial? = null
    private var conversationId = -1
    private var fromType = -1

    val onlineLiveData = MutableLiveData<Boolean>()

    fun connect(conversationId:Int,type:Int,context: Context){
        this.conversationId = conversationId
        fromType = type
        sessionInitial = SessionInitial(type,conversationId,context)
    }


    fun reCall(){
        DiBus.postEvent(Message.obtain(conversationId,Message.RTC_AGREE,"",fromType),MessageSend{

        })
    }

    @BusEvent(threadPolicy = THREAD_POLICY_MAIN)
    fun rtcEvent(event:RTCEvent){
        if(event.message.conversationId != conversationId){
            return
        }
        when(event.message.messageType){
            Message.RTC_NOT_ONLINE->onlineLiveData.value = false
            Message.RTC_ONLINE->sessionInitial?.createPeerConnection()
            Message.RTC_AGREE->sessionInitial?.createOffer()
            Message.RTC_DES_OFFER->answer(event.message.content)
            Message.RTC_ICE_OFFER->addIce(event.message.content)
        }
    }

    fun attachView(local:SurfaceViewRenderer,remote:SurfaceViewRenderer,videoCapturer: VideoCapturer){
        sessionInitial?.attachView(local,remote,videoCapturer)
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

}


data class RTCEvent(val message: Message,val fromUser: User)