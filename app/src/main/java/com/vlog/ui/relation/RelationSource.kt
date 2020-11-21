package com.vlog.ui.relation

import androidx.lifecycle.LiveData
import com.common.HOST
import com.common.Result
import com.common.ext.getType
import com.common.ext.toLiveData
import com.dibus.AutoWire
import com.dibus.Service
import com.vlog.database.Friend
import com.vlog.database.FriendDao
import com.vlog.user.Owner
import okhttp3.Request


@Service
class RelationSource {

    @AutoWire
    lateinit var dao: FriendDao

    fun getRelations(userId:Int):LiveData<Result<List<Friend>>>{
        val url = "$HOST/relation/get?userId=$userId"
        val request = Request.Builder()
            .url(url)
            .get()
            .build()
        return request.toLiveData(getType(List::class.java,Friend::class.java)){ list ->
            list.forEach {
                it.ownerId = Owner().userId
            }
            dao.insert(list)
        }
    }

    fun friendListen(userId: Int) = dao.getFriend(userId)
}