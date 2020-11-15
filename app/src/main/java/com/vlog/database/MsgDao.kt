package com.vlog.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MsgDao {

    @Query("select * from msgwithuser where destination=:destination order by createAt")
    fun getFromDestination(destination:Int):LiveData<List<MsgWithUser>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(msgWithUser: MsgWithUser)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(msgWithUser: List<MsgWithUser>)
}