package com.vlog.conversation.room

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.common.HOST
import com.common.Result
import com.common.ext.sync
import com.common.ext.toLiveData
import com.common.pushExecutors
import com.dibus.AutoWire
import com.dibus.Service
import com.google.gson.Gson
import com.vlog.avatar.ImgSource
import com.vlog.database.Room
import com.vlog.database.User
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

@Service
class RoomEditSource {

    @AutoWire
    lateinit var gson: Gson

    @AutoWire
    lateinit var imgSource:ImgSource

    fun update(room: Room):LiveData<Result<Room>>{
        val request = buildRequest(room)
        return request.toLiveData()
    }

   private fun buildRequest(room: Room):Request{
        val url = "$HOST/room/update"
        val body = gson.toJson(room).toRequestBody()
        return Request.Builder()
            .url(url)
            .post(body)
            .build()
    }

    fun changeAvatar(path:String,room: Room):LiveData<Result<Room>>{
        val liveData = MutableLiveData<Result<Room>>()
        pushExecutors {
            try {
                val url =  imgSource.upLoad(path)
                val newRoom = room.copy(roomAvatar = url)
                val request = buildRequest(newRoom)
                val room = request.sync<Room>()
                liveData.postValue(Result.Data(room))
            }catch (e :Exception){
                liveData.postValue(Result.Error(e))
            }

        }
        return liveData
    }
}