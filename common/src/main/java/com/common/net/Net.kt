package com.common.net

import com.dibus.CREATE_SINGLETON
import com.dibus.Register
import com.dibus.Service
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
@Service
class Net{
    private val okHttpClient = OkHttpClient.Builder()
        .pingInterval(40,TimeUnit.SECONDS)
        .build()

    @Register(createStrategy = CREATE_SINGLETON)
    fun provideOkHttpClient() = okHttpClient


}