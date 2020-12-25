package com.vlog.conversation.phone

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.common.pushExecutors
import com.dibus.ViewModelService
import com.rtp.jlibrtp.Participant
import com.rtp.voice.RtpSession
import com.vlog.database.User
import java.lang.Exception
import kotlin.Exception

class PhoneViewModel @ViewModelService(PhoneActivity::class)internal constructor(private val source: VoicePhoneSource):ViewModel(){

    fun call(user:User):LiveData<Exception?>{
        val liveData = MutableLiveData<Exception?>()
        pushExecutors {
            val address = source.fetchNetAddressInfo(user.userId)
            val p = Participant(address.host,address.port,address.port+1)
            p.location

            RtpSession.addParticipant(p)
        }
    }
}