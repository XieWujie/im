package com.vlog.ui.relation

import androidx.lifecycle.LiveData
import com.common.HOST
import com.common.Result
import com.common.ext.getType
import com.common.ext.toLiveData
import com.dibus.Service
import com.vlog.database.User
import okhttp3.Request


data class UserWithCov(val user: User, val conversationId:Int)
@Service
class RelationSource {


    fun getRelations(userId:Int):LiveData<Result<List<UserWithCov>>>{
        val url = "$HOST/relation/get?userId=$userId"
        val request = Request.Builder()
            .url(url)
            .get()
            .build()
        return request.toLiveData(getType(List::class.java,UserWithCov::class.java))
    }
}