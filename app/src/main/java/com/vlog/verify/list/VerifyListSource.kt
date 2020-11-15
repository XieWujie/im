package com.vlog.verify.list

import androidx.lifecycle.LiveData
import com.common.HOST
import com.common.Result
import com.common.ext.getType
import com.common.ext.toLiveData
import com.dibus.Service
import com.vlog.database.Message
import com.vlog.database.MsgWithUser
import okhttp3.Request

@Service
class VerifyListSource {


    fun findVerifyMessage(userId:Int):LiveData<Result<List<MsgWithUser>>>{
        val url = "$HOST/message/get?messageType=${Message.SendVerifyAdd}&&destination=$userId"
        val request = Request.Builder()
            .url(url)
            .get()
            .build()
        return request.toLiveData(getType(List::class.java, MsgWithUser::class.java))
    }
}