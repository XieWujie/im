package com.vlog.user

import androidx.lifecycle.LiveData
import com.common.HOST
import com.common.Result
import com.common.ext.toLiveData
import com.dibus.Service
import okhttp3.Request

@Service
class UserSource {

    fun checkRelation(ownerId:Int,userId:Int):LiveData<Result<RelationBody>>{
        val url = "$HOST/user/relation?ownerId=$ownerId&&userId=$userId"
        val request = Request.Builder()
            .url(url)
            .get()
            .build()
        return request.toLiveData()
    }
}

data class RelationBody(val isFriend:Boolean,val conversationId:Int)