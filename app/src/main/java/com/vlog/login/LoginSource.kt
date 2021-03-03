package com.vlog.login

import androidx.lifecycle.LiveData
import com.common.HOST_PORT
import com.common.Result
import com.common.ext.toLiveData
import com.dibus.AutoWire
import com.dibus.Service
import com.google.gson.Gson
import com.vlog.conversation.ConversationSource
import com.vlog.ui.relation.RelationSource
import com.vlog.ui.relation.RoomListSource
import com.vlog.user.Owner
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

@Service
class LoginSource {
    
    @AutoWire
    lateinit var gson: Gson

    @AutoWire
    lateinit var roomListSource: RoomListSource

    @AutoWire
    lateinit var conversationSource: ConversationSource

    @AutoWire
    lateinit var relationSource: RelationSource


    fun login(username:String, password:String):LiveData<Result<LoginResponse>>{
        val entity = LoginEntity(username,password)
        val body = gson.toJson(entity)
        val request = Request.Builder()
            .url("$HOST_PORT/user/login")
            .post(body.toRequestBody())
            .build()
        return request.toLiveData{
            Owner().init(it)
            try {
                roomListSource.requestRoomListSyn(it.userId)
                relationSource.getRelationsSyc(it.userId)
                conversationSource.getRecentMessageByNet(it.userId)
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    data class LoginEntity(val username: String,val password: String)
}
