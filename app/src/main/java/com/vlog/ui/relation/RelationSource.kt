package com.vlog.ui.relation

import android.util.Log
import androidx.lifecycle.LiveData
import com.common.HOST_PORT
import com.common.Result
import com.common.ext.getType
import com.common.ext.sync
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

    @AutoWire
    lateinit var relationSource: RelationSource

    fun getRelations(userId:Int):LiveData<Result<List<Friend>>>{
        val url = "$HOST_PORT/relation/get?userId=$userId"
        val request = Request.Builder()
            .url(url)
            .get()
            .build()
        return request.toLiveData(getType(List::class.java,Friend::class.java)){ list ->
            list.forEach {
                it.ownerId = Owner().userId
            }
            dao.insert(list)
            Log.d("relationShip",list.toString())
        }
    }

    fun getRelationsSyc(userId: Int){
        val url = "$HOST_PORT/relation/get?userId=$userId"
        val request = Request.Builder()
            .url(url)
            .get()
            .build()
        val list  = request.sync<List<Friend>>(getType(List::class.java,Friend::class.java))
        dao.insert(list)
    }

    fun friendListen(userId: Int) = dao.getFriend(userId)
}