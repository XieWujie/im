package com.vlog

import android.app.Application
import com.dibus.DiBus
import com.google.gson.Gson
import com.rtp.voice.RtpSession
import com.vlog.conversation.phone.PhoneActivity
import com.vlog.database.DbApp
import dibus.common.NetCreator

class App :Application(){


    override fun onCreate() {
        super.onCreate()
        context = this
        DiBus().injectApplication(this)
        DiBus().addObjectSingle(Gson::class.java.canonicalName!!,Gson())
        NetCreator.get()
        DbApp.get(this)
        RtpSession.callListener = {
            PhoneActivity.launchAnswerPhone(this,it.ssrc.toInt())
        }
    }

    companion object{
        private lateinit var context:App

        fun get() = context
    }
}