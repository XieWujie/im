package com.vlog.user

import androidx.lifecycle.LiveData
import com.common.HOST
import com.common.Result
import com.common.ext.sync
import com.common.ext.toLiveData
import com.dibus.AutoWire
import com.dibus.Service
import com.vlog.database.Friend
import com.vlog.database.FriendDao
import com.vlog.database.User
import com.vlog.database.UserDao
import okhttp3.Request

@Service
class UserSource {

    @AutoWire
    lateinit var dao: FriendDao

    @AutoWire
    lateinit var userDao: UserDao

    fun checkRelation(userId:Int):LiveData<Friend>{
        return dao.getFromUerId(userId)
    }

    fun insert(user:User){
        userDao.insert(user)
    }

    fun findUser(userId: Int):User{
       return userDao.getUser(userId)?:findByNet(userId).also {
           userDao.insert(it)
       }
    }

    fun findByNet(userId: Int):User{
        val url  = "$HOST/user/getById?userId=$userId"
        val request = Request.Builder()
            .url(url)
            .get()
            .build()
       return request.sync()
    }
}

