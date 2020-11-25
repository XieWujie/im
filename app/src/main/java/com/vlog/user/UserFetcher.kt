package com.vlog.user

import android.content.SharedPreferences
import com.common.int
import com.common.string
import com.dibus.AutoWire
import com.dibus.CREATE_SINGLETON
import com.dibus.Service
import com.vlog.database.User
import com.vlog.login.LoginResponse
import dibus.app.UserFetcherCreator


class UserFetcher @Service(CREATE_SINGLETON) constructor(private val sharedPreferences: SharedPreferences) {
    var userId: Int by sharedPreferences.int()
    var token by sharedPreferences.string()
    var username by sharedPreferences.string()
    var description by sharedPreferences.string()
    var avatar by sharedPreferences.string()
}


class Owner private constructor() {

    var userFetcher: UserFetcher = UserFetcherCreator.get()
    var token: String? = null
    var userId = userFetcher.userId
    var username = userFetcher.username
    var description = userFetcher.description
    var avatar = userFetcher.avatar

    var isLogout =true



    companion object {
        val instance = Owner()

        operator fun invoke() = instance
    }

    fun getUser() = User(userId, username, avatar, description)

    fun init(loginResponse: LoginResponse) {
        isLogout = false
        loginResponse.also {
            userId = it.userId
            token = it.token
            username = it.username
            avatar = it.avatar?:""
            userFetcher.token = it.token?:""
            userFetcher.userId = it.userId
            userFetcher.username = it.username
            userFetcher.avatar = it.avatar?:""
        }
    }
}

