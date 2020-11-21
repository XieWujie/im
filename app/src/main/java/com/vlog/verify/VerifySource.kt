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

    private val liveData = MutableLiveData<List<VerifyWithUser>>()

    @AutoWire
    lateinit var gson: Gson

    @AutoWire
    lateinit var verifyDao: VerifyDao

    @AutoWire
    lateinit var friendDao: FriendDao

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
            if(it.state == Verify.agree){
                val conversationId = it.verifyInfo.toInt()
                val friend = Friend(verify.user,conversationId,Owner().userId)
                friendDao.insert(friend)
            }
        }
    }

    fun findVerifyMessage(userId: Int): LiveData<Result<List<Verify>>> {
        val url = "$HOST/verify/get?userId=$userId"
        val request = Request.Builder()
            .url(url)
            .get()
            .build()
        return request.toLiveData(getType(List::class.java, Verify::class.java)){
            if(it.isNotEmpty())
            verifyDao.insert(it)
            obsVerifyList(userId)
        }
    }

    fun obsVerifyList(userId: Int):LiveData<List<VerifyWithUser>>{
        pushExecutors {
            val newList = verifyDao.getFromUserTo(userId).map {
                VerifyWithUser(it,userSource.findUser(userId))
            }
            liveData.postValue(newList)
        }
        return liveData
    }
}