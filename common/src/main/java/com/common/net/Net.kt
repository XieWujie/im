package com.common.net

import com.common.HOST
import com.dibus.CREATE_SINGLETON
import com.dibus.Register
import com.dibus.Service
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
@Service
class Net{
    private val okHttpClient = OkHttpClient.Builder()
        .pingInterval(40,TimeUnit.SECONDS)
        .build()

    @Register(createStrategy = CREATE_SINGLETON)
    fun provideOkHttpClient() = okHttpClient


}