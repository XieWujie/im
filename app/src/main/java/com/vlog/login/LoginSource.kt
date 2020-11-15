package com.vlog.login

import androidx.lifecycle.LiveData
import com.common.HOST
import com.common.Result
import com.common.ext.toLiveData
import com.dibus.AutoWire
import com.dibus.Service
import com.google.gson.Gson
import com.vlog.user.Owner
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

@Service
class LoginSource {
    
    @AutoWire
    lateinit var gson: Gson


    fun login(username:String, password:String):LiveData<Result<LoginResponse>>{
        val entity = LoginEntity(username,password)
        val body = gson.toJson(entity)
        val request = Request.Builder()
            .url("$HOST/user/login")
            .post(body.toRequestBody())
            .build()
        return request.toLiveData{
            Owner().init(it)
        }
    }

    data class LoginEntity(val username: String,val password: String)
}
