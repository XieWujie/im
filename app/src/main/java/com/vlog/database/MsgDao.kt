package com.vlog.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MsgDao {

    @Query("select * from message where conversationId=:destination order by createAt")
    fun getByConversationId(destination:Int):List<Message>

    @Query("select message.*,user.* from message,user where conversationId=:destination and message.sendFrom==user.userId order by createAt")
    fun getLiveById(destination:Int):LiveData<List<MsgWithUser>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(message: Message)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(msgs: List<Message>)
}