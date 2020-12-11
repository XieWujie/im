package com.vlog.conversation.friend

import androidx.lifecycle.LiveData
import com.common.HOST_PORT
import com.common.Result
import com.common.ext.toLiveData
import com.dibus.AutoWire
import com.dibus.Service
import com.google.gson.Gson
import com.vlog.database.Friend
import com.vlog.database.FriendDao
import com.vlog.database.Room
import com.vlog.database.RoomDao
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

@Service
class FriendSource {

    @AutoWire
    lateinit var friendDao: FriendDao

    @AutoWire
    lateinit var gson: Gson

    fun updateCustomerFriendBg(userId: Int, friend:Friend, file: File): LiveData<Result<HashMap<String, String>>> {
        val body = file.asRequestBody(contentType = "image/png".toMediaTypeOrNull())
        val map = HashMap<String,Any>()
        map["ownerId"] = userId
        map["conversationId"] = friend.conversationId
        val json = gson.toJson(map)
        val url = "$HOST_PORT/friend/background"
        val requestBody = MultipartBody.Builder()
            .addFormDataPart("file",file.name,body)
            .addFormDataPart("json",json)
            .setType("multipart/form-data".toMediaTypeOrNull()!!)
            .build()
        val req = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()
        return req.toLiveData {
            friend.background = it["background"]
            friendDao.insert(friend)
        }
    }

    fun friendNotify(friend: Friend, notify:Boolean,ownerId:Int):LiveData<Result<Any?>>{
        val map = HashMap<String,Any>()
        map["conversationId"] = friend.conversationId
        map["notify"] = notify
        map["ownerId"] = ownerId
        val json = gson.toJson(map)
        val request  = Request.Builder()
            .url("$HOST_PORT/friend/notify")
            .post(json.toRequestBody())
            .build()
        return request.toLiveData(){
            friend.notify = notify
            friendDao.insert(friend)
        }
    }

    fun updateFriendMarkName(ownerId: Int,friend: Friend):LiveData<Result<Any?>>{
        val  map = HashMap<String,Any>()
        map["ownerId"] = ownerId
        map["conversationId"] = friend.conversationId
        map["markName"] = friend.markName!!
        val url = "$HOST_PORT/friend/markName"
        val req = Request.Builder()
            .url(url)
            .post(gson.toJson(map).toRequestBody())
            .build()
        return req.toLiveData {
            friendDao.insert(friend)
        }
    }
}