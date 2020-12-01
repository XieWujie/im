package com.vlog.search

import androidx.lifecycle.LiveData
import com.common.HOST_PORT
import com.common.Result
import com.common.ext.getType
import com.common.ext.toLiveData
import com.dibus.Service
import com.vlog.database.User
import okhttp3.Request

@Service
class FindSource {



    fun searchUser(key:String):LiveData<Result<List<User>>>{
        val url = "$HOST_PORT/user/find?key=$key"
        val request = Request.Builder()
            .url(url)
            .get()
            .build()
        return request.toLiveData(getType(List::class.java, User::class.java))
    }
}

