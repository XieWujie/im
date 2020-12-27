package com.vlog.conversation.phone

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.common.pushExecutors
import com.dibus.ViewModelService
import com.rtp.jlibrtp.Participant
import com.rtp.voice.RtpSession
import com.vlog.database.User
import com.vlog.user.UserSource

class PhoneViewModel @ViewModelService(PhoneActivity::class)internal constructor(private val source: VoicePhoneSource,val userSource: UserSource):ViewModel(){

    fun call(user:User):LiveData<Exception?>{
        val liveData = MutableLiveData<Exception?>()
        pushExecutors {
            try {
                val address = source.fetchNetAddressInfo(user.userId)
                val p = Participant(address.host,address.port,address.port+1)
                p.setSsrc(user.userId.toLong())
                RtpSession.addParticipant(p)
                liveData.postValue(null)
                RtpSession.addParticipant(p)
                RtpSession.start()
                liveData.postValue(null)
            }catch (e:Exception){
                e.printStackTrace()
                liveData.postValue(e)
            }
        }
        return liveData
    }

    fun getUser(userId:Int) = userSource.dao.getFromUerId(userId)

    fun start(){
        RtpSession.start()
    }
}