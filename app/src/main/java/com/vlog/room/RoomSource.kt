package com.vlog.room

import android.util.Log
import androidx.lifecycle.LiveData
import com.common.HOST_PORT
import com.common.Result
import com.common.ext.getType
import com.common.ext.toLiveData
import com.dibus.AutoWire
import com.dibus.Service
import com.google.gson.Gson
import com.vlog.database.MemberInfo
import com.vlog.database.Room
import com.vlog.database.RoomDao
import com.vlog.database.User
import com.vlog.user.Owner
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

@Service
class RoomSource {


    @AutoWire
    lateinit var gson: Gson

    @AutoWire
    lateinit var roomDao: RoomDao

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
        val url = "$HOST_PORT/room/create"
        val request = Request.Builder()
            .url(url)
            .post(body.toRequestBody())
            .build()
        return request.toLiveData()
    }

    fun addUser(conversationId:Int,users: List<Int>):LiveData<Result<Any?>>{
        val members = users.map {
            MemberInfo(it,"")
        }
        val map = HashMap<String,Any>()
        map["conversationId"] = conversationId
        map["members"] = members
        val body = gson.toJson(map)
        val url = "$HOST_PORT/room/memberAdd"
        val request = Request.Builder()
            .url(url)
            .post(body.toRequestBody())
            .build()
        return request.toLiveData()
    }

    fun quitRoom(room: Room,userId:Int):LiveData<Result<Any?>>{
        val map = HashMap<String,Any>()
        map["conversationId"] = room.conversationId
        map["userId"] = userId
        val body = gson.toJson(map)
        val url = "$HOST_PORT/room/quit"
        val request = Request.Builder()
            .url(url)
            .post(body.toRequestBody())
            .build()
        return request.toLiveData(){
            roomDao.delete(room)
        }
    }

    fun getMembers(conversationId:Int):LiveData<Result<List<User>>>{
        val url = "$HOST_PORT/conversation/getMembers?conversationId=$conversationId"
        val request = Request.Builder()
            .url(url)
            .get()
            .build()
        return request.toLiveData(getType(List::class.java, User::class.java))
    }

    fun roomNotify(room: Room, notify:Boolean,ownerId:Int):LiveData<Result<Any?>>{
        val map = HashMap<String,Any>()
        map["conversationId"] = room.conversationId
        map["notify"] = notify
        map["ownerId"] = ownerId
        val json = gson.toJson(map)
        val request  = Request.Builder()
            .url("$HOST_PORT/room/notify")
            .post(json.toRequestBody())
            .build()
        return request.toLiveData(){
            val newRoom = room.copy(notify = notify)
            roomDao.insert(newRoom)
        }
    }
}