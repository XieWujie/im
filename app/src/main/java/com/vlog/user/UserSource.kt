package com.vlog.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.common.HOST_PORT
import com.common.Result
import com.common.ext.sync
import com.common.ext.toLiveData
import com.common.pushExecutors
import com.dibus.AutoWire
import com.dibus.DiBus
import com.dibus.Service
import com.google.gson.Gson
import com.vlog.App
import com.vlog.database.*
import com.vlog.photo.ImgSource
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

@Service
class UserSource {

    @AutoWire
    lateinit var dao: FriendDao

    @AutoWire
    lateinit var userDao: UserDao

    @AutoWire
    lateinit var gson: Gson

    @AutoWire
    lateinit var imgSource: ImgSource

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
        val url  = "$HOST_PORT/user/getById?userId=$userId"
        val request = Request.Builder()
            .url(url)
            .get()
            .build()
       return request.sync()
    }

    fun changeAvatar(path:String,user: User):LiveData<Result<String>>{
        val liveData = MutableLiveData<Result<String>>()
        pushExecutors {
            try {
                val url =  imgSource.upLoad(path)
                val newUser = user.copy(avatar = url)
                val request = buildUserRequest(newUser)
                 request.sync<String?>()
                liveData.postValue(Result.Data(url))
            }catch (e :Exception){
                liveData.postValue(Result.Error(e))
            }

        }
        return liveData
    }


    private fun buildUserRequest(user: User):Request{
        val json = gson.toJson(user)
        val url = "$HOST_PORT/user/update"
        return Request.Builder()
            .url(url)
            .post(json.toRequestBody())
            .build()
    }

    fun userUpdate(user: User):LiveData<Result<String>>{
       val request = buildUserRequest(user)
        return request.toLiveData()
    }

    fun logout(userId: Int):LiveData<Result<String>>{
        val url = "$HOST_PORT/user/logout?userId=$userId"
        val request = Request.Builder()
            .url(url)
            .get()
            .build()
        Owner().logout()
        return request.toLiveData{
            val roomDatabase = DbApp.get(App.get())
            roomDatabase.clearAllTables()
        }
    }

}

