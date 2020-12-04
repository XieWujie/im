package com.vlog

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.dibus.DiBus
import com.google.gson.Gson
import com.vlog.database.DbApp
import dibus.app.WsListenerCreator
import dibus.common.NetCreator

class App :Application(){


    override fun onCreate() {
        super.onCreate()
        context = this
        DiBus().injectApplication(this)
        DiBus().addObjectSingle(Gson::class.java.canonicalName!!,Gson())
        NetCreator.get()
        DbApp.get(this)

    }

    companion object{
        private lateinit var context:App

        fun get() = context
    }
}