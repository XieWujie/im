package com.vlog.room

import android.util.Log
import androidx.lifecycle.LiveData
import com.common.HOST
import com.common.Result
import com.common.ext.toLiveData
import com.dibus.AutoWire
import com.dibus.Service
import com.google.gson.Gson
import com.vlog.database.MemberInfo
import com.vlog.database.Room
import com.vlog.user.Owner
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

@Service
class RoomCreateSource {

    @AutoWire
    lateinit var gson: Gson

    fun create(room: Room, users:List<Int>):LiveData<Result<Room>>{
        val newList = ArrayList<Int>()
        newList.addAll(users)
        newList.add(Owner().userId)
        val members = newList.map {
            MemberInfo(it,"")
        }
        val map = HashMap<String,Any>()
        map["room"] = room
        map["members"] = members
        val body = gson.toJson(map)
        Log.d("createRoom",body)
        val url = "$HOST/room/create"
        val request = Request.Builder()
            .url(url)
            .post(body.toRequestBody())
            .build()
        return request.toLiveData()
    }
}