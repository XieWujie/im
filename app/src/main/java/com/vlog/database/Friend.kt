package com.vlog.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Entity(primaryKeys = ["userId"])
class Friend(@Embedded val user: User,
             val conversationId:Int,var ownerId:Int)

@Dao
interface FriendDao{

    @Query("select * from friend where ownerId=:userId")
    fun getFriend(userId:Int): LiveData<List<Friend>>


    @Query("select * from friend where userId=:userId")
    fun getFromUerId(userId: Int):LiveData<Friend>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(friend: Friend)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(friends :List<Friend>)
}