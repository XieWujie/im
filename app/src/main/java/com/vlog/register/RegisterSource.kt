package com.vlog.register

import androidx.lifecycle.LiveData
import com.common.HOST
import com.common.Result
import com.common.ext.toLiveData
import com.dibus.AutoWire
import com.dibus.Service
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

@Service
class RegisterSource {

    @AutoWire
    lateinit var httpClient: OkHttpClient

    @AutoWire
    lateinit var gson: Gson


    fun register(username:String,password:String):LiveData<Result<RegisterResponse>>{
        val entity = LoginEntity(username,password)
        val body = gson.toJson(entity)
        val request = Request.Builder()
            .url("$HOST/user/login")
            .post(body.toRequestBody())
            .build()
        return request.toLiveData()
    }

    data class LoginEntity(val username: String,val password: String)
}
