package com.vlog.user

import android.content.SharedPreferences
import com.common.int
import com.common.string
import com.dibus.AutoWire
import com.dibus.CREATE_SINGLETON
import com.dibus.Service
import com.vlog.login.LoginResponse
import dibus.app.OwnerCreator


class UserFetcher @Service(CREATE_SINGLETON) constructor(private val sharedPreferences: SharedPreferences) {
    var userId:Int by sharedPreferences.int()
    var token by sharedPreferences.string()
    var username by sharedPreferences.string()
}


 class Owner private constructor(){
    var token:String?= null
     var userId = -1
     lateinit var username:String
     var description = ""

    @AutoWire
    lateinit var userFetcher: UserFetcher
    init {
        OwnerCreator.inject(this)
    }

    companion object{
        val instance = Owner()

        operator fun invoke() = instance
    }

    fun init(loginResponse: LoginResponse){
        loginResponse.also {
            userId = it.userId
            token = it.token
            username =it.username
            userFetcher.token = it.token
            userFetcher.userId = it.userId
            userFetcher.username = it.username
        }
    }
}

