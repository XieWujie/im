package com.vlog.conversation.room

import androidx.lifecycle.LiveData
import com.common.HOST
import com.common.Result
import com.common.ext.getType
import com.common.ext.toLiveData
import com.vlog.database.User
import okhttp3.Request


class RoomMembersSource {

    fun getMembers(conversationId:Int):LiveData<Result<List<User>>>{
        val url = "$HOST/conversation/getMembers?conversationId=$conversationId"
        val request = Request.Builder()
            .url(url)
            .get()
            .build()
        return request.toLiveData(getType(List::class.java,User::class.java))
    }


}
