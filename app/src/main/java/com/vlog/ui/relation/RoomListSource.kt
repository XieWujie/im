package com.vlog.ui.relation

import androidx.lifecycle.LiveData
import com.common.HOST_PORT
import com.common.Result
import com.common.ext.getType
import com.common.ext.toLiveData
import com.dibus.AutoWire
import com.dibus.Service
import com.vlog.database.Room
import com.vlog.database.RoomDao
import okhttp3.Request

@Service
class RoomListSource {

    @AutoWire
    lateinit var roomDao: RoomDao



    fun requestFromNet(userId:Int):LiveData<Result<List<Room>>>{
        val url = "$HOST_PORT/room/get?userId=$userId"
        val request = Request.Builder()
            .url(url)
            .get()
            .build()
        return request.toLiveData(getType(List::class.java,Room::class.java)) {
            roomDao.insert(it)
        }
    }

    fun obsRooms(userId: Int) = roomDao.getRooms()

}