package com.vlog.verify

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.common.HOST
import com.common.Result
import com.common.ext.getType
import com.common.ext.toLiveData
import com.common.pushExecutors
import com.dibus.AutoWire
import com.dibus.Service
import com.google.gson.Gson
import com.vlog.database.*
import com.vlog.user.Owner
import com.vlog.user.UserSource
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

@Service
class VerifySource {


    @AutoWire
    lateinit var gson: Gson

    @AutoWire
    lateinit var verifyDao: VerifyDao

    @AutoWire
    lateinit var friendDao: FriendDao

    @AutoWire
    lateinit var userDao: UserDao

    @AutoWire lateinit var userSource: UserSource

    fun sendVerify(verify:VerifyWithUser):LiveData<Result<Verify>>{
        val url = "$HOST/verify/send"
        val json = gson.toJson(verify.verify)
        val request = Request.Builder()
            .url(url)
            .post(json.toRequestBody())
            .build()
        return request.toLiveData(){
            verifyDao.insert(it)
        }
    }

    fun findVerifyMessage(userId: Int): LiveData<Result<List<VerifyWithUser>>> {
        val url = "$HOST/verify/get?userId=$userId"
        val request = Request.Builder()
            .url(url)
            .get()
            .build()
        return request.toLiveData(getType(List::class.java, VerifyWithUser::class.java)){
            val users = it.map { it.user }
            userDao.insert(users)
            val verifyList = it.map { it.verify }
            verifyDao.insert(verifyList)
        }
    }

    fun obsVerifyList(userId: Int):LiveData<List<VerifyWithUser>>{
        return verifyDao.getFromUserTo(userId)
    }
}